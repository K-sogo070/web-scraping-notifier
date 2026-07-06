package com.usiwakaman.portfolio.service;

import com.usiwakaman.portfolio.entity.MonitorTarget;
import com.usiwakaman.portfolio.entity.ScrapedItem;
import com.usiwakaman.portfolio.repository.MonitorTargetRepository;
import com.usiwakaman.portfolio.repository.ScrapedItemRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CrawlerService {

    private static final Logger log = LoggerFactory.getLogger(CrawlerService.class);
    private static final String USER_AGENT = "PortfolioBot/1.0 (+contact: your-email@example.com)";

    private final MonitorTargetRepository monitorTargetRepository;
    private final ScrapedItemRepository scrapedItemRepository;
    private final NotificationService notificationService;
    private final RobotsTxtChecker robotsTxtChecker;

    public CrawlerService(MonitorTargetRepository monitorTargetRepository,
                        ScrapedItemRepository scrapedItemRepository,
                        NotificationService notificationService,
                        RobotsTxtChecker robotsTxtChecker) {
        this.monitorTargetRepository = monitorTargetRepository;
        this.scrapedItemRepository = scrapedItemRepository;
        this.notificationService = notificationService;
        this.robotsTxtChecker = robotsTxtChecker;
    }

    @Scheduled(fixedRate = 3600000) // 3,600,000ミリ秒 = 1時間ごとに実行
    public void crawlAll() {
        List<MonitorTarget> targets = monitorTargetRepository.findByIsActiveTrue();
        
        for (MonitorTarget target : targets) {
            try {
                crawlSingleTarget(target);
                // 相手サーバーへの負荷軽減：1サイト処理するごとに必ず待つ
                Thread.sleep(1000);
            } catch (Exception e) {
                // 1サイトで失敗しても全体は止めず、ログを残して次に進む
                log.error("クロール失敗: site={}, error={}", target.getSiteName(), e.getMessage());
            }
        }
    }

    private void crawlSingleTarget(MonitorTarget target) throws Exception {

        if (!robotsTxtChecker.isAllowed(target.getBaseUrl())) {
            log.warn("robots.txtにより巡回スキップ: {}", target.getSiteName());
            return;
        }

        Document doc = Jsoup.connect(target.getBaseUrl())
        .userAgent(USER_AGENT)
        .timeout(5000)
        .get();

        log.info("検出された文字コード: {}", doc.charset());

        Elements elements = doc.select(target.getCssSelector());

        for (Element el : elements) {
            Element linkEl = el.selectFirst("a");
            Element titleEl = el.selectFirst("p.tit");

            if (linkEl == null || titleEl == null) {
                continue; // 想定外の構造の要素はスキップ
            }

            String title = titleEl.text();
            String itemUrl = linkEl.absUrl("href");
            String contentHash = sha256(title + itemUrl);

            Optional<ScrapedItem> existing = scrapedItemRepository
                    .findByMonitorTarget_IdAndContentHash(target.getId(), contentHash);

            if (existing.isEmpty()) {
                ScrapedItem newItem = new ScrapedItem();
                newItem.setMonitorTarget(target);
                newItem.setTitle(title);
                newItem.setItemUrl(itemUrl);
                newItem.setContentHash(contentHash);
                newItem.setFetchedAt(LocalDateTime.now());
                newItem.setNotified(false);

                ScrapedItem saved = scrapedItemRepository.save(newItem);
                log.info("新着検知: {}", title);

                notificationService.notifyNewItem(saved);
            }
        }
    }

    private String sha256(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
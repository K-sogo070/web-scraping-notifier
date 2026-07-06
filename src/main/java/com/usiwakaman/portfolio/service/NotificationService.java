package com.usiwakaman.portfolio.service;

import com.usiwakaman.portfolio.entity.NotificationLog;
import com.usiwakaman.portfolio.entity.ScrapedItem;
import com.usiwakaman.portfolio.repository.NotificationLogRepository;
import com.usiwakaman.portfolio.repository.ScrapedItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationLogRepository notificationLogRepository;
    private final ScrapedItemRepository scrapedItemRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${notification.slack.webhook-url}")
    private String slackWebhookUrl;

    public NotificationService(NotificationLogRepository notificationLogRepository,
                                ScrapedItemRepository scrapedItemRepository) {
        this.notificationLogRepository = notificationLogRepository;
        this.scrapedItemRepository = scrapedItemRepository;
    }

    @Async
    public void notifyNewItem(ScrapedItem item) {
        String channel = "slack";
        String status;
        String errorMessage = null;

        try {
            sendToSlack(item);
            status = "success";

            item.setNotified(true);
            scrapedItemRepository.save(item);

        } catch (Exception e) {
            status = "failed";
            errorMessage = e.getMessage();
            log.error("通知失敗: item={}, error={}", item.getTitle(), e.getMessage());
        }

        NotificationLog logEntry = new NotificationLog();
        logEntry.setScrapedItem(item);
        logEntry.setChannel(channel);
        logEntry.setStatus(status);
        logEntry.setSentAt(LocalDateTime.now());
        logEntry.setErrorMessage(errorMessage);
        notificationLogRepository.save(logEntry);
    }

    private void sendToSlack(ScrapedItem item) throws Exception {
        String messageText = String.format("📰 新着情報を検知しました\n*%s*\n%s",
                item.getTitle(), item.getItemUrl());

        Map<String, String> payload = Map.of("text", messageText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(slackWebhookUrl, request, String.class);
        log.info("Slackへ通知送信成功: {}", item.getTitle());
    }
}
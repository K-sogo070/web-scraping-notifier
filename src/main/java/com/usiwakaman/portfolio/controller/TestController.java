package com.usiwakaman.portfolio.controller;

import com.usiwakaman.portfolio.service.CrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final CrawlerService crawlerService;

    public TestController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
    }

    @GetMapping("/test/crawl")
    public String testCrawl() {
        crawlerService.crawlAll();
        return "クロール処理を実行しました。コンソールログを確認してください。";
    }
}
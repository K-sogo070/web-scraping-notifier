package com.usiwakaman.portfolio.dto;

import java.time.LocalDateTime;

public class ScrapedItemDto {

    private Long id;
    private String siteName;
    private String title;
    private String itemUrl;
    private LocalDateTime fetchedAt;

    public ScrapedItemDto(Long id, String siteName, String title, String itemUrl, LocalDateTime fetchedAt) {
        this.id = id;
        this.siteName = siteName;
        this.title = title;
        this.itemUrl = itemUrl;
        this.fetchedAt = fetchedAt;
    }

    public Long getId() { return id; }
    public String getSiteName() { return siteName; }
    public String getTitle() { return title; }
    public String getItemUrl() { return itemUrl; }
    public LocalDateTime getFetchedAt() { return fetchedAt; }
}
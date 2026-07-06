package com.usiwakaman.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "monitor_targets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitorTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "site_name", nullable = false, length = 100)
    private String siteName;

    @Column(name = "base_url", nullable = false, length = 500)
    private String baseUrl;

    @Column(name = "css_selector", nullable = false, length = 255)
    private String cssSelector;

    @Column(name = "crawl_interval_minutes", nullable = false)
    private Integer crawlIntervalMinutes = 60;

    @Column(name = "robots_txt_compliant", nullable = false)
    private Boolean robotsTxtCompliant = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
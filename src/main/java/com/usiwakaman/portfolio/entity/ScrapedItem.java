package com.usiwakaman.portfolio.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "scraped_items",
    uniqueConstraints = @UniqueConstraint(columnNames = {"monitor_target_id", "content_hash"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitor_target_id", nullable = false)
    private MonitorTarget monitorTarget;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    @Column(name = "item_url", nullable = false, length = 500)
    private String itemUrl;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt = LocalDateTime.now();

    @Column(name = "notified", nullable = false)
    private Boolean notified = false;
}
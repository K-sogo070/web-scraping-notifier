package com.usiwakaman.portfolio.repository;

import com.usiwakaman.portfolio.dto.ScrapedItemDto;
import com.usiwakaman.portfolio.entity.ScrapedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ScrapedItemRepository extends JpaRepository<ScrapedItem, Long> {

    Optional<ScrapedItem> findByMonitorTarget_IdAndContentHash(Long monitorTargetId, String contentHash);

    List<ScrapedItem> findByNotifiedFalse();

    @Query("SELECT new com.usiwakaman.portfolio.dto.ScrapedItemDto(" +
           "s.id, s.monitorTarget.siteName, s.title, s.itemUrl, s.fetchedAt) " +
           "FROM ScrapedItem s ORDER BY s.fetchedAt DESC")
    List<ScrapedItemDto> findAllForDashboard();
}
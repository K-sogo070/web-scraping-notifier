package com.usiwakaman.portfolio.repository;

import com.usiwakaman.portfolio.entity.ScrapedItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ScrapedItemRepository extends JpaRepository<ScrapedItem, Long> {

    // 差分検知で使う：同じ監視対象・同じハッシュ値のレコードが既に存在するか確認
    Optional<ScrapedItem> findByMonitorTarget_IdAndContentHash(Long monitorTargetId, String contentHash);

    // まだ通知していない新着記事を取得
    List<ScrapedItem> findByNotifiedFalse();
}
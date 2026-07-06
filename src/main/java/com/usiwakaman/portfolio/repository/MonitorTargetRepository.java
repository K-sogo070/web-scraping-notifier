package com.usiwakaman.portfolio.repository;

import com.usiwakaman.portfolio.entity.MonitorTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitorTargetRepository extends JpaRepository<MonitorTarget, Long> {

    List<MonitorTarget> findByIsActiveTrue();
}
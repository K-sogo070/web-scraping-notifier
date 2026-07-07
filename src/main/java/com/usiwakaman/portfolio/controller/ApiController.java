package com.usiwakaman.portfolio.controller;

import com.usiwakaman.portfolio.dto.ScrapedItemDto;
import com.usiwakaman.portfolio.repository.ScrapedItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final ScrapedItemRepository scrapedItemRepository;

    public ApiController(ScrapedItemRepository scrapedItemRepository) {
        this.scrapedItemRepository = scrapedItemRepository;
    }

    @GetMapping("/items")
    public List<ScrapedItemDto> getItems() {
        return scrapedItemRepository.findAllForDashboard();
    }
}
package com.usiwakaman.portfolio.service;

import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class RobotsTxtChecker {

    private static final Logger log = LoggerFactory.getLogger(RobotsTxtChecker.class);
    private static final String USER_AGENT = "PortfolioBot";

    public boolean isAllowed(String targetUrl) {
        try {
            URI uri = URI.create(targetUrl);
            String robotsUrl = uri.getScheme() + "://" + uri.getHost() + "/robots.txt";

            String robotsTxt = Jsoup.connect(robotsUrl)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .ignoreContentType(true)
                    .get()
                    .text();

            List<String> disallowedPaths = parseDisallowedPaths(robotsTxt);
            String path = uri.getPath().isEmpty() ? "/" : uri.getPath();

            for (String disallowed : disallowedPaths) {
                if (!disallowed.isEmpty() && path.startsWith(disallowed)) {
                    log.warn("robots.txtにより禁止: url={}, disallowed={}", targetUrl, disallowed);
                    return false;
                }
            }
            return true;

        } catch (Exception e) {
            // robots.txtが存在しない、または取得できない場合は「許可」として扱う一般的な慣習に従う
            log.info("robots.txt取得不可のため許可扱い: url={}, reason={}", targetUrl, e.getMessage());
            return true;
        }
    }

    private List<String> parseDisallowedPaths(String robotsTxt) {
        List<String> paths = new ArrayList<>();
        boolean relevantSection = false;

        for (String line : robotsTxt.split("\n")) {
            line = line.trim();
            if (line.toLowerCase().startsWith("user-agent:")) {
                String agent = line.substring(11).trim();
                relevantSection = agent.equals("*");
            } else if (relevantSection && line.toLowerCase().startsWith("disallow:")) {
                paths.add(line.substring(9).trim());
            }
        }
        return paths;
    }
}
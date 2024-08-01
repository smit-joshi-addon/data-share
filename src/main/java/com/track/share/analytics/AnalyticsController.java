package com.track.share.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/{uuid}")
    public ResponseEntity<AnalyticsDTO> getAnalyticsById(@PathVariable String uuid) {
        AnalyticsDTO analyticsDTO = analyticsService.getAnalyticsById(uuid);
        return new ResponseEntity<>(analyticsDTO, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsDTO>> getAllAnalytics() {
        List<AnalyticsDTO> analyticsList = analyticsService.getAllAnalytics();
        return new ResponseEntity<>(analyticsList, HttpStatus.OK);
    }
    
}


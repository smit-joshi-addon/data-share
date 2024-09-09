package com.track.share.analytics;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class AnalyticsScheduler {

    @Autowired
    private AnalyticsService analyticsService;

    private final Set<SseEmitter> emitters = Collections.synchronizedSet(new HashSet<>());
    private final AtomicBoolean running = new AtomicBoolean(false);

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        // Start the scheduled task if not running
        if (running.compareAndSet(false, true)) {
            startScheduledTask();
        }
    }

    @Scheduled(fixedRate = 1000) // Execute every second
    public void fetchData() {
        if (!running.get()) {
            return;
        }

        ResponseEntity<List<AnalyticsData>> response = ResponseEntity.ok(analyticsService.getAllAnalytics());
        synchronized (emitters) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event().data(response.getBody()));
                } catch (IOException e) {
                    handleException(emitter, e);
                }
            }
        }
    }

    private void handleException(SseEmitter emitter, Exception e) {
        try {
            emitter.completeWithError(e);
        } finally {
            emitters.remove(emitter);
        }
    }

    private void startScheduledTask() {
        // Ensure the scheduler is running
        if (running.compareAndSet(false, true)) {
            fetchData(); // Trigger an immediate fetch
        }
    }

    public void stopScheduledTask() {
        running.set(false);
    }

    public boolean isRunning() {
        return running.get();
    }
}

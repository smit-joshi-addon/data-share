package com.track.share.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AnalyticsScheduler {

	@Autowired
	private RestTemplate restTemplate;

	private final String baseUrl = "http://localhost:8080"; // Externalize this to a configuration file
	private final String sseEndpointUrl = baseUrl + "/api/analytics";

	private final AtomicReference<SseEmitter> currentEmitter = new AtomicReference<>();
	private final AtomicReference<String> currentToken = new AtomicReference<>();
	private final AtomicBoolean running = new AtomicBoolean(false);

	public void startScheduledTask(SseEmitter emitter, String token) {
		if (running.getAndSet(true)) {
			throw new IllegalStateException("Scheduler is already running");
		}
		currentEmitter.set(emitter);
		currentToken.set(token);
	}

	@Scheduled(fixedRate = 1000) // Execute every second
	public void fetchData() {
		if (!running.get()) {
			return;
		}

		SseEmitter emitterToUse = currentEmitter.get();
		String tokenToUse = currentToken.get();

		if (emitterToUse == null || tokenToUse == null) {
			return;
		}

		ResponseEntity<List<?>> response;
		try {
			response = restTemplate.exchange(sseEndpointUrl, HttpMethod.GET,
					new HttpEntity<>(createHeaders(tokenToUse)), new ParameterizedTypeReference<>() {
					});
		} catch (Exception e) {
			handleException(emitterToUse, e);
			return;
		}

		try {
			emitterToUse.send(SseEmitter.event().data(response.getBody()));
		} catch (IOException e) {
			handleException(emitterToUse, e);
		}
	}

	private void handleException(SseEmitter emitter, Exception e) {
		if (emitter != null) {
			try {
				emitter.completeWithError(e);
			} finally {
				// Clean up
				running.set(false);
				currentEmitter.set(null);
				currentToken.set(null);
			}
		}
	}

	private HttpHeaders createHeaders(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		return headers;
	}
}

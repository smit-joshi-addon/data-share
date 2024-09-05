package com.track.share.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

	@Autowired
	private AnalyticsScheduler analyticsScheduler;

	@GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter streamData(@RequestParam("token") String token) {
		SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
		analyticsScheduler.startScheduledTask(emitter, token);
		emitter.onError(emitter::completeWithError);
		return emitter;
	}
}

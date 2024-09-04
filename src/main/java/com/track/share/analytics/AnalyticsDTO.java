package com.track.share.analytics;

import java.time.ZonedDateTime;

public record AnalyticsDTO(String uuid, String businessId, String methodName, ZonedDateTime calledAt,
		String calledByIp) {
}

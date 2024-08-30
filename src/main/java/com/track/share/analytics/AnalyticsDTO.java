package com.track.share.analytics;

import java.time.OffsetDateTime;

public record AnalyticsDTO(String uuid, String businessId, String methodName, OffsetDateTime calledAt,
		String calledByIp) {
}

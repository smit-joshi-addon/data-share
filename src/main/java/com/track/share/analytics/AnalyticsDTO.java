package com.track.share.analytics;

import java.time.LocalDateTime;

public record AnalyticsDTO(
		 String uuid,
		 String businessId,
		 String methodName,
		 LocalDateTime calledAt,
		 String calledByIp
) {}

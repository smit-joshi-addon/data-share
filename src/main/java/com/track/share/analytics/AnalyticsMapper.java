package com.track.share.analytics;

import org.springframework.stereotype.Component;

import com.track.share.business.Business;

@Component
class AnalyticsMapper {

	// Converts Analytics entity to AnalyticsDTO
	public AnalyticsDTO toDto(Analytics analytics) {
		return new AnalyticsDTO(analytics.getUuid(),
				analytics.getBusiness() != null ? analytics.getBusiness().getBusinessId().toString() : null,
				analytics.getMethodName(), analytics.getCalledAt(), analytics.getCalledByIp());
	}

	// Converts AnalyticsDTO to Analytics entity
	public Analytics toEntity(AnalyticsDTO analyticsDTO) {
		Analytics analytics = new Analytics();
		analytics.setUuid(analyticsDTO.uuid());
		analytics.setBusiness(Business.builder().businessId(Integer.valueOf(analyticsDTO.businessId())).build());
		analytics.setMethodName(analyticsDTO.methodName());
		analytics.setCalledAt(analyticsDTO.calledAt());
		analytics.setCalledByIp(analyticsDTO.calledByIp());
		return analytics;
	}
}

package com.track.share.analytics;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.track.share.business.Business;
import com.track.share.business.BusinessService;
import com.track.share.utility.Utility;

@Service
class AnalyticsServiceImpl implements AnalyticsService {

	@Autowired
	private AnalyticsRepository analyticsRepository;

	@Autowired
	private AnalyticsMapper analyticsMapper;

	@Autowired
	private BusinessService businessService;

	@Autowired
	private Utility utility;

	@Override
	public AnalyticsDTO createAnalytics(String methodName, String calleByIp) {
		Business business = businessService.getBusinessByUsername(utility.getCurrentUsername());
		Analytics analytics = Analytics.builder().methodName(methodName).calledByIp(calleByIp).business(business)
				.build();
		Analytics savedAnalytics = analyticsRepository.save(analytics);
		return analyticsMapper.toDto(savedAnalytics);
	}

	@Override
	public AnalyticsDTO getAnalyticsById(String uuid) {
		Analytics analytics = analyticsRepository.findById(uuid)
				.orElseThrow(() -> new RuntimeException("Analytics not found"));
		return analyticsMapper.toDto(analytics);
	}

	@Override
	public List<AnalyticsDTO> getAllAnalytics() {
		return analyticsRepository.findAll().stream().map(analyticsMapper::toDto).collect(Collectors.toList());
	}

	@Override
	public void deleteAnalytics(String uuid) {
		analyticsRepository.deleteById(uuid);
	}
}

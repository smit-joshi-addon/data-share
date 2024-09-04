package com.track.share.pulldata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.track.share.analytics.AnalyticsService;

@Service
class DefaultDataPullService implements DataPullService {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDataPullService.class);

	@Autowired
	private AnalyticsService analyticsService;

	@Override
	public String getData(String calledByIp) {
		// Log the method entry and parameters
		logger.info("Entering getData() with calledByIp={}", calledByIp);

		// analytics
		analyticsService.createAnalytics("getData", calledByIp);

		// Log method exit with IP address
		logger.info("Exiting getData() with calledByIp={}", calledByIp);

		return "Pulling Data 1";
	}

	@Override
	public String getData2(String calledByIp) {
		// Log the method entry and parameters
		logger.info("Entering getData2() with calledByIp={}", calledByIp);

		// analytics
		analyticsService.createAnalytics("getData2", calledByIp);

		// Log method exit with IP address
		logger.info("Exiting getData2() with calledByIp={}", calledByIp);

		return "Pulling Data 2";
	}

	@Override
	public String getData3(String calledByIp) {
		// Log the method entry and parameters
		logger.info("Entering getData3() with calledByIp={}", calledByIp);

		// analytics
		analyticsService.createAnalytics("getData3", calledByIp);

		// Log method exit with IP address
		logger.info("Exiting getData3() with calledByIp={}", calledByIp);

		return "Pulling Data 3";
	}

	@Override
	public String getData4(String calledByIp) {
		// Log the method entry and parameters
		logger.info("Entering getData4() with calledByIp={}", calledByIp);

		// analytics
		analyticsService.createAnalytics("getData4", calledByIp);

		// Log method exit with IP address
		logger.info("Exiting getData4() with calledByIp={}", calledByIp);

		return "Pulling Data 4";
	}
}

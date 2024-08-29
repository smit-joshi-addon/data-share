package com.track.share.pulldata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.track.share.analytics.AnalyticsService;

@Service
class DefaultDataPullService implements DataPullService {

	private Logger logger = LoggerFactory.getLogger(DefaultDataPullService.class);

	@Autowired
	private AnalyticsService analyticsService;

	@Override
	public String getData(String calledByIp) {
		// logger
		logger.info("pulling data from getData()");
		// analytics
		analyticsService.createAnalytics("getData", calledByIp);

		return "Pulling Data";
	}

}

package com.track.share.analytics;

import java.util.List;

public record AnalyticsData(String methodName, List<Long> count, List<String> hours) {

}

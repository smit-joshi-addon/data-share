package com.track.share.analytics;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsRepositoryCustomImpl implements AnalyticsRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AnalyticsData> findAnalyticsData() {
        String queryStr = "SELECT a.methodName, " +
                "EXTRACT(HOUR FROM a.calledAt) AS hour, " +
                "COUNT(*) AS count " +
                "FROM Analytics a " +
                "GROUP BY a.methodName, EXTRACT(HOUR FROM a.calledAt) " +
                "ORDER BY a.methodName, EXTRACT(HOUR FROM a.calledAt)";

        Query query = entityManager.createQuery(queryStr);

        @SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
        return transformResults(results);
    }

    private List<AnalyticsData> transformResults(List<Object[]> results) {
        // Transform the raw results into AnalyticsData format
        List<AnalyticsData> analyticsDataList = new ArrayList<>();
        String currentMethodName = null;
        List<Long> counts = new ArrayList<>();
        List<String> hours = new ArrayList<>();

        for (Object[] result : results) {
            String methodName = (String) result[0];
            String hour = ((Number) result[1]).intValue() + ":00";
            Long count = ((Number) result[2]).longValue();

            if (currentMethodName == null) {
                currentMethodName = methodName;
            }

            if (!methodName.equals(currentMethodName)) {
                analyticsDataList.add(new AnalyticsData(currentMethodName, counts, hours));
                counts = new ArrayList<>();
                hours = new ArrayList<>();
                currentMethodName = methodName;
            }

            counts.add(count);
            hours.add(hour);
        }

        if (currentMethodName != null) {
            analyticsDataList.add(new AnalyticsData(currentMethodName, counts, hours));
        }

        return analyticsDataList;
    }
}

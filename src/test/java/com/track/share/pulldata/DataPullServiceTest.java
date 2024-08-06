package com.track.share.pulldata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.track.share.analytics.AnalyticsService;

public class DataPullServiceTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private DefaultDataPullService dataPullService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLogAndCallCreateAnalytics_whenGetDataIsCalled() {
        // Given
        String calledByIp = "127.0.0.1";

        // When
        String result = dataPullService.getData(calledByIp);

        // Then
        assertEquals("Pulling Data", result);
        verify(analyticsService).createAnalytics("getData", calledByIp);
    }
}

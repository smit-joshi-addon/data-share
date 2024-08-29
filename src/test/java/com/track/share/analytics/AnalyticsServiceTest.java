package com.track.share.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.track.share.business.Business;
import com.track.share.business.BusinessService;
import com.track.share.utility.Utility;

public class AnalyticsServiceTest {

	@Mock
	private AnalyticsRepository analyticsRepository;

	@Mock
	private AnalyticsMapper analyticsMapper;

	@Mock
	private BusinessService businessService;

	@Mock
	private Utility utility;

	@InjectMocks
	private AnalyticsServiceImpl analyticsService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldCreateAnalyticsAndReturnDTO() {
		// Given
		String methodName = "testMethod";
		String calledByIp = "127.0.0.1";
		String currentUsername = "testUser";
		Business business = Business.builder().businessId(1).build();
		Analytics analytics = Analytics.builder().uuid("1234").methodName(methodName).calledByIp(calledByIp)
				.business(business).build();
		AnalyticsDTO analyticsDTO = new AnalyticsDTO("1234", "1", methodName, analytics.getCalledAt(), calledByIp);

		when(utility.getCurrentUsername()).thenReturn(currentUsername);
		when(businessService.getBusinessByUsername(currentUsername)).thenReturn(business);
		when(analyticsRepository.save(any(Analytics.class))).thenReturn(analytics);
		when(analyticsMapper.toDto(any(Analytics.class))).thenReturn(analyticsDTO);

		// When
		AnalyticsDTO result = analyticsService.createAnalytics(methodName, calledByIp);

		// Then
		assertEquals(analyticsDTO, result);
		verify(analyticsRepository).save(any(Analytics.class));
	}

	@Test
	void shouldThrowRuntimeException_whenAnalyticsNotFoundById() {
		// Given
		String uuid = "1234";
		when(analyticsRepository.findById(uuid)).thenReturn(Optional.empty());

		// When / Then
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			analyticsService.getAnalyticsById(uuid);
		});

		assertTrue(exception.getMessage().contains("Analytics not found"));
	}

	@Test
	void shouldReturnAnalyticsDTO_whenAnalyticsFoundById() {
		// Given
		String uuid = "1234";
		Business business = Business.builder().businessId(1).build();
		Analytics analytics = Analytics.builder().uuid(uuid).methodName("testMethod").calledByIp("127.0.0.1")
				.business(business).build();
		AnalyticsDTO analyticsDTO = new AnalyticsDTO(uuid, "1", "testMethod", analytics.getCalledAt(), "127.0.0.1");

		when(analyticsRepository.findById(uuid)).thenReturn(Optional.of(analytics));
		when(analyticsMapper.toDto(any(Analytics.class))).thenReturn(analyticsDTO);

		// When
		AnalyticsDTO result = analyticsService.getAnalyticsById(uuid);

		// Then
		assertEquals(analyticsDTO, result);
	}

	@Test
	void shouldReturnListOfAnalyticsDTO_whenGetAllAnalyticsIsCalled() {
		// Given
		Business business = Business.builder().businessId(1).build();
		Analytics analytics = Analytics.builder().uuid("1234").methodName("testMethod").calledByIp("127.0.0.1")
				.business(business).build();
		AnalyticsDTO analyticsDTO = new AnalyticsDTO("1234", "1", "testMethod", analytics.getCalledAt(), "127.0.0.1");

		when(analyticsRepository.findAll()).thenReturn(Collections.singletonList(analytics));
		when(analyticsMapper.toDto(any(Analytics.class))).thenReturn(analyticsDTO);

		// When
		List<AnalyticsDTO> result = analyticsService.getAllAnalytics();

		// Then
		assertEquals(1, result.size());
		assertEquals(analyticsDTO, result.get(0));
	}

	@Test
	void shouldDeleteAnalytics_whenAnalyticsExists() {
		// Given
		String uuid = "1234";
		doNothing().when(analyticsRepository).deleteById(uuid);

		// When
		analyticsService.deleteAnalytics(uuid);

		// Then
		verify(analyticsRepository).deleteById(uuid);
	}
}

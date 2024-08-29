package com.track.share.analytics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.track.share.business.Business;
import com.track.share.business.BusinessRepository;
import com.track.share.config.auth.dtos.AuthRequest;
import com.track.share.config.auth.dtos.AuthResponse;
import com.track.share.config.auth.dtos.SignUpRequest;
import com.track.share.datadetail.DataDetailRepository;
import com.track.share.datamaster.DataMasterDTO;
import com.track.share.datamaster.DataMasterRepository;
import com.track.share.datamaster.RequestType;
import com.track.share.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class AnalyticsIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DataMasterRepository dataMasterRepository;

	@Autowired
	private DataDetailRepository dataDetailRepository;

	@Autowired
	private AnalyticsRepository underTest;

	private static String token = "";
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16.2"));

	@BeforeAll
	static void setup() {
		// setup connection
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@BeforeEach
	void setUp() {
		// auth
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest registerUser = new SignUpRequest("admin", "admin@gmail.com", "admin");
		restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST, new HttpEntity<>(registerUser), Void.class);

		AuthRequest request = new AuthRequest("admin@gmail.com", "admin");
		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange(API_AUTH_PATH + "/login", HttpMethod.POST,
				new HttpEntity<>(request), AuthResponse.class);

		assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		token = authResponse.getBody().token();

		// business
		Business business = Business.builder().name("business").username("masterbusiness")
				.password(new BCryptPasswordEncoder().encode("password")).build();
		business = businessRepository.save(business);

		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();
		DataMasterDTO newRecord = new DataMasterDTO(null, business.getBusinessId(), null, null, null, true, null, null,
				RequestType.PULL);

		restTemplate.exchange(API_DATA_MASTER_PATH, HttpMethod.POST, new HttpEntity<>(newRecord, headers), Void.class);

		// PULL DATA
		String API_PULL_PATH = "/api/pull";
		restTemplate.exchange(API_PULL_PATH, HttpMethod.GET, new HttpEntity<>(headers), Void.class);
		restTemplate.exchange(API_PULL_PATH, HttpMethod.GET, new HttpEntity<>(headers), Void.class);
		restTemplate.exchange(API_PULL_PATH, HttpMethod.GET, new HttpEntity<>(headers), Void.class);

	}

	@AfterEach
	void cleanUp() {
		underTest.deleteAll();
		dataDetailRepository.deleteAll();
		dataMasterRepository.deleteAll();
		businessRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void shouldGetAllAnalytics() {
		// given
		String API_ANALYTICS_PATH = "/api/analytics";
		HttpHeaders headers = createHeaders();

		// when
		ResponseEntity<List<?>> response = restTemplate.exchange(API_ANALYTICS_PATH, HttpMethod.GET,
				new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
				});

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		return headers;
	}
}

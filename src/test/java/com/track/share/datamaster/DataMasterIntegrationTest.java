package com.track.share.datamaster;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import com.track.share.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class DataMasterIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DataMasterRepository underTest;

	@Autowired
	private DataDetailRepository detailRepository;

	private static String token = "";

	private Business business;

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
		// given
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest registerUser = new SignUpRequest("admin", "admin@gmail.com", "admin");
		restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST, new HttpEntity<>(registerUser), Void.class);
		// when
		AuthRequest request = new AuthRequest("admin@gmail.com", "admin");
		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange(API_AUTH_PATH + "/login", HttpMethod.POST,
				new HttpEntity<>(request), AuthResponse.class);
		// then
		assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		token = authResponse.getBody().token();

		business = Business.builder().name("business").username("masterbusiness")
				.password(new BCryptPasswordEncoder().encode("password")).build();
		business = businessRepository.save(business);
	}

	@AfterEach
	void cleanUp() {
		detailRepository.deleteAll();
		underTest.deleteAll();
		businessRepository.deleteById(business.getBusinessId());
		userRepository.deleteAll();
	}

	@Test
	void shouldGetAllMasterRecords() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();

		// when
		ResponseEntity<List<?>> response = restTemplate.exchange(API_DATA_MASTER_PATH, HttpMethod.GET,
				new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
				});

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldAddMasterRecord() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();
		DataMasterDTO newRecord = new DataMasterDTO(null, business.getBusinessId(), null, null, null, true, null, null,
				RequestType.PULL);

		// when
		ResponseEntity<DataMasterDTO> response = restTemplate.exchange(API_DATA_MASTER_PATH, HttpMethod.POST,
				new HttpEntity<>(newRecord, headers), DataMasterDTO.class);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
	}

	@Test
	void shouldUpdateMasterRecord() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();

		DataMasterDTO newMaster = new DataMasterDTO(null, business.getBusinessId(), null, null, null, true, null, null,
				RequestType.PULL);

		ResponseEntity<DataMasterDTO> createdMaster = restTemplate.exchange(API_DATA_MASTER_PATH, HttpMethod.POST,
				new HttpEntity<>(newMaster, headers), DataMasterDTO.class);

		// when
		DataMasterDTO masterToUpdate = new DataMasterDTO(createdMaster.getBody().sharingId(), business.getBusinessId(),
				null, null, null, true, null, null, RequestType.PULL);
		Integer sharingId = createdMaster.getBody().sharingId();
		ResponseEntity<DataMasterDTO> updatedMaster = restTemplate.exchange(API_DATA_MASTER_PATH + "/" + sharingId,
				HttpMethod.PUT, new HttpEntity<>(masterToUpdate, headers), DataMasterDTO.class);

		// then
		assertThat(updatedMaster.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldReturnNotFoundWhenUpdatingNonExistingRecord() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();

		// when
		DataMasterDTO masterToUpdate = new DataMasterDTO(10, business.getBusinessId(), null, null, null, true, null,
				null, RequestType.PULL);
		Integer sharingId = 10; // does not exists
		ResponseEntity<DataMasterDTO> updatedMaster = restTemplate.exchange(API_DATA_MASTER_PATH + "/" + sharingId,
				HttpMethod.PUT, new HttpEntity<>(masterToUpdate, headers), DataMasterDTO.class);

		// then
		assertThat(updatedMaster.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@Disabled
	void shouldGetMasterRecordById() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();
		DataMasterDTO newRecord = new DataMasterDTO(null, business.getBusinessId(), null, null, null, true, null, null,
				RequestType.PULL);

		ResponseEntity<DataMasterDTO> createdRecord = restTemplate.exchange(API_DATA_MASTER_PATH, HttpMethod.POST,
				new HttpEntity<>(newRecord, headers), DataMasterDTO.class);

		// when
		ResponseEntity<DataMasterDTO> fetchedRecord = restTemplate.exchange(
				API_DATA_MASTER_PATH + "/" + createdRecord.getBody().sharingId(), HttpMethod.GET,
				new HttpEntity<>(headers), DataMasterDTO.class);

		// then
		assertThat(fetchedRecord.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	@Disabled
	void shouldReturnNotFoundWhenGettingNonExistingRecord() {
		// given
		String API_DATA_MASTER_PATH = "/api/datamaster";
		HttpHeaders headers = createHeaders();

		// when
		Integer sharingId = 100;
		ResponseEntity<DataMasterDTO> fetchedRecord = restTemplate.exchange(API_DATA_MASTER_PATH + "/" + sharingId,
				HttpMethod.GET, new HttpEntity<>(headers), DataMasterDTO.class);

		// then
		assertThat(fetchedRecord.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		return headers;
	}

}

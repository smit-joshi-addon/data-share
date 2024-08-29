package com.track.share.business;

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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.track.share.config.auth.dtos.AuthRequest;
import com.track.share.config.auth.dtos.AuthResponse;
import com.track.share.config.auth.dtos.SignUpRequest;
import com.track.share.exceptions.ApiErrorResponse;
import com.track.share.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class BusinessIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private BusinessRepository underTest;

	@Autowired
	private UserRepository userRepository;

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
	}

	@AfterEach
	void cleanUp() {
		userRepository.deleteAll();
	}

	@Test
	void shouldGetAllBusinesses() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		HttpHeaders headers = createHeaders();

		// when
		ResponseEntity<List<?>> response = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.GET,
				new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
				});

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

	}

	@Test
	void shouldReturnUnauthorizedWhenAuthenticationTokenIsInvalidOrExpired() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		// when
		String corruptedToken = token + "some-invalid-data";
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + corruptedToken);

		// GET
		ResponseEntity<String> invalidTokenResponse = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.GET,
				new HttpEntity<>(headers), String.class);
		// then for GET
		assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// POST
		invalidTokenResponse = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.POST, new HttpEntity<>(headers),
				String.class);
		// then for POST
		assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// PUT
		invalidTokenResponse = restTemplate.exchange(API_BUSINESS_PATH + "/1", HttpMethod.PUT,
				new HttpEntity<>(headers), String.class);
		// then for PUT
		assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// DELETE
		invalidTokenResponse = restTemplate.exchange(API_BUSINESS_PATH + "/1", HttpMethod.DELETE,
				new HttpEntity<>(headers), String.class);
		// then for DELETE
		assertThat(invalidTokenResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldCreateBusiness() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		String businessName = "sample-business";
		String businessUsername = "created_business";
		String businessPassword = "business";
		Business business = Business.builder().name(businessName).username(businessUsername).password(businessPassword)
				.build();
		HttpHeaders headers = createHeaders();

		// when
		ResponseEntity<BusinessDTO> createdBusiness = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.POST,
				new HttpEntity<>(business, headers), BusinessDTO.class);

		// then
		assertThat(createdBusiness.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createdBusiness.getBody().name()).isEqualTo(businessName);
		assertThat(createdBusiness.getBody().username()).isEqualTo(businessUsername);

		// clean
		underTest.deleteById(createdBusiness.getBody().businessId());
	}

	@Test
	void shouldUpdateBusiness() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		String businessName = "sample-business";
		String businessUsername = "business_to_update";
		String businessPassword = "business";
		Business business = Business.builder().name(businessName).username(businessUsername).password(businessPassword)
				.build();
		HttpHeaders headers = createHeaders();

		ResponseEntity<BusinessDTO> createdBusiness = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.POST,
				new HttpEntity<>(business, headers), BusinessDTO.class);

		// when
		Integer businessId = createdBusiness.getBody().businessId();
		businessName += " Updated";
		businessUsername = "business_updated";
		businessPassword = "passwordUpdated";
		Business businessToUpdate = Business.builder().name(businessName).username(businessUsername)
				.password(businessPassword).build();
		ResponseEntity<BusinessDTO> updatedBusiness = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.PUT, new HttpEntity<>(businessToUpdate, headers), BusinessDTO.class);

		// then
		assertThat(updatedBusiness.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(updatedBusiness.getBody().name()).isEqualTo(businessName);
		assertThat(updatedBusiness.getBody().username()).isEqualTo(businessUsername);

		// clean
		underTest.deleteById(updatedBusiness.getBody().businessId());
	}

	@Test
	void shouldReturnNotFoundWhenNoBusinessToUpdate() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		Integer businessId = 5;
		String businessName = "sample-business";
		String businessUsername = "business_updated";
		String businessPassword = "passwordUpdated";
		HttpHeaders headers = createHeaders();

		// when
		Business businessToUpdate = Business.builder().name(businessName).username(businessUsername)
				.password(businessPassword).build();
		ResponseEntity<ApiErrorResponse> businessNotFound = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.PUT, new HttpEntity<>(businessToUpdate, headers), ApiErrorResponse.class);

		// then
		assertThat(businessNotFound.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldGetBusinessById() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		String businessName = "sample-business";
		String businessUsername = "get_business";
		String businessPassword = "business";
		Business business = Business.builder().name(businessName).username(businessUsername).password(businessPassword)
				.build();
		HttpHeaders headers = createHeaders();

		ResponseEntity<BusinessDTO> createdBusiness = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.POST,
				new HttpEntity<>(business, headers), BusinessDTO.class);

		// when
		Integer businessId = createdBusiness.getBody().businessId();
		ResponseEntity<BusinessDTO> fetchedBusiness = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.GET, new HttpEntity<>(headers), BusinessDTO.class);

		// then
		assertThat(fetchedBusiness.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(fetchedBusiness.getBody().name()).isEqualTo(businessName);
		assertThat(fetchedBusiness.getBody().username()).isEqualTo(businessUsername);

		// clean
		underTest.deleteById(fetchedBusiness.getBody().businessId());
	}

	@Test
	void shouldReturnNotFoundWhenBusinessDoesNotExist() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		Integer businessId = 9999; // a likely non-existing ID
		HttpHeaders headers = createHeaders();

		// when
		ResponseEntity<BusinessDTO> notFoundResponse = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.GET, new HttpEntity<>(headers), BusinessDTO.class);

		// then
		assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldDeleteBusiness() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		String businessName = "sample-business";
		String businessUsername = "business_delete";
		String businessPassword = "business";
		Business business = Business.builder().name(businessName).username(businessUsername).password(businessPassword)
				.build();
		HttpHeaders headers = createHeaders();

		ResponseEntity<BusinessDTO> createdBusiness = restTemplate.exchange(API_BUSINESS_PATH, HttpMethod.POST,
				new HttpEntity<>(business, headers), BusinessDTO.class);

		// when
		Integer businessId = createdBusiness.getBody().businessId();
		ResponseEntity<Void> deleteResponse = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);

		// then
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// Verify that the business has been deleted
		ResponseEntity<BusinessDTO> fetchedBusiness = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.GET, new HttpEntity<>(headers), BusinessDTO.class);
		assertThat(fetchedBusiness.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldReturnNotFoundWhenDeletingBusinessThatDoesNotExists() {
		// given
		String API_BUSINESS_PATH = "/api/businesses";
		HttpHeaders headers = createHeaders();
		// when
		Integer businessId = 10;
		ResponseEntity<Void> deleteResponse = restTemplate.exchange(API_BUSINESS_PATH + "/" + businessId,
				HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
		// then
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	private HttpHeaders createHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		return headers;
	}
}

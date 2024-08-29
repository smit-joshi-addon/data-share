package com.track.share.config.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
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
import com.track.share.user.UserRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class ApiAuthIntegrationTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private UserRepository underTest;

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16.2"));

	@BeforeAll
	static void canEstalishedConnection() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@AfterEach
	void cleanUp() {
		underTest.deleteAll();
	}

	@Test
	void shouldAuthenticateUserIfCredentialsAreCorrect() {
		// given
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest registerUser1 = new SignUpRequest("admin", "admin@gmail.com", "admin");
		restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST, new HttpEntity<>(registerUser1),
				new ParameterizedTypeReference<>() {
				});
		// when
		AuthRequest request = new AuthRequest("admin@gmail.com", "admin");
		ResponseEntity<AuthResponse> authResponce = restTemplate.exchange(API_AUTH_PATH + "/login", HttpMethod.POST,
				new HttpEntity<>(request), AuthResponse.class);
		// then
		assertThat(authResponce.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldNotAuthenticateUserIfCredentialsAreIncorrect() {
		// given
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest registerUser1 = new SignUpRequest("admin", "admin@gmail.com", "admin");
		restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST, new HttpEntity<>(registerUser1),
				Void.class);

		// when
		AuthRequest request = new AuthRequest("admin2@gmail.com", "incorrectpassword");
		ResponseEntity<String> authResponce = restTemplate.exchange(API_AUTH_PATH + "/login", HttpMethod.POST,
				new HttpEntity<>(request), String.class);
		// then
		assertThat(authResponce.getStatusCode().is4xxClientError()).isTrue();

	}

	@Test
	void shouldRegisterUserIfEmailIsUnique() {
		// given
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest request = new SignUpRequest("admin", "admin@gmail.com", "admin");
		// when
		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST,
				new HttpEntity<>(request), AuthResponse.class);
		// then
		assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldNotRegisterUserIfEmailIsNotUnique() {
		// given
		String API_AUTH_PATH = "/api/auth";
		SignUpRequest registerUser1 = new SignUpRequest("admin", "admin@gmail.com", "admin");
		restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST, new HttpEntity<>(registerUser1),
				Void.class);
		// when
		SignUpRequest registerUser2 = new SignUpRequest("user2", "admin@gmail.com", "user2password");
		ResponseEntity<AuthResponse> authResponse = restTemplate.exchange(API_AUTH_PATH + "/register", HttpMethod.POST,
				new HttpEntity<>(registerUser2), AuthResponse.class);
		// then
		assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	void shouldReturnNotAllowedWhileAuthenticatingUser() {
		// given
		String API_AUTH_PATH = "/api/auth";
		// when
		ResponseEntity<String> message = restTemplate.getForEntity(API_AUTH_PATH + "/login", String.class);
		// then
		assertThat(message.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
	}

	@Test
	void shouldReturnNotAllowedWhileRegisteringUser() {
		// given
		String API_AUTH_PATH = "/api/auth";
		// when
		ResponseEntity<String> message = restTemplate.getForEntity(API_AUTH_PATH + "/register", String.class);
		// then
		assertThat(message.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
	}

}

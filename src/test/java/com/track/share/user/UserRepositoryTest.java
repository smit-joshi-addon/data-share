package com.track.share.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class UserRepositoryTest {

	@Autowired
	private UserRepository underTest;
	
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16.2"));
	
	@Test
	void canEstalishedConnection() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@BeforeEach
	void setUp() throws Exception {
		Users user = Users.builder().name("smit").email("smit@gmail.com").password("password").build();
		underTest.save(user);
	}

	@AfterEach
	void tearDown() throws Exception {
		underTest.deleteAll();
	}

	@Test
	void shouldReturnUsersWhenFindByEmailIsPresent() {
		// given
		// when
		Optional<Users> user = underTest.findByEmail("smit@gmail.com");
		// then
		assertThat(user).isPresent();
	}

	@Test
	void shouldNotUserWhenFindByEmailIsNotPreset() {
		// given
		// when
		Optional<Users> user = underTest.findByEmail("unknown@gmail.com");
		// then
		assertThat(user).isNotPresent();
	}

	@Test
	void shouldReturnTrueWhenUserExistsWithEmail() {
		// given
		// when
		Boolean isThereAnyUser = underTest.existsByEmail("smit@gmail.com");
		// then
		assertThat(isThereAnyUser).isTrue();
	}

	@Test
	void shouldReturnFalseWhenUserNotExistsWithEmail() {
		// given
		// when
		Boolean isThereAnyUser = underTest.existsByEmail("unknown@gmail.com");
		// then
		assertThat(isThereAnyUser).isFalse();
	}

}

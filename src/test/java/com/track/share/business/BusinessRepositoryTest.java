package com.track.share.business;

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
class BusinessRepositoryTest {

	@Autowired
	private BusinessRepository underTest;


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
		Business business = Business.builder().name("business").username("username").password("password").build();
		underTest.save(business);
	}

	@AfterEach
	void tearDown() throws Exception {
		underTest.deleteAll();
	}

	@Test
	void shouldReturnBueinessWhenFindByUsername() {
		// given
		// when
		Optional<Business> businessByUsername = underTest.findByUsername("username");
		// then
		assertThat(businessByUsername).isPresent();
	}

	@Test
	void shouldNotBusinessWhenFindByBusinessIsNotPresent() {
		// given
		// when
		Optional<Business> businessByUsername = underTest.findByUsername("unknownUser");
		// then
		assertThat(businessByUsername).isNotPresent();
	}
	
	@Test
	void shouldReturnTrueWhenExistsByUsername() {
		//given
		//when
		Boolean isThereAnyBusiness = underTest.existsByUsername("username");
		//then
		assertThat(isThereAnyBusiness).isTrue();
	}
	
	@Test
	void shouldReturnFalseWhenNotExistsByUsername() {
		//given
		//when
		Boolean isThereAnyBusiness = underTest.existsByUsername("unknownUser");
		//then
		assertThat(isThereAnyBusiness).isFalse();
	}

}

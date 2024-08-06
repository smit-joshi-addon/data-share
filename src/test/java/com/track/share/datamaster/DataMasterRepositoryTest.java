package com.track.share.datamaster;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

import com.track.share.business.Business;
import com.track.share.business.BusinessRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class DataMasterRepositoryTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16.2"));
	
	@Autowired
	private DataMasterRepository underTest;
	
	@Autowired
	private BusinessRepository businessRepository;
	
	private Business business;
	
	@BeforeAll
	static void  canEstalishedConnection() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}


	@BeforeEach
	void setUp() throws Exception {
		business = Business.builder().
				name("business-x").
				username("mybusiness").
				password("password").build();
		business=businessRepository.save(business);
		
		DataMaster master = DataMaster.builder()
				.type(RequestType.PULL)
				.business(business)
				.build();
		underTest.save(master);
	}

	@AfterEach
	void tearDown() throws Exception {
		underTest.deleteAll();
		businessRepository.deleteAll();
	}

	@Test
	void shouldReturnMasterByBusinessIfPresent() {
		//given
		//when
		Optional<DataMaster> masterByBusiness = underTest.findByBusiness(business);
		//then
		assertThat(masterByBusiness).isPresent();
	}
	
	@Test
	void shoudNotMasterByBusinessIfNotPresent() {
		//given
		business = Business.builder().
				name("business-x").
				username("mybusiness2").
				password("password").build();
		business=businessRepository.save(business);
		//when
		Optional<DataMaster> masterByBusiness = underTest.findByBusiness(business);
		//then
		assertThat(masterByBusiness).isNotPresent();
	}
}

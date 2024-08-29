package com.track.share.datadetail;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

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
import com.track.share.datamaster.DataMaster;
import com.track.share.datamaster.DataMasterRepository;
import com.track.share.datamaster.RequestType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Testcontainers
class DataDetailRepositoryTest {

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
			DockerImageName.parse("postgres:16.2"));

	@Autowired
	private DataDetailRepository underTest;

	@Autowired
	private DataMasterRepository masterRepository;

	@Autowired
	private BusinessRepository businessRepository;

	private DataMaster master;

	@BeforeAll
	static void canEstalishedConnection() {
		assertThat(postgreSQLContainer.isCreated()).isTrue();
		assertThat(postgreSQLContainer.isRunning()).isTrue();
	}

	@BeforeEach
	void setUp() throws Exception {
		Business business = Business.builder().name("business-x").username("mybusiness").password("password").build();
		business = businessRepository.save(business);

		master = DataMaster.builder().type(RequestType.PULL).business(business).build();
		master = masterRepository.save(master);

		DataDetail detail = DataDetail.builder().createdAt(LocalDateTime.now()).createdById("1")
				.createdByIp("192.168.1.1").createdByName("smit").master(master).secret("token").status(Boolean.TRUE)
				.build();
		detail = underTest.save(detail);
	}

	@AfterEach
	void tearDown() throws Exception {
		underTest.deleteAll();
		masterRepository.deleteAll();
		businessRepository.deleteAll();
	}

	@Test
	void shouldReturnNonZeroValueWhenMasterAndStatusPresent() {
		// given
		// when
		Integer updatedCount = underTest.updateByStatus(master, Boolean.FALSE);
		// then
		assertThat(updatedCount).isNotZero();
	}

	@Test
	void shouldReturnZeroWhenMasterAndStatusNotPresent() {
		// given
		Business business = Business.builder().name("business-x").username("mybusiness2").password("password").build();
		business = businessRepository.save(business);

		master = DataMaster.builder().type(RequestType.PULL).business(business).build();
		master = masterRepository.save(master);
		// when
		Integer updatedCount = underTest.updateByStatus(master, Boolean.FALSE);
		// then
		assertThat(updatedCount).isZero();
	}

	@Test
	void shouldReturnTrueWhenExistsByMasterAndStatusAndSecret() {
		// given
		// when
		Boolean isThereAnyDetail = underTest.existsByMasterAndStatusAndSecret(master, Boolean.TRUE, "token");
		// then
		assertThat(isThereAnyDetail).isTrue();
	}

	@Test
	void shouldReturnFalseWhenNotExistsByMasterAndStatusAndSecret() {
		// given
		// when
		Boolean isThereAnyDetail = underTest.existsByMasterAndStatusAndSecret(master, Boolean.FALSE, "token");
		// then
		assertThat(isThereAnyDetail).isFalse();
	}

}

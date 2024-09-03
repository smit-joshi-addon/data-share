package com.track.share.datadetail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.track.share.datamaster.DataMaster;
import com.track.share.datamaster.RequestType;
import com.track.share.exceptions.NotFoundException;

public class DataDetailServiceTest {

	@Mock
	private DataDetailRepository dataDetailRepository;

	@Mock
	private DataDetailMapper dataDetailMapper;

	@InjectMocks
	private DataDetailServiceImpl dataDetailService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldReturnAllDataDetails_whenGetAllDataDetailsIsCalled() {
		// Given
		DataDetail dataDetail = new DataDetail();
		DataDetailDTO dataDetailDTO = new DataDetailDTO(1, 1, "secret", LocalDateTime.now(), LocalDateTime.now(), "id",
				"name", "ip", true, RequestType.PULL);
		when(dataDetailRepository.findAll()).thenReturn(Collections.singletonList(dataDetail));
		when(dataDetailMapper.toDTO(dataDetail)).thenReturn(dataDetailDTO);

		// When
		List<DataDetailDTO> result = dataDetailService.getAllDataDetails();

		// Then
		assertEquals(1, result.size());
		assertEquals(dataDetailDTO, result.get(0));
	}

	@Test
	void shouldReturnDataDetailById_whenValidIdIsProvided() {
		// Given
		DataDetail dataDetail = new DataDetail();
		DataDetailDTO dataDetailDTO = new DataDetailDTO(1, 1, "secret", LocalDateTime.now(), LocalDateTime.now(), "id",
				"name", "ip", true, RequestType.PULL);
		when(dataDetailRepository.findById(1)).thenReturn(Optional.of(dataDetail));
		when(dataDetailMapper.toDTO(dataDetail)).thenReturn(dataDetailDTO);

		// When
		DataDetailDTO result = dataDetailService.getDataDetailById(1);

		// Then
		assertEquals(dataDetailDTO, result);
	}

	@Test
	void shouldThrowNotFoundException_whenIdDoesNotExist() {
		// Given
		when(dataDetailRepository.findById(1)).thenReturn(Optional.empty());

		// When / Then
		Exception exception = assertThrows(NotFoundException.class, () -> {
			dataDetailService.getDataDetailById(1);
		});
		assertTrue(exception.getMessage().contains("NO Data Exists for given id 1"));
	}

	@Test
	void shouldSaveAndReturnDataDetail_whenAddDataDetailIsCalled() {
		// Given
		DataDetailDTO dataDetailDTO = new DataDetailDTO(1, 1, "secret", LocalDateTime.now(), LocalDateTime.now(), "id",
				"name", "ip", true, RequestType.PULL);
		DataDetail dataDetail = new DataDetail();
		when(dataDetailMapper.toEntity(dataDetailDTO)).thenReturn(dataDetail);
		when(dataDetailRepository.save(dataDetail)).thenReturn(dataDetail);
		when(dataDetailMapper.toDTO(dataDetail)).thenReturn(dataDetailDTO);

		// When
		DataDetailDTO result = dataDetailService.addDataDetail(dataDetailDTO);

		// Then
		assertEquals(dataDetailDTO, result);
	}

	@Test
	void shouldUpdateDataDetailStatus_whenUpdateDataDetailStatusIsCalled() {
		// Given
		DataMaster master = new DataMaster();
		when(dataDetailRepository.updateByStatus(master, false)).thenReturn(1);

		// When
		Integer updatedCount = dataDetailService.updateDataDetailStatus(master);

		// Then
		assertEquals(1, updatedCount);
	}

	@Test
	void shouldReturnTrue_whenActiveStatusExists() {
		// Given
		DataMaster master = new DataMaster();
		String token = "token";
		when(dataDetailRepository.existsByMasterAndStatusAndSecret(master, true, token)).thenReturn(true);

		// When
		Boolean result = dataDetailService.isAnyActiveStatus(master, true, token);

		// Then
		assertTrue(result);
	}
}

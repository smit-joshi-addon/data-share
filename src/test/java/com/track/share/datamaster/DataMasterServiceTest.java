package com.track.share.datamaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.track.share.business.Business;
import com.track.share.business.BusinessService;
import com.track.share.datadetail.DataDetailDTO;
import com.track.share.datadetail.DataDetailService;
import com.track.share.exceptions.NotFoundException;
import com.track.share.user.UserService;
import com.track.share.user.Users;
import com.track.share.utility.JwtHelper;
import com.track.share.utility.Utility;

import jakarta.servlet.http.HttpServletRequest;

public class DataMasterServiceTest {

    @Mock
    private DataMasterRepository dataMasterRepository;

    @Mock
    private BusinessService businessService;

    @Mock
    private DataDetailService detailService;

    @Mock
    private DataMasterMapper dataMasterMapper;

    @Mock
    private UserService userService;

    @Mock
    private Utility utility;

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private DataMasterServiceImpl dataMasterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnAllMasterRecords_whenGetMasterRecordsIsCalled() {
        // Given
        DataMaster dataMaster = new DataMaster();
        DataMasterDTO dataMasterDTO = new DataMasterDTO(1, 1, "secret", "user123", "192.168.1.1", true, LocalDateTime.now(), LocalDateTime.now(), RequestType.PULL);
        when(dataMasterRepository.findAll()).thenReturn(Collections.singletonList(dataMaster));
        when(dataMasterMapper.toDTO(dataMaster)).thenReturn(dataMasterDTO);

        // When
        List<DataMasterDTO> result = dataMasterService.getMasterRecords();

        // Then
        assertEquals(1, result.size());
        assertEquals(dataMasterDTO, result.get(0));
    }

    @Test
    void shouldAddMasterRecordAndReturnDTO_whenAddMasterRecordIsCalled() {
        // Given
        DataMasterDTO masterDTO = new DataMasterDTO(null, 1, "secret", null, null, true, LocalDateTime.now(), null, RequestType.PULL);
        DataMaster dataMaster = new DataMaster();
        Users user = new Users();
        Business business = new Business();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(userService.getUser(anyString())).thenReturn(user);
        when(businessService.getBusiness(masterDTO.businessId())).thenReturn(business);
        when(dataMasterMapper.toEntity(masterDTO)).thenReturn(dataMaster);
        when(jwtHelper.generateToken(business.getUsername())).thenReturn("token");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(dataMasterRepository.save(dataMaster)).thenReturn(dataMaster);
        when(dataMasterMapper.toDTO(dataMaster)).thenReturn(masterDTO);
        when(userService.getUser("username")).thenReturn(Users.builder().userId(1L).name("user").email("user@gmail.com").build());
        when(utility.getCurrentUsername()).thenReturn("username");
        when(jwtHelper.getExpirationDateFromToken(dataMaster.getSecret())).thenReturn(new Date());

        // When
        DataMasterDTO result = dataMasterService.addMasterRecord(masterDTO, request);

        // Then
        assertEquals(masterDTO, result);
        verify(detailService, times(1)).addDataDetail(any(DataDetailDTO.class));
    }

    @Test
    void shouldUpdateMasterRecordAndReturnDTO_whenUpdateMasterRecordIsCalled() {
        // Given
        DataMasterDTO masterDTO = new DataMasterDTO(null, 1, "secret", "user123", "192.168.1.1", true, LocalDateTime.now(), null, RequestType.PULL);
        DataMaster existingDataMaster = new DataMaster();
        DataMaster updatedDataMaster = new DataMaster();
        Users user = new Users();
        Business business = new Business();
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(dataMasterRepository.findById(anyInt())).thenReturn(Optional.of(existingDataMaster));
        when(userService.getUser(anyString())).thenReturn(user);
        when(businessService.getBusiness(masterDTO.businessId())).thenReturn(business);
        when(jwtHelper.generateToken(business.getUsername())).thenReturn("token");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");
        when(dataMasterRepository.save(existingDataMaster)).thenReturn(updatedDataMaster);
        when(dataMasterMapper.toDTO(updatedDataMaster)).thenReturn(masterDTO);
        when(userService.getUser("username")).thenReturn(Users.builder().userId(1L).name("user").email("user@gmail.com").build());
        when(utility.getCurrentUsername()).thenReturn("username");
        when(jwtHelper.getExpirationDateFromToken(existingDataMaster.getSecret())).thenReturn(new Date());

        // When
        DataMasterDTO result = dataMasterService.updateMasterRecord(masterDTO, 1, request);

        // Then
        assertEquals(masterDTO, result);
        verify(detailService, times(1)).updateDataDetailStatus(updatedDataMaster);
        verify(detailService, times(1)).addDataDetail(any(DataDetailDTO.class));
    }

    @Test
    void shouldThrowRuntimeException_whenUpdatingNonExistentMasterRecord() {
        // Given
        DataMasterDTO masterDTO = new DataMasterDTO(null, 1, "secret", "user123", "192.168.1.1", true, LocalDateTime.now(), null, RequestType.PULL);

        when(dataMasterRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataMasterService.updateMasterRecord(masterDTO, 1, mock(HttpServletRequest.class));
        });

        assertTrue(exception.getMessage().contains("DataMaster record not found with id 1"));
    }

    @Test
    void shouldReturnMasterRecord_whenGetMasterRecordIsCalled() {
        // Given
        DataMaster dataMaster = new DataMaster();
        when(dataMasterRepository.findById(anyInt())).thenReturn(Optional.of(dataMaster));

        // When
        DataMaster result = dataMasterService.getMasterRecord(1);

        // Then
        assertEquals(dataMaster, result);
    }

    @Test
    void shouldThrowRuntimeException_whenMasterRecordDoesNotExist() {
        // Given
        when(dataMasterRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dataMasterService.getMasterRecord(1);
        });

        assertTrue(exception.getMessage().contains("DataMaster record not found with id 1"));
    }

    @Test
    void shouldReturnMaster_whenGetMasterByBusinessIsCalled() {
        // Given
        Business business = new Business();
        DataMaster dataMaster = new DataMaster();
        when(dataMasterRepository.findByBusiness(business)).thenReturn(Optional.of(dataMaster));

        // When
        DataMaster result = dataMasterService.getMasterByBusiness(business);

        // Then
        assertEquals(dataMaster, result);
    }

    @Test
    void shouldThrowNotFoundException_whenNoMasterFoundByBusiness() {
        // Given
        Business business = new Business();
        when(dataMasterRepository.findByBusiness(business)).thenReturn(Optional.empty());

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            dataMasterService.getMasterByBusiness(business);
        });

        assertTrue(exception.getMessage().contains("No Master found associated with " + business.getName()));
    }
}

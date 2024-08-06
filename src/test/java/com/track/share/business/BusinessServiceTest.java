package com.track.share.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.track.share.config.auth.AuthUserDetails;
import com.track.share.datadetail.DataDetailService;
import com.track.share.datamaster.DataMaster;
import com.track.share.datamaster.DataMasterService;
import com.track.share.exceptions.NotFoundException;
import com.track.share.exceptions.UsernameUnavailableException;
import com.track.share.user.Users;

public class BusinessServiceTest {

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private BusinessMapper businessMapper;

    @Mock
    private DataMasterService masterService;

    @Mock
    private DataDetailService detailService;

    @InjectMocks
    private BusinessServiceImpl businessService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowUsernameUnavailableException_whenAddingBusinessWithExistingUsername() {
        // Given
        Business business = new Business();
        when(businessRepository.existsByUsername(business.getUsername())).thenReturn(true);

        // When / Then
        UsernameUnavailableException exception = assertThrows(UsernameUnavailableException.class, () -> {
            businessService.addBusiness(business);
        });

        assertTrue(exception.getMessage().contains("Username unavailable, please try with a new username"));
    }

    @Test
    void shouldAddBusinessAndReturnDTO_whenAddingNewBusiness() {
        // Given
    	Integer businessId=1;
    	String businessName="testBusiness";
    	String businessUsername="testusername";
    	String businessPassword="testpassword";
        Business business = new Business(businessId,businessName,businessUsername,businessPassword);
        BusinessDTO businessDTO = new BusinessDTO(businessId,businessName,businessUsername);
        when(businessRepository.existsByUsername(business.getUsername())).thenReturn(false);
        when(businessMapper.toDTO(any(Business.class))).thenReturn(businessDTO);
        when(businessRepository.save(business)).thenReturn(business);

        // When
        BusinessDTO result = businessService.addBusiness(business);

        // Then
        assertEquals(businessDTO, result);
        verify(businessRepository).save(business);
    }

    @Test
    void shouldReturnListOfBusinesses_whenGetBusinessesIsCalled() {
        // Given
        Business business = new Business();
        BusinessDTO businessDTO = new BusinessDTO(1, "Test Business", "testUsername");
        when(businessRepository.findAll()).thenReturn(Collections.singletonList(business));
        when(businessMapper.toDTO(business)).thenReturn(businessDTO);

        // When
        List<BusinessDTO> result = businessService.getBusinesses();

        // Then
        assertEquals(1, result.size());
        assertEquals(businessDTO, result.get(0));
    }

    @Test
    void shouldThrowNotFoundException_whenUpdatingNonExistentBusiness() {
        // Given
        Business business = new Business();
        when(businessRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            businessService.updateBusiness(1, business);
        });

        assertTrue(exception.getMessage().contains("Business not found with id 1"));
    }

    @Test
    void shouldUpdateBusinessAndReturnDTO_whenBusinessExists() {
        // Given
        Business existingBusiness = new Business();
        BusinessDTO updatedBusinessDTO = new BusinessDTO(1, "Updated Business", "updatedUsername");
        Business updatedBusiness = new Business();
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(existingBusiness));
        when(businessRepository.save(existingBusiness)).thenReturn(updatedBusiness);
        when(businessMapper.toDTO(updatedBusiness)).thenReturn(updatedBusinessDTO);

        // When
        BusinessDTO result = businessService.updateBusiness(1, existingBusiness);

        // Then
        assertEquals(updatedBusinessDTO, result);
    }

    @Test
    void shouldDeleteBusinessAndReturnTrue_whenBusinessExists() {
        // Given
        when(businessRepository.existsById(anyInt())).thenReturn(true);

        // When
        Boolean result = businessService.deleteBusiness(1);

        // Then
        assertTrue(result);
        verify(businessRepository).deleteById(1);
    }

    @Test
    void shouldThrowNotFoundException_whenDeletingNonExistentBusiness() {
        // Given
        when(businessRepository.existsById(anyInt())).thenReturn(false);

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            businessService.deleteBusiness(1);
        });

        assertTrue(exception.getMessage().contains("Business not found with id 1"));
    }

    @Test
    void shouldReturnBusiness_whenGettingBusinessById() {
        // Given
        Business business = new Business();
        when(businessRepository.findById(anyInt())).thenReturn(Optional.of(business));

        // When
        Business result = businessService.getBusiness(1);

        // Then
        assertEquals(business, result);
    }

    @Test
    void shouldThrowNotFoundException_whenBusinessNotFoundById() {
        // Given
        when(businessRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            businessService.getBusiness(1);
        });

        assertTrue(exception.getMessage().contains("Business not found with id 1"));
    }

    @Test
    void shouldReturnBusiness_whenGettingBusinessByUsername() {
        // Given
        Business business = new Business();
        when(businessRepository.findByUsername(anyString())).thenReturn(Optional.of(business));

        // When
        Business result = businessService.getBusinessByUsername("testUsername");

        // Then
        assertEquals(business, result);
    }

    @Test
    void shouldThrowNotFoundException_whenBusinessNotFoundByUsername() {
        // Given
        when(businessRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            businessService.getBusinessByUsername("testUsername");
        });

        assertTrue(exception.getMessage().contains("Invalid Username Or Password"));
    }

    @Test
    void shouldReturnAuthUserDetails_whenLoadingUserByUsername() {
        // Given
        Business business = new Business();
        business.setUsername("testUsername");
        business.setPassword("encodedPassword");
        DataMaster dataMaster = new DataMaster();
        when(businessRepository.findByUsername(anyString())).thenReturn(Optional.of(business));
        when(masterService.getMasterByBusiness(any(Business.class))).thenReturn(dataMaster);
        when(detailService.isAnyActiveStatus(any(DataMaster.class), anyBoolean(), anyString())).thenReturn(true);

        AuthUserDetails expectedDetails = new AuthUserDetails(Users.builder()
            .email(business.getUsername())
            .status(true)
            .password(business.getPassword())
            .build());

        // When
        AuthUserDetails result = businessService.loadUserByUsername("testUsername", "token");

        // Then
        assertEquals(expectedDetails, result);
    }

    @Test
    void shouldThrowUsernameNotFoundException_whenLoadingUserByUsernameFails() {
        // Given
        when(businessRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When / Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            businessService.loadUserByUsername("testUsername", "token");
        });

        assertTrue(exception.getMessage().contains("Invalid Username Or Password"));
    }
}

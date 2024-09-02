package com.track.share.business;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.track.share.config.auth.AuthUserDetails;
import com.track.share.datadetail.DataDetailService;
import com.track.share.datamaster.DataMaster;
import com.track.share.datamaster.DataMasterServiceImpl;
import com.track.share.exceptions.NotFoundException;
import com.track.share.exceptions.UsernameUnavailableException;
import com.track.share.user.Users;

@Service
public class BusinessServiceImpl implements BusinessService {

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private BusinessMapper businessMapper;

	@Autowired
    private ApplicationContext context;


	@Autowired
	private DataDetailService detailService;

	@Override
	public BusinessDTO addBusiness(Business business) {
		if (businessRepository.existsByUsername(business.getUsername())) {
			throw new UsernameUnavailableException("Username unavailable, please try with a new username");
		}
		business.setPassword(new BCryptPasswordEncoder().encode(business.getPassword()));
		Business savedBusiness = businessRepository.save(business);
		return businessMapper.toDTO(savedBusiness);
	}

	@Override
	public List<BusinessDTO> getBusinesses() {
		return businessRepository.findAll().stream().map(businessMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	public BusinessDTO updateBusiness(Integer businessId, Business business) {
		Optional<Business> existingBusiness = businessRepository.findById(businessId);
		if (existingBusiness.isPresent()) {
			Business updatedBusiness = existingBusiness.get();
			updatedBusiness.setName(business.getName());
			updatedBusiness.setUsername(business.getUsername());
			// Update other fields if necessary
			Business savedBusiness = businessRepository.save(updatedBusiness);
			return businessMapper.toDTO(savedBusiness);
		} else {
			throw new NotFoundException("Business not found with id " + businessId);
		}
	}

	@Override
	public Boolean deleteBusiness(Integer businessId) {
		if (!businessRepository.existsById(businessId)) {
			throw new NotFoundException("Business not found with id " + businessId);
		}
		businessRepository.deleteById(businessId);
		return true;
	}

	@Override
	public Business getBusiness(Integer businessId) {
		return businessRepository.findById(businessId)
				.orElseThrow(() -> new NotFoundException("Business not found with id " + businessId));
	}

	@Override
	public AuthUserDetails loadUserByUsername(String username, String token) throws UsernameNotFoundException {
		Optional<Business> business = businessRepository.findByUsername(username);
		if (business.isEmpty()) {
			throw new NotFoundException("Invalid Username Or Password");
		}
		DataMaster master = context.getBean(DataMasterServiceImpl.class).getMasterByBusiness(business.get());
		Boolean status = detailService.isAnyActiveStatus(master, Boolean.TRUE, token);
		return new AuthUserDetails(Users.builder().email(business.get().getUsername()).status(status)
				.password(business.get().getPassword()).build());

	}

	@Override
	public Business getBusinessByUsername(String username) {
		Optional<Business> business = businessRepository.findByUsername(username);
		if (business.isEmpty()) {
			throw new NotFoundException("Invalid Username Or Password");
		}
		return business.get();
	}
}
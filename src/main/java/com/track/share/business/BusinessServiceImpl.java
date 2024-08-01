package com.track.share.business;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.track.share.config.auth.AuthUserDetails;
import com.track.share.datadetail.DataDetailService;
import com.track.share.datamaster.DataMaster;
import com.track.share.datamaster.DataMasterService;
import com.track.share.user.Users;

@Service
class BusinessServiceImpl implements BusinessService {

	@Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private BusinessMapper businessMapper;

	@Autowired
	private DataMasterService masterService;

	@Autowired
	private DataDetailService detailService;

	@Override
	public BusinessDTO addBusiness(Business business) {
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
			throw new RuntimeException("Business not found with id " + businessId);
		}
	}

	@Override
	public Boolean deleteBusiness(Integer businessId) {
		if (businessRepository.existsById(businessId)) {
			businessRepository.deleteById(businessId);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Business getBusiness(Integer businessId) {
		return businessRepository.findById(businessId)
				.orElseThrow(() -> new RuntimeException("Business not found with id " + businessId));
	}

	@Override
	public AuthUserDetails loadUserByUsername(String username, String token) throws UsernameNotFoundException {
		Business business = businessRepository.findByUsername(username);
		DataMaster master = masterService.getMasterByBusiness(business);
		Boolean status = detailService.isAnyActiveStatus(master, Boolean.TRUE,token);
		if (business == null) {
			throw new UsernameNotFoundException("Invalid Username Or Password");
		}
		
		return new AuthUserDetails(
				Users.builder().email(business.getUsername()).status(status).password(business.getPassword()).build());

	}

	@Override
	public Business getBusinessByUsername(String username) {
		return businessRepository.findByUsername(username);
	}
}
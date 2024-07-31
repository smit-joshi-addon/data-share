package com.track.share.business;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
class BusinessServiceImpl implements BusinessService {

    @Autowired
	private BusinessRepository businessRepository;

	@Autowired
	private BusinessMapper businessMapper;

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
}
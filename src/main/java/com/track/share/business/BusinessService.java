package com.track.share.business;

import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.track.share.config.auth.AuthUserDetails;

public interface BusinessService {

	BusinessDTO addBusiness(Business business);

	List<BusinessDTO> getBusinesses();

	BusinessDTO updateBusiness(Integer businessId, Business business);

	Boolean deleteBusiness(Integer businessId);
	
	Business getBusiness(Integer businessId);
	
	Business getBusinessByUsername(String username);
	
	AuthUserDetails loadUserByUsername(String username,String token) throws UsernameNotFoundException;

}

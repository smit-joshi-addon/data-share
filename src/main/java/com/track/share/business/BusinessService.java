package com.track.share.business;

import java.util.List;

public interface BusinessService {

	BusinessDTO addBusiness(Business business);

	List<BusinessDTO> getBusinesses();

	BusinessDTO updateBusiness(Integer businessId, Business business);

	Boolean deleteBusiness(Integer businessId);
	
	Business getBusiness(Integer businessId);

}

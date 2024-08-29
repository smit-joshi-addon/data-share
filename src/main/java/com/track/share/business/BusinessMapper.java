package com.track.share.business;

import org.springframework.stereotype.Component;

@Component
class BusinessMapper {

	// Convert Business entity to BusinessDTO
	public BusinessDTO toDTO(Business business) {
		if (business == null) {
			return null;
		}
		return new BusinessDTO(business.getBusinessId(), business.getName(), business.getUsername());
	}

	// Convert BusinessDTO to Business entity
	public Business toEntity(BusinessDTO businessDTO) {
		if (businessDTO == null) {
			return null;
		}
		return Business.builder().businessId(businessDTO.businessId()).name(businessDTO.name())
				.username(businessDTO.username()).build();
	}
}

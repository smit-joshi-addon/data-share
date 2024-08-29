package com.track.share.datamaster;

import org.springframework.stereotype.Component;

import com.track.share.business.Business;

@Component
class DataMasterMapper {

	// Convert DataMaster entity to DataMasterDTO
	public DataMasterDTO toDTO(DataMaster dataMaster) {
		if (dataMaster == null) {
			return null;
		}
		return new DataMasterDTO(dataMaster.getSharingId(),
				dataMaster.getBusiness() != null ? dataMaster.getBusiness().getBusinessId() : null, // Assuming you only
																									// need the business
																									// ID
				dataMaster.getSecret(), dataMaster.getCeatedById(), dataMaster.getCreatedByIp(), dataMaster.getStatus(),
				dataMaster.getCreatedAt(), dataMaster.getUpdatedAt(), dataMaster.getType());
	}

	// Convert DataMasterDTO to DataMaster entity
	public DataMaster toEntity(DataMasterDTO dataMasterDTO) {
		if (dataMasterDTO == null) {
			return null;
		}
		return DataMaster.builder().sharingId(dataMasterDTO.sharingId() != null ? dataMasterDTO.sharingId() : null)
				.business(Business.builder().businessId(dataMasterDTO.businessId()).build())
				.secret(dataMasterDTO.secret() != null ? dataMasterDTO.secret() : null)
				.ceatedById(dataMasterDTO.createdById() != null ? dataMasterDTO.createdById() : null)
				.createdByIp(dataMasterDTO.createdByIp() != null ? dataMasterDTO.createdByIp() : null)
				.status(dataMasterDTO.status() != null ? dataMasterDTO.status() : null).type(dataMasterDTO.type())
				.build();
	}
}
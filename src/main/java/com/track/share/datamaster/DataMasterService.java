package com.track.share.datamaster;

import java.util.List;

import com.track.share.business.Business;

import jakarta.servlet.http.HttpServletRequest;

public interface DataMasterService {

	List<DataMasterDTO> getMasterRecords();

	DataMasterDTO addMasterRecord(DataMasterDTO master,HttpServletRequest request);

	DataMasterDTO updateMasterRecord(DataMasterDTO master, Integer sharingId,HttpServletRequest request);

	DataMaster getMasterRecord(Integer sharingId);
	
	DataMaster getMasterByBusiness(Business business);

}

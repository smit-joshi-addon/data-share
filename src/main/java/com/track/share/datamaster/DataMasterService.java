package com.track.share.datamaster;

import java.util.List;

public interface DataMasterService {

	List<DataMasterDTO> getMasterRecords();

	DataMasterDTO addMasterRecord(DataMasterDTO master);

	DataMasterDTO updateMasterRecord(DataMasterDTO master, Integer sharingId);

	DataMaster getMasterRecord(Integer sharingId);

}

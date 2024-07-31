package com.track.share.datadetail;

import java.util.List;

import com.track.share.datamaster.DataMaster;

public interface DataDetailService {

    List<DataDetailDTO> getAllDataDetails();

    DataDetailDTO getDataDetailById(Integer detailId);

    DataDetailDTO addDataDetail(DataDetailDTO dataDetailDTO);
    
    Integer updateDataDetailStatus(DataMaster master);

//    DataDetailDTO updateDataDetail(Integer detailId, DataDetailDTO dataDetailDTO);
//
//    boolean deleteDataDetail(Integer detailId);
}

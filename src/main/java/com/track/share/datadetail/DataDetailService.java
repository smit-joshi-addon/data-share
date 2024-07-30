package com.track.share.datadetail;

import java.util.List;

public interface DataDetailService {

    List<DataDetailDTO> getAllDataDetails();

    DataDetailDTO getDataDetailById(Integer detailId);

    DataDetailDTO addDataDetail(DataDetailDTO dataDetailDTO);

    DataDetailDTO updateDataDetail(Integer detailId, DataDetailDTO dataDetailDTO);

    boolean deleteDataDetail(Integer detailId);
}

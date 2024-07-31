package com.track.share.datadetail;

import org.springframework.stereotype.Component;

import com.track.share.datamaster.DataMaster;

@Component
public class DataDetailMapper {

    public DataDetailDTO toDTO(DataDetail dataDetail) {
        if (dataDetail == null) {
            return null;
        }

        return new DataDetailDTO(
                dataDetail.getDetailId(),
                dataDetail.getMaster() != null ? dataDetail.getMaster().getSharingId() : null,
                dataDetail.getSecret(),
                dataDetail.getValidTill(),
                dataDetail.getCreatedAt(),
                dataDetail.getCreatedById(),
                dataDetail.getCreatedByName(),
                dataDetail.getCreatedByIp(),
                dataDetail.getStatus()
        );
    }

    public DataDetail toEntity(DataDetailDTO dataDetailDTO) {
        if (dataDetailDTO == null) {
            return null;
        }

        DataDetail dataDetail = new DataDetail();
        dataDetail.setMaster(DataMaster.builder().sharingId(dataDetailDTO.masterId()).build());
        dataDetail.setSecret(dataDetailDTO.secret());
        dataDetail.setValidTill(dataDetailDTO.validTill());
        
        dataDetail.setCreatedById(dataDetailDTO.createdById());
        dataDetail.setCreatedByName(dataDetailDTO.createdByName());
        dataDetail.setCreatedByIp(dataDetailDTO.createdByIp());
        dataDetail.setStatus(dataDetailDTO.status());

        return dataDetail;
    }
}
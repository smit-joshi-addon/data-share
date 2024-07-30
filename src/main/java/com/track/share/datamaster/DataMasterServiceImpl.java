package com.track.share.datamaster;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.track.share.business.Business;
import com.track.share.business.BusinessService;
import com.track.share.utility.JwtHelper;


@Service
public class DataMasterServiceImpl implements DataMasterService {

    @Autowired
    private DataMasterRepository dataMasterRepository;
    
    @Autowired
    private BusinessService businessService;

    @Autowired
    private DataMasterMapper dataMasterMapper;
    
    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public List<DataMasterDTO> getMasterRecords() {
        return dataMasterRepository.findAll().stream()
            .map(dataMasterMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public DataMasterDTO addMasterRecord(DataMasterDTO masterDTO) {
    	Business  business = businessService.getBusiness(masterDTO.businessId());
        DataMaster dataMaster = dataMasterMapper.toEntity(masterDTO);
        dataMaster.setSecret(jwtHelper.generateToken(business.getUsername()));
        DataMaster savedDataMaster = dataMasterRepository.save(dataMaster);
        return dataMasterMapper.toDTO(savedDataMaster);
    }

    @Override
    public DataMasterDTO updateMasterRecord(DataMasterDTO masterDTO, Integer sharingId) {
        Optional<DataMaster> existingDataMaster = dataMasterRepository.findById(sharingId);
        if (existingDataMaster.isPresent()) {
            DataMaster dataMaster = existingDataMaster.get();
            Business  business = businessService.getBusiness(masterDTO.businessId());
            dataMaster.setBusiness(Business.builder().businessId(masterDTO.businessId()).build());
            dataMaster.setSecret(jwtHelper.generateToken(business.getUsername()));
            dataMaster.setCeatedBy(masterDTO.ceatedBy());
            dataMaster.setCreatedByIp(masterDTO.createdByIp());
            dataMaster.setStatus(masterDTO.status());
            // Update other fields if needed
            DataMaster updatedDataMaster = dataMasterRepository.save(dataMaster);
            return dataMasterMapper.toDTO(updatedDataMaster);
        } else {
            throw new RuntimeException("DataMaster record not found with id " + sharingId);
        }
    }

    @Override
    public DataMaster getMasterRecord(Integer sharingId) {
        return dataMasterRepository.findById(sharingId)
            .orElseThrow(() -> new RuntimeException("DataMaster record not found with id " + sharingId));
    }
}
package com.track.share.datadetail;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.track.share.datamaster.DataMaster;
import com.track.share.exceptions.NotFoundException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class DataDetailServiceImpl implements DataDetailService {

    private DataDetailRepository dataDetailRepository;

    private DataDetailMapper dataDetailMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DataDetailDTO> getAllDataDetails() {
        return dataDetailRepository.findAll().stream()
                .map(dataDetailMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DataDetailDTO getDataDetailById(Integer detailId) {
        return dataDetailRepository.findById(detailId)
                .map(dataDetailMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("NO Data Exists for given id "+detailId)); // Or throw an exception if not found
    }

    @Override
    @Transactional
    public DataDetailDTO addDataDetail(DataDetailDTO dataDetailDTO) {
            DataDetail dataDetail = dataDetailMapper.toEntity(dataDetailDTO);
            DataDetail savedDataDetail = dataDetailRepository.save(dataDetail);
            return dataDetailMapper.toDTO(savedDataDetail);
    }
    
    @Override
    @Transactional
    public Integer updateDataDetailStatus(DataMaster master) {
    	 return dataDetailRepository.updateByStatus(master, false);
    }

	@Override
	public Boolean isAnyActiveStatus(DataMaster master,Boolean status,String token) {
		return dataDetailRepository.existsByMasterAndStatusAndSecret(master, status, token);
	}
    

//    @Override
//    @Transactional
//    public DataDetailDTO updateDataDetail(Integer detailId, DataDetailDTO dataDetailDTO) {
//        Optional<DataDetail> optionalDataDetail = dataDetailRepository.findById(detailId);
//        if (optionalDataDetail.isPresent()) {
//            DataDetail existingDataDetail = optionalDataDetail.get();
//            // Update fields
//            existingDataDetail.setSecret(dataDetailDTO.secret());
//            existingDataDetail.setValidTill(dataDetailDTO.validTill());
//            existingDataDetail.setCreatedById(dataDetailDTO.createdById());
//            existingDataDetail.setCreatedByName(dataDetailDTO.createdByName());
//            existingDataDetail.setCreatedByIp(dataDetailDTO.createdByIp());
//            existingDataDetail.setStatus(dataDetailDTO.status());
//            // Save and return updated entity
//            DataDetail updatedDataDetail = dataDetailRepository.save(existingDataDetail);
//            return dataDetailMapper.toDTO(updatedDataDetail);
//        }
//        return null; // Or throw an exception if not found
//    }
//
//    @Override
//    @Transactional
//    public boolean deleteDataDetail(Integer detailId) {
//        if (dataDetailRepository.existsById(detailId)) {
//            dataDetailRepository.deleteById(detailId);
//            return true;
//        }
//        return false; // Or throw an exception if not found
//    }
}

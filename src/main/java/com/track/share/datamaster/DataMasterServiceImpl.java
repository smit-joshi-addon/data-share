package com.track.share.datamaster;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.track.share.business.Business;
import com.track.share.business.BusinessService;
import com.track.share.datadetail.DataDetailDTO;
import com.track.share.datadetail.DataDetailService;
import com.track.share.user.UserService;
import com.track.share.user.Users;
import com.track.share.utility.JwtHelper;
import com.track.share.utility.Utility;

import jakarta.servlet.http.HttpServletRequest;

@Service
class DataMasterServiceImpl implements DataMasterService {

	@Autowired
	private DataMasterRepository dataMasterRepository;

	@Autowired
	private BusinessService businessService;

	@Autowired
	private DataDetailService detailService;

	@Autowired
	private DataMasterMapper dataMasterMapper;

	@Autowired
	private UserService userService;

	@Autowired
	private Utility utility;

	@Autowired
	private JwtHelper jwtHelper;

	@Override
	@Transactional(readOnly = true)
	public List<DataMasterDTO> getMasterRecords() {
		return dataMasterRepository.findAll().stream().map(dataMasterMapper::toDTO).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public DataMasterDTO addMasterRecord(DataMasterDTO masterDTO, HttpServletRequest request) {
		Users user = userService.getUser(utility.getCurrentUsername());
		Business business = businessService.getBusiness(masterDTO.businessId());
		DataMaster dataMaster = dataMasterMapper.toEntity(masterDTO);
		dataMaster.setSecret(jwtHelper.generateToken(business.getUsername()));

		dataMaster.setCeatedById(user.getUserId().toString());
		dataMaster.setCreatedByIp(request.getRemoteAddr());
		DataMaster savedDataMaster = dataMasterRepository.save(dataMaster);
		addDetails(savedDataMaster);
		return dataMasterMapper.toDTO(savedDataMaster);
	}

	private void addDetails(DataMaster master) {
		Users user = userService.getUser(utility.getCurrentUsername());
		Date dateTieme = jwtHelper.getExpirationDateFromToken(master.getSecret());
		DataDetailDTO detailDTO = new DataDetailDTO(null, master.getSharingId(), master.getSecret(),
				utility.convertToLocalDateTime(dateTieme), null, master.getCeatedById(), user.getName(),
				master.getCreatedByIp(), master.getStatus());
		detailService.addDataDetail(detailDTO);
	}

	@Override
	@Transactional
	public DataMasterDTO updateMasterRecord(DataMasterDTO masterDTO, Integer sharingId, HttpServletRequest request) {
		Optional<DataMaster> existingDataMaster = dataMasterRepository.findById(sharingId);
		if (existingDataMaster.isPresent()) {
			Users user = userService.getUser(utility.getCurrentUsername());
			DataMaster dataMaster = existingDataMaster.get();
			Business business = businessService.getBusiness(masterDTO.businessId());
			dataMaster.setBusiness(Business.builder().businessId(masterDTO.businessId()).build());
			dataMaster.setSecret(jwtHelper.generateToken(business.getUsername()));
			dataMaster.setCeatedById(user.getUserId().toString());
			dataMaster.setCreatedByIp(request.getRemoteAddr());
			dataMaster.setStatus(masterDTO.status());
			// Update other fields if needed
			DataMaster updatedDataMaster = dataMasterRepository.save(dataMaster);

			detailService.updateDataDetailStatus(updatedDataMaster);
			addDetails(updatedDataMaster);
			return dataMasterMapper.toDTO(updatedDataMaster);
		} else {
			throw new RuntimeException("DataMaster record not found with id " + sharingId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DataMaster getMasterRecord(Integer sharingId) {
		return dataMasterRepository.findById(sharingId)
				.orElseThrow(() -> new RuntimeException("DataMaster record not found with id " + sharingId));
	}

	@Override
	public DataMaster getMasterByBusiness(Business business) {
		return dataMasterRepository.findByBusiness(business);
	}
}
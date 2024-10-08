package com.track.share.datamaster;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/datamaster")
class DataMasterController {

	@Autowired
	private DataMasterService dataMasterService;

	@GetMapping
	@Operation(summary = "get all the master records")
	public ResponseEntity<List<DataMasterDTO>> getMasterRecords() {
		List<DataMasterDTO> masterRecords = dataMasterService.getMasterRecords();
		return new ResponseEntity<>(masterRecords, HttpStatus.OK);
	}

	@PostMapping
	@Operation(summary = "adds the Master Entry")
	@ApiResponse(responseCode = "201", description = "Master Entry Created successfully")
	public ResponseEntity<DataMasterDTO> addMasterRecord(@RequestBody DataMasterDTO masterDTO,
			HttpServletRequest request) {
		DataMasterDTO createdMaster = dataMasterService.addMasterRecord(masterDTO, request);
		return new ResponseEntity<>(createdMaster, HttpStatus.CREATED);
	}

	@PutMapping("/{sharingId}")
	@Operation(summary = "updates the master entry")
	@ApiResponse(responseCode = "200", description = "updated master entry")
	public ResponseEntity<DataMasterDTO> updateMasterRecord(@PathVariable Integer sharingId,
			@RequestBody DataMasterDTO masterDTO, HttpServletRequest request) {
		try {
			DataMasterDTO updatedMaster = dataMasterService.updateMasterRecord(masterDTO, sharingId, request);
			return new ResponseEntity<>(updatedMaster, HttpStatus.OK);
		} catch (RuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

//     Not Exposing the End-point

//    @GetMapping("/{sharingId}")
//    public ResponseEntity<DataMaster> getMasterRecord(@PathVariable Integer sharingId) {
//        try {
//            DataMaster dataMaster = dataMasterService.getMasterRecord(sharingId);
//            return new ResponseEntity<>(dataMaster, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
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

@RestController
@RequestMapping("/api/datamaster")
public class DataMasterController {

    @Autowired
    private DataMasterService dataMasterService;

    @GetMapping
    public ResponseEntity<List<DataMasterDTO>> getMasterRecords() {
        List<DataMasterDTO> masterRecords = dataMasterService.getMasterRecords();
        return new ResponseEntity<>(masterRecords, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DataMasterDTO> addMasterRecord(@RequestBody DataMasterDTO masterDTO) {
        DataMasterDTO createdMaster = dataMasterService.addMasterRecord(masterDTO);
        return new ResponseEntity<>(createdMaster, HttpStatus.CREATED);
    }

    @PutMapping("/{sharingId}")
    public ResponseEntity<DataMasterDTO> updateMasterRecord(
            @PathVariable Integer sharingId,
            @RequestBody DataMasterDTO masterDTO) {
        try {
            DataMasterDTO updatedMaster = dataMasterService.updateMasterRecord(masterDTO, sharingId);
            return new ResponseEntity<>(updatedMaster, HttpStatus.OK);
        } catch (RuntimeException e) {
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
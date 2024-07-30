package com.track.share.datadetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data-details")
class DataDetailController {

    @Autowired
    private DataDetailService dataDetailService;

    @GetMapping
    public ResponseEntity<List<DataDetailDTO>> getAllDataDetails() {
        List<DataDetailDTO> dataDetails = dataDetailService.getAllDataDetails();
        return ResponseEntity.ok(dataDetails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataDetailDTO> getDataDetailById(@PathVariable Integer id) {
        DataDetailDTO dataDetail = dataDetailService.getDataDetailById(id);
        if (dataDetail != null) {
            return ResponseEntity.ok(dataDetail);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DataDetailDTO> addDataDetail(@RequestBody DataDetailDTO dataDetailDTO) {
        DataDetailDTO createdDataDetail = dataDetailService.addDataDetail(dataDetailDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDataDetail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DataDetailDTO> updateDataDetail(
            @PathVariable Integer id,
            @RequestBody DataDetailDTO dataDetailDTO) {
        DataDetailDTO updatedDataDetail = dataDetailService.updateDataDetail(id, dataDetailDTO);
        if (updatedDataDetail != null) {
            return ResponseEntity.ok(updatedDataDetail);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDataDetail(@PathVariable Integer id) {
        boolean deleted = dataDetailService.deleteDataDetail(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}


package com.track.share.datadetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
}

package com.track.share.pulldata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pull")
class DataPullController {

	@Autowired
	private DataPullService pullService;

	@GetMapping
	public String getData(HttpServletRequest request) {
		return pullService.getData(request.getRemoteAddr());
	}

}

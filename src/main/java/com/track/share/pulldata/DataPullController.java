package com.track.share.pulldata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/pull")
class DataPullController {

	@Autowired
	private DataPullService pullService;

	@GetMapping("/default")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public String getData(HttpServletRequest request) {
		return pullService.getData(request.getRemoteAddr());
	}

	@GetMapping("/default2")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public String getData2(HttpServletRequest request) {
		return pullService.getData2(request.getRemoteAddr());
	}

	@GetMapping("/default3")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public String getData3(HttpServletRequest request) {
		return pullService.getData3(request.getRemoteAddr());
	}

	@GetMapping("/default4")
	@Operation(security = { @SecurityRequirement(name = "bearer-key") })
	public String getData4(HttpServletRequest request) {
		return pullService.getData4(request.getRemoteAddr());
	}

}

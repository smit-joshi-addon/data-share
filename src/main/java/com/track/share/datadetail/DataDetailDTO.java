package com.track.share.datadetail;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Give the Proper Description")
public record DataDetailDTO(@Schema(requiredMode = RequiredMode.NOT_REQUIRED) Integer detailId, Integer masterId, // Assuming
																													// you
																													// only
																													// need
																													// the
																													// ID
																													// of
																													// the
																													// master
																													// data
		String secret, LocalDateTime validTill, LocalDateTime createdAt, String createdById, String createdByName,
		String createdByIp, Boolean status) {
}
package com.track.share.datamaster;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for managing data master records.")
public record DataMasterDTO(
		@Schema(description = "Optional unique identifier for sharing. This ID is auto-generated.", example = "1234") Integer sharingId,

		@Schema(description = "Required unique identifier for the associated business entity.", example = "5678") Integer businessId,

		@Schema(description = "Secret information associated with the data record. This value is auto-generated.") String secret,

		@Schema(description = "ID of the user who created the record. This field is auto-generated.", example = "user123") String createdById,

		@Schema(description = "IP address from which the record was created. This field is auto-generated.", example = "192.168.1.1") String createdByIp,

		@Schema(description = "Status of the data record. Indicates whether the token is active or inactive. Required value can be true or false.", example = "true") Boolean status,

		@Schema(description = "Timestamp when the record was created. This value is auto-generated.", example = "2024-08-02T12:34:56") LocalDateTime createdAt,

		@Schema(description = "Timestamp when the record was last updated. This value is auto-generated.", example = "2024-08-02T15:30:00") LocalDateTime updatedAt,

		@Schema(description = "Type of request associated with the data record. Required values are PULL, PUSH, or BOTH.", example = "PULL") RequestType type) {
}

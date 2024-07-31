package com.track.share.datamaster;

import java.time.LocalDateTime;

public record DataMasterDTO(
    Integer sharingId,
    Integer businessId,  // Assuming you only need the business ID, not the entire business object
    String secret,
    String ceatedById,
    String createdByIp,
    Boolean status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    RequestType type
) {
}

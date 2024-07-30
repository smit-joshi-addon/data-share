package com.track.share.datadetail;

import java.time.LocalDateTime;

public record DataDetailDTO(
        Integer detailId,
        Integer masterId, // Assuming you only need the ID of the master data
        String secret,
        LocalDateTime validTill,
        LocalDateTime createdAt,
        String createdById,
        String createdByName,
        String createdByIp,
        Boolean status
) {}
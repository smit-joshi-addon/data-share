package com.track.share.config.auth.dtos;

import java.time.LocalDateTime;

public record AuthResponse(String token, LocalDateTime expiry) {
}

package com.track.share.config.auth.dtos;

import java.util.Date;

public record AuthResponse (String token,Date expiry) {}

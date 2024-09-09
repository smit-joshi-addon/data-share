package com.track.share.analytics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.track.share.exceptions.UnauthorizedException;
import com.track.share.utility.JwtHelper;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AnalyticsScheduler analyticsScheduler;

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData(@RequestParam("token") String token) {
        Boolean isValid = jwtHelper.validateToken(token, jwtHelper.getUsernameFromToken(token));
        if (isValid) {
            String username = jwtHelper.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
            analyticsScheduler.addEmitter(emitter);

            emitter.onCompletion(() -> {
                // Clean up
                analyticsScheduler.stopScheduledTask();
            });
            emitter.onError(e -> {
                // Handle errors
                analyticsScheduler.stopScheduledTask();
            });

            return emitter;
        } else {
            throw new UnauthorizedException("Invalid token");
        }
    }
}

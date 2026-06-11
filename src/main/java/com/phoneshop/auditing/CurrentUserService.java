package com.phoneshop.auditing;

import com.phoneshop.security.JwtUtil;
import com.phoneshop.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CurrentUserService {
    private final HttpServletRequest request;
    public UUID getUserId() {
        return AuthUtils.getCurrentUserId();
    }

    public String getUsername() {
        return AuthUtils.getCurrentUsername();
    }
    // ✅ Get client IP address
    public String getIpAddress() {
        String ip = request.getHeader("X-Forwarded-For"); // if behind proxy/load balancer
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // If multiple IPs, take the first one
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
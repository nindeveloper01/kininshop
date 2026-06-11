package com.phoneshop.auditing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class AuditConfig {
    @Bean
    public AuditorAware<String> auditorAwareImpl(HttpServletRequest request) {
        return new AuditorAwareImpl(request);
    }
}
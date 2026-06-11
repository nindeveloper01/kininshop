package com.phoneshop.auditing;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class AuditContextService {
    @PersistenceContext
    private EntityManager entityManager;

    public void setAuditContext(UUID currentUserId, String ipAddress) {
        // PostgreSQL SET does not support parameters
        entityManager.createNativeQuery(
                "SET app.current_user_uuid = '" + currentUserId + "'"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "SET app.current_ip = '" + ipAddress + "'"
        ).executeUpdate();
    }
}

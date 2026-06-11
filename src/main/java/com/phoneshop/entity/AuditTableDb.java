package com.phoneshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_table_db")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTableDb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operation", nullable = false, length = 10)
    private String operation; // INSERT / UPDATE / DELETE

    @Column(name = "stamp", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private OffsetDateTime stamp;

    @Column(name = "db_user", nullable = false)
    private String dbUser;

    @Column(name = "db_table_name", nullable = false)
    private String dbTableName;

    @Column(name = "old_val", columnDefinition = "TEXT")
    private String oldVal;

    @Column(name = "new_val", columnDefinition = "TEXT")
    private String newVal;

    @Column(name = "db_table_row_id")
    private String dbTableRowId;

    @Column(name = "ip_address")
    private String ipAddress;
}
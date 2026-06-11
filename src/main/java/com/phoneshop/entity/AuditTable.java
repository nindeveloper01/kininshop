package com.phoneshop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "audit_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "row_id")
    private String rowId;

    @Column(name = "create_by", nullable = false)
    private UUID createBy;

    @Column(name = "create_date", columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime createDate;

    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "type")
    private String type; // INSERT / UPDATE / DELETE

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (status == null) status = true;
        if (isDeleted == null) isDeleted = false;
    }

}

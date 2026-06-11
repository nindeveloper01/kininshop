package com.phoneshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "files")
public class FileUpload extends BaseAuditEntity{
    @Column(name = "url")
    private String url;

    @Column (name = "file_name")
    private String fileName;

    public FileUpload() {}
    public FileUpload(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
}
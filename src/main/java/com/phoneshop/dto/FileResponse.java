package com.phoneshop.dto;

public record FileResponse<T>(
        int statusCode,
        String message,
        T results
){

}

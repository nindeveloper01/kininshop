package com.phoneshop.dto;

import com.phoneshop.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private User.Role role;
}

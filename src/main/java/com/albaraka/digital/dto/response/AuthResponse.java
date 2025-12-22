package com.albaraka.digital.dto.response;

import com.albaraka.digital.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;

    private String email;
    private String fullName;
    private Role role;
    private String accountNumber;
}

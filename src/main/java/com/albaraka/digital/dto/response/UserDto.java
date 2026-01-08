package com.albaraka.digital.dto.response;

import com.albaraka.digital.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String email;
    private String name;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}

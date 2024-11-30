package com.myplan.server.dto.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Setter
@Getter
public class ResponseUserDTO {

    private Long id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private String role;
}

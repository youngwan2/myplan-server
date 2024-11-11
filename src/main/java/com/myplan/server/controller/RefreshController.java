package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.service.RefreshService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshController {
    private final RefreshService refreshService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
            /* MEMO: Exception is GlobalException */
            String newAccess = refreshService.refresh(request, response);
            return ApiResponse.success("RefreshToken successfully created", HttpStatus.OK);
    }
}

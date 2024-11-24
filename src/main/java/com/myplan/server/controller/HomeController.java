package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.UserInfoDTO;
import com.myplan.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HomeController {
    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<ApiResponse<UserInfoDTO>> home(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfoDTO userInfoDTO= userService.getUsersByUsername(username);
        return ApiResponse.success(userInfoDTO, "성공적으로 조회되었습니다.", HttpStatus.OK) ;
    }
}

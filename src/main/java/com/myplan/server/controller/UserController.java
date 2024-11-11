package com.myplan.server.controller;

import com.myplan.server.config.response.ApiResponse;
import com.myplan.server.dto.PasswordUpdateRequestDTO;
import com.myplan.server.dto.UserDTO;
import com.myplan.server.exception.UserAlreadyExistsException;
import com.myplan.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ApiResponse.success("회원가입 성공", HttpStatus.CREATED);

        } catch (UserAlreadyExistsException ex) {
            log.error("User registration failed: {}", ex.getMessage());
            return ApiResponse.error("이미 존재하는 유저 입니다.", HttpStatus.BAD_REQUEST);
        }
    }


    // 로그인
    @PostMapping("/login")
    public String login(SecurityContext securityContext) {
        securityContext.getAuthentication().getDetails();
        return "로그인 되었습니다.";
    }

    //회원탈퇴
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("id") Long id) {

        try {
            userService.deleteUser(id);
            return ApiResponse.success("정상적으로 탈퇴되었습니다.", HttpStatus.NO_CONTENT);
        } catch (IllegalAccessException ex) {
            log.error(ex.getMessage());
            return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 비밀번호 수정
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> patchUser(@PathVariable("id") Long id, @Valid @RequestBody PasswordUpdateRequestDTO passwordDTO) {
            userService.updateUser(id, passwordDTO.getPassword());
            return ApiResponse.success("정상적으로 수정되었습니다.", HttpStatus.NO_CONTENT);
    }
}

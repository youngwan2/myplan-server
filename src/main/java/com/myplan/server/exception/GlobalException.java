package com.myplan.server.exception;


import com.myplan.server.config.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalException extends RuntimeException {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex){
        return ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ApiResponse.error("인증 실패: " + ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidTokenException(InvalidTokenException ex){
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex){
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(AlreadyExistsException ex){
        return ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponse<Void>> handleDateTimeParseException(DateTimeParseException ex){
        return ApiResponse.error("현재 날짜 범위를 벗어났습니다.", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        return ApiResponse.error(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

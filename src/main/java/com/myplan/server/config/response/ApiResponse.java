package com.myplan.server.config.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Setter
@NoArgsConstructor
public class ApiResponse<T> {

    private T data;
    private String message;
    private HttpStatus status;

    public ApiResponse(T data, String message, HttpStatus httpStatus) {
        this.data = data;
        this.message = message;
        this.status = httpStatus;
    }

    public ApiResponse(String message, HttpStatus httpStatus) {
        this.message = message;
        this.status = httpStatus;
        this.data = null;
    }

    // 성공 응답을 ResponseEntity로 반환
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, HttpStatus httpStatus) {
        ApiResponse<T> response = new ApiResponse<>(data, message, httpStatus);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    public static ResponseEntity<ApiResponse<Void>> success(String message, HttpStatus httpStatus) {
        ApiResponse<Void> response = new ApiResponse<>(message, httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }

    // 에러 응답을 ResponseEntity로 반환
    public static ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus httpStatus) {
        ApiResponse<Void> response = new ApiResponse<>(message, httpStatus);
        return new ResponseEntity<>(response, httpStatus);
    }
}

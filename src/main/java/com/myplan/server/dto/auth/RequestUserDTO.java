package com.myplan.server.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestUserDTO {

    @Size(min = 3, max = 15, message="name은 최소 3자 이상 15자 이하 이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]{3,15}$", message = "name 은 알파벳(소/대문자), 숫자만 포함되어야 합니다.")
    private String username;

    @NotBlank(message = "email 은 필수 입력값 입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다. test12@naver.com 와 같이 유효한 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "password 는 필수 입력값 입니다.")
    @Size(min = 8, message = "password 는 적어도 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$", message = "password 는 최소 8자 이상이며, 적어도 하나의 알파벳 문자와 하나의 숫자를 포함해야 합니다. 특수 문자(@$!%*?&)도 선택적으로 포함할 수 있습니다.")
    private String password;
}

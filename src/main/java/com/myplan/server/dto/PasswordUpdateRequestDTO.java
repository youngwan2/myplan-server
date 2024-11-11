package com.myplan.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class PasswordUpdateRequestDTO {

    @NotBlank(message = "비밀번호는 필수 항목 입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$", message = "password 는 최소 8자 이상이며, 적어도 하나의 알파벳 문자와 하나의 숫자를 포함해야 합니다. 특수 문자(@$!%*?&)도 선택적으로 포함할 수 있습니다.")
    private String password;

}

package com.myproject.mini_board.web.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDTO {

    // 로그인 요청 시 아이디와 비밀번호는 필수값이므로, 비어있지 않은지 검증합니다.
    @NotBlank(message = "아이디를 입력해주세요.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    public UserLoginDTO(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
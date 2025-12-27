package com.myproject.mini_board.web.dto.user;

import com.myproject.mini_board.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRegisterDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 12자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영문 또는 숫자만 사용 가능합니다.")
    private String loginId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 6, max = 16, message = "비밀번호는 6자 이상 16자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    private String username;

    public UserRegisterDTO(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
    }

}

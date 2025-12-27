package com.myproject.mini_board.web.dto.user;

import com.myproject.mini_board.domain.user.Role;
import com.myproject.mini_board.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class UserResponseDTO {
    private Long id;

    private String loginId;

    private String password;

    private String username;

    private Role role;

    public UserResponseDTO(Long id, String loginId, String password, String username, Role role) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.loginId = user.getLoginId();
        this.password = user.getPassword();
        this.username = user.getUsername();
        this.role = user.getRole();
    }
}

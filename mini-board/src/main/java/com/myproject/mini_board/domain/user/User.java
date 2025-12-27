// [리팩토링 제안]
// 1. 다른 도메인 객체와의 일관성 및 코드 중복 제거를 위해 BaseEntity를 상속받는 것을 고려해 보세요.
//    - `private Long id;` 필드가 BaseEntity에 이미 존재하므로 제거할 수 있습니다.
//    - BaseEntity를 상속하면 createdAt, updatedAt 필드도 함께 관리할 수 있게 됩니다.
//    - 예: `public class User extends BaseEntity { ... }`
package com.myproject.mini_board.domain.user;

import com.myproject.mini_board.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class User extends BaseEntity {


    private String loginId;

    private String password;

    private String username;

    private Role role;

    public User(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.role = Role.USER;
    }

    public User(Long id, String loginId, String password, String username, Role role) {
        super(id);
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
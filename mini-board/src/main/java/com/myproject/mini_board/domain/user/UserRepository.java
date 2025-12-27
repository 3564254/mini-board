package com.myproject.mini_board.domain.user;

import com.myproject.mini_board.domain.post.Post;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);

    // [리팩토링 제안]
    // findById와 같이 단일 객체를 조회하는 메서드는 결과가 없을 경우 null을 반환하기보다 Optional<User>를 반환하는 것이 좋습니다.
    // 이는 클라이언트 코드에서 null 체크를 강제하여 NullPointerException(NPE)을 예방하는 데 도움이 됩니다.
    // 예: `Optional<User> findById(Long id);`
    User findById(Long id);

    // [리팩토링 제안]
    // findById와 마찬가지로, findByLoginId 또한 Optional<User>를 반환 타입으로 사용하는 것을 고려해 보세요.
    // 예: `Optional<User> findByLoginId(String loginId);`
    User findByLoginId(String loginId);
    String findUsernameById(Long id);

    void update(User user);

    void delete(Long id);

}

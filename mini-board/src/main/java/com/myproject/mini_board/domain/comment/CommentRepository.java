package com.myproject.mini_board.domain.comment;

import com.myproject.mini_board.web.dto.comment.CommentResponseDTO;

import java.util.List;

public interface CommentRepository {
    void save(Comment comment);

    // [리팩토링 제안]
    // findById와 같이 단일 객체를 조회하는 메서드는 결과가 없을 경우 null을 반환하기보다 Optional<Comment>를 반환하는 것이 좋습니다.
    // 이는 클라이언트 코드에서 null 체크를 강제하여 NullPointerException(NPE)을 예방하는 데 도움이 됩니다.
    // 예: `Optional<Comment> findById(Long id);`
    Comment findById(Long id);

    List<Comment> findByPostId(Long postId);

    List<CommentResponseDTO> findByPostIdWithname(Long postId);

    List<Comment> findAll();

    void update(Comment comment);

    void delete(Long id);

}

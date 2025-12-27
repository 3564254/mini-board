package com.myproject.mini_board.domain.post;

import com.myproject.mini_board.web.dto.post.PostResponseDTO;
import com.myproject.mini_board.web.dto.post.PostSearchCond;

import java.util.List;

public interface PostRepository {
    Post save(Post post);

    Post findById(Long id);
    PostResponseDTO findByIdWithName(Long id);

    List<PostResponseDTO> searchPosts(PostSearchCond cond, int page);
    List<PostResponseDTO> findAll(int page);

    void update(Post post);

    void delete(Long id);

    int count();

    int searchCount(PostSearchCond cond);

    void increaseLikeCount(Long id);

    void decreaseLikeCount(Long id);
    void increaseDislikeCount(Long id);
    void decreaseDislikeCount(Long id);
}
package com.myproject.mini_board.web.dto.post;


import com.myproject.mini_board.domain.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class PostResponseDTO {
    private Long id;

    private String title;

    private String content;

    private Long userId;

    private String username;

    private Long likeCount;
    private Long dislikeCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }

    public PostResponseDTO(Post post, String username) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userId = post.getUserId();
        this.username = username;
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}

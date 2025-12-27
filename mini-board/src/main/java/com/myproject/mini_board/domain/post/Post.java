package com.myproject.mini_board.domain.post;

import com.myproject.mini_board.domain.BaseEntity;
import lombok.*;


@Getter @Setter
public class Post extends BaseEntity {
    private String title;
    private String content;
    private Long userId;
    private Long likeCount;
    private Long dislikeCount;

    public Post() {
    }

    public Post(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }
}

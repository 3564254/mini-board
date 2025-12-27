package com.myproject.mini_board.domain.comment;

import com.myproject.mini_board.domain.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class Comment extends BaseEntity {
    // 어떤 게시글(Post)에 달린 댓글인지 식별하기 위한 ID 입니다.
    private Long postId;
    private String content;
    // 어떤 사용자(User)가 작성한 댓글인지 식별하기 위한 ID 입니다.
    private Long userId;

    public Comment() {
    }

    // 댓글을 새로 생성할 때 사용하는 생성자입니다.
    public Comment(Long postId, String content, Long userId) {
        this.postId = postId;
        this.content = content;
        this.userId = userId;
    }


    public Comment(Long id, Long postId, String content) {
        super(id);
        this.postId = postId;
        this.content = content;
    }



}
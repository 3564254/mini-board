package com.myproject.mini_board.web.dto.comment;

import com.myproject.mini_board.domain.comment.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class CommentResponseDTO {
    private Long id;
    private String content;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDTO(Comment comment){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.createdAt = comment.getCreatedAt();
    }

    public CommentResponseDTO(Comment comment, String username) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUserId();
        this.username = username;
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}

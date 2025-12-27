package com.myproject.mini_board.web.dto.comment;


import com.myproject.mini_board.domain.comment.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "내용을 입력해 주세요.")
    @Size(max = 100, message = "내용은 100자 이내로 입력해 주세요")
    private String content;

    public CommentRequestDTO(Comment comment) {
        this.content = comment.getContent();
    }
}

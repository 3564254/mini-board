package com.myproject.mini_board.web.dto.post;

import com.myproject.mini_board.domain.post.Post;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class PostRequestDTO {

    @NotBlank(message = "제목을 입력해 주세요")
    @Size(max = 50, message = "제목은 50자 이내로 입력해 주세요.")
    private String title;


    @Size(max = 5000, message = "내용은 100자 이내로 입력해 주세요")
    private String content;


    public PostRequestDTO(Post post) {
        this.title = post.getTitle();
        this.content = post.getContent();
    }
}
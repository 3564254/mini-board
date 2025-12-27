package com.myproject.mini_board.web.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostSearchCond {
    private String title;
    private String content;
    private String username;
    private String comment;
}

package com.myproject.mini_board.domain.vote;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public abstract class Vote {
    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
}

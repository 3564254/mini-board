package com.myproject.mini_board.domain.vote.post;

import com.myproject.mini_board.domain.vote.Vote;
import com.myproject.mini_board.domain.vote.VoteType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PostVote extends Vote {
    private Long postId;
    private VoteType voteType;

    public PostVote(Long postId, VoteType voteType) {
        this.postId = postId;
        this.voteType = voteType;
    }
}

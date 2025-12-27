package com.myproject.mini_board.service;

import com.myproject.mini_board.domain.post.PostRepository;
import com.myproject.mini_board.domain.vote.VoteType;
import com.myproject.mini_board.domain.vote.post.PostVote;
import com.myproject.mini_board.domain.vote.post.PostVoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostVoteService {
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;


    public void toggleVote(Long postId, Long userId, VoteType requestType) {
        PostVote existingVote = postVoteRepository.getPostVote(postId, userId);

        // Case 1: 신규 투표 (기록이 없음)
        if (existingVote == null) {
            handleNewVote(postId, userId, requestType);
            return;
        }

        VoteType currentType = existingVote.getVoteType();

        // Case 2: 투표 취소 (같은 버튼 클릭)
        if (currentType == requestType) {
            handleCancelVote(postId, userId, currentType);
        }
        // Case 3: 투표 변경 (다른 버튼 클릭: 좋아요 <-> 싫어요)
        else {
            handleSwitchVote(postId, userId, currentType, requestType);
        }
    }

    private void handleNewVote(Long postId, Long userId, VoteType newType) {
        if (newType == VoteType.LIKE) {
            postRepository.increaseLikeCount(postId);
        } else {
            postRepository.increaseDislikeCount(postId);
        }
        postVoteRepository.saveVote(postId, userId, newType);
    }

    private void handleCancelVote(Long postId, Long userId, VoteType currentType) {
        if (currentType == VoteType.LIKE) {
            postRepository.decreaseLikeCount(postId);
        } else {
            postRepository.decreaseDislikeCount(postId);
        }
        postVoteRepository.deleteVote(postId, userId);
    }


    private void handleSwitchVote(Long postId, Long userId, VoteType oldType, VoteType newType) {
        if (oldType == VoteType.LIKE) {
            postRepository.decreaseLikeCount(postId);
        } else {
            postRepository.decreaseDislikeCount(postId);
        }

        // 2. 새로운 집계 증가
        if (newType == VoteType.LIKE) {
            postRepository.increaseLikeCount(postId);
        } else {
            postRepository.increaseDislikeCount(postId);
        }

        postVoteRepository.updateVote(postId, userId, newType);
    }
}

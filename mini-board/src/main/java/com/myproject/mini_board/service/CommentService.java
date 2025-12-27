package com.myproject.mini_board.service;

import com.myproject.mini_board.domain.comment.Comment;
import com.myproject.mini_board.domain.comment.CommentRepository;
import com.myproject.mini_board.domain.user.Role;
import com.myproject.mini_board.domain.user.User;
import com.myproject.mini_board.domain.user.UserRepository;
import com.myproject.mini_board.global.exception.DifferentUserException;
import com.myproject.mini_board.global.exception.NotFoundException;
import com.myproject.mini_board.web.dto.comment.CommentRequestDTO;
import com.myproject.mini_board.web.dto.comment.CommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseDTO create(Long postId, CommentRequestDTO dto, Long userId) {
        Comment comment = new Comment(postId, dto.getContent(), userId);
        commentRepository.save(comment);
        return new CommentResponseDTO(comment);
    }

    public CommentResponseDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) {
            throw new NotFoundException("댓글을 찾을 수 없습니다 id: " + id);
        }
        return new CommentResponseDTO(comment);
    }


    public List<CommentResponseDTO> getByPostId(Long postId) {
        List<Comment> commentList = commentRepository.findByPostId(postId);
        List<CommentResponseDTO> commentResponseDTOS = new ArrayList<>();

        for (Comment comment : commentList) {
            commentResponseDTOS.add(new CommentResponseDTO(comment, userRepository.findUsernameById(comment.getUserId())));
        }
        return commentResponseDTOS;
    }

    public List<CommentResponseDTO> getByPostIdWithUsername(Long postId) {
        return commentRepository.findByPostIdWithname(postId);
    }

    @Transactional
    public CommentResponseDTO update(Long id, CommentRequestDTO dto, Long userId) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) throw new NotFoundException("댓글을 찾을 수 없습니다.");
        
        // 수정은 작성자 본인만 가능
        if (!comment.getUserId().equals(userId)) throw new DifferentUserException("수정 권한 없음");
        
        comment.setContent(dto.getContent());
        commentRepository.update(comment);
        return new CommentResponseDTO(comment);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Comment comment = commentRepository.findById(id);
        if (comment == null) throw new NotFoundException("댓글을 찾을 수 없습니다.");

        User user = userRepository.findById(userId);
        if (user == null) throw new NotFoundException("사용자를 찾을 수 없습니다.");

        // 작성자 본인이거나 관리자인 경우 삭제 가능
        if (!comment.getUserId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new DifferentUserException("삭제 권한 없음");
        }

        commentRepository.delete(id);
    }



}

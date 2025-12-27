package com.myproject.mini_board.service;

import com.myproject.mini_board.domain.post.Post;
import com.myproject.mini_board.domain.post.PostRepository;
import com.myproject.mini_board.domain.user.Role;
import com.myproject.mini_board.domain.user.User;
import com.myproject.mini_board.domain.user.UserRepository;
import com.myproject.mini_board.global.exception.DifferentUserException;
import com.myproject.mini_board.global.exception.NotFoundException;
import com.myproject.mini_board.web.dto.page.PageResponseDTO;
import com.myproject.mini_board.web.dto.post.PostRequestDTO;
import com.myproject.mini_board.web.dto.post.PostResponseDTO;
import com.myproject.mini_board.web.dto.post.PostSearchCond;
import com.myproject.mini_board.web.dto.post.SearchTarget;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {
    // jdbc
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Transactional
    public PostResponseDTO create(PostRequestDTO dto, Long userId) {
        // PostRequestDTO : Title, Content
        Post existingPost = new Post(dto.getTitle(), dto.getContent(), userId);
        log.info("게시물 생성 시작 postId={}, postTitle={}, userId={}", existingPost.getId(), existingPost.getTitle(), userId);
        Post newPost = postRepository.save(existingPost);
        log.info("게시물 생성 성공 postId={}, postTitle={}", newPost.getId(), newPost.getTitle());
        return new PostResponseDTO(newPost);
    }

    public PostResponseDTO get(Long id) {
        PostResponseDTO post = postRepository.findByIdWithName(id);
        if (post == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        return post;
    }

    public PageResponseDTO<PostResponseDTO> getList(int page) {
        int totalCount = postRepository.count();
        List<PostResponseDTO> posts = postRepository.findAll(page);
        return new PageResponseDTO<>(posts, page, totalCount);
    }

    public PostResponseDTO getPostWithUsername(Long id) {
        PostResponseDTO post = postRepository.findByIdWithName(id);
        if (post == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다. ID: " + id);
        }
        return post;
    }

    public PageResponseDTO<PostResponseDTO> search(String keyword, String target, int page) {
        PostSearchCond cond = new PostSearchCond();

        if (target.equals(SearchTarget.TITLE.toString())) {
            cond.setTitle(keyword);
        }
        else if (target.equals(SearchTarget.TITLE_CONTENT.toString())) {
            cond.setTitle(keyword);
            cond.setContent(keyword);
        }
        else if (target.equals(SearchTarget.CONTENT.toString())) {
            cond.setContent(keyword);
        }
        else if (target.equals(SearchTarget.COMMENT.toString())) {
            cond.setComment(keyword);
        }
        else if (target.equals(SearchTarget.USERNAME.toString())) {
            cond.setUsername(keyword);
        }

        List<PostResponseDTO> posts =  postRepository.searchPosts(cond,page);
        int totalCount = postRepository.searchCount(cond);
        return new PageResponseDTO<>(posts, page, totalCount);
    }


    @Transactional
    public PostResponseDTO update(Long id, PostRequestDTO dto, Long userId) {
        Post existingPost = findPostAndCheckPermission(id, userId);

        existingPost.setContent(dto.getContent());
        existingPost.setTitle(dto.getTitle());
        postRepository.update(existingPost);
         return new PostResponseDTO(existingPost);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Post existingPost = postRepository.findById(id);
        if (existingPost == null) throw new NotFoundException("게시글을 찾을 수 없습니다. ID: " + id);

        User user = userRepository.findById(userId);
        if (user == null) throw new NotFoundException("사용자를 찾을 수 없습니다.");

        // 작성자 본인이거나 관리자인 경우 삭제 가능
        if (!existingPost.getUserId().equals(userId) && user.getRole() != Role.ADMIN) {
            throw new DifferentUserException("권한 없음");
        }
        
        postRepository.delete(id);
    }

    private Post findPostAndCheckPermission(Long id, Long userId) {
        Post existingPost = postRepository.findById(id);
        if (existingPost == null) throw new NotFoundException("게시글을 찾을 수 없습니다. ID: " + id);
        
        // 수정은 작성자 본인만 가능
        if (!existingPost.getUserId().equals(userId)) throw new DifferentUserException("권한 없음");
        return existingPost;
    }


}
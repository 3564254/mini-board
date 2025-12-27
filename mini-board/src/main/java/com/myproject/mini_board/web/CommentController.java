package com.myproject.mini_board.web;

import com.myproject.mini_board.global.utils.SessionConst;
import com.myproject.mini_board.service.CommentService;
import com.myproject.mini_board.service.PostService;
import com.myproject.mini_board.web.dto.comment.CommentRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/posts/{postId}")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    // 게시글 내 댓글 조회
    @GetMapping("/comments")
    public String getCommentsByPostId(@PathVariable Long postId, Model model) {
        model.addAttribute("commentList", commentService.getByPostId(postId));
        return "comments/list";
    }

    @PostMapping("/comments")
    public String createComment(@PathVariable Long postId,
                                @ModelAttribute @Valid CommentRequestDTO dto,
                                BindingResult bindingResult,
                                @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                                Model model,
                                RedirectAttributes rd) {
        log.info("댓글 생성 시도: postId={}, content={}", postId, dto.getContent());

        if (bindingResult.hasErrors()) {
            log.info("댓글 검증 오류 발생: {}", bindingResult);
            model.addAttribute("commentList", commentService.getByPostId(postId));
            model.addAttribute("post", postService.getPostWithUsername(postId));
            return  "posts/detail";
        }
        commentService.create(postId, dto, userId);
        rd.addFlashAttribute("status", true);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/edit")
    public String updateComment(@PathVariable Long postId,
                                @PathVariable Long id,
                                @Valid CommentRequestDTO dto,
                                BindingResult bindingResult,
                                @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                                Model model,
                                RedirectAttributes rd) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentList", commentService.getByPostId(postId));
            model.addAttribute("commentForm", dto);
            return "comments/list";
        }
        rd.addAttribute("comment", commentService.update(id, dto, userId));
        rd.addFlashAttribute("status", true);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long postId,
                                @PathVariable Long id,
                                @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                                RedirectAttributes rd) {
        commentService.delete(id, userId);
        rd.addFlashAttribute("status", true);
        return "redirect:/posts/" + postId;
    }
}

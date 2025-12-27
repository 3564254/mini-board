package com.myproject.mini_board.web;


import com.myproject.mini_board.domain.vote.VoteType;
import com.myproject.mini_board.global.utils.SessionConst;
import com.myproject.mini_board.service.CommentService;
import com.myproject.mini_board.service.PostService;
import com.myproject.mini_board.service.PostVoteService;
import com.myproject.mini_board.web.dto.page.PageResponseDTO;
import com.myproject.mini_board.web.dto.post.PostRequestDTO;
import com.myproject.mini_board.web.dto.post.PostResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {
    private final PostService postService;
    private final PostVoteService postVoteService;
    private final CommentService commentService;

    // 메인 페이지 - 게시글 목록 조회
    @GetMapping
    public String getPosts(@RequestParam(defaultValue = "1") int page,
                              HttpServletResponse response,
                              Model model) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        PageResponseDTO<PostResponseDTO> pageResult = postService.getList(page);
        model.addAttribute("posts", pageResult.getContent()); // 글 목록
        model.addAttribute("pageInfo", pageResult);           // 페이지 정보 (1,2,3 버튼용)

        return "posts/list";
    }



    // 게시글 클릭 - 게시글 상세 조회
    @GetMapping("/{postId}")
    public String findPostById(@PathVariable Long postId,
                               HttpServletResponse response,
                               Model model) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        model.addAttribute("post", postService.get(postId));
        model.addAttribute("commentList", commentService.getByPostIdWithUsername(postId));

        return "posts/detail";
    }

    // 게시글 - 검색
    @GetMapping("/search")
    public String searchPost(@RequestParam(defaultValue = "1") int page,
                             @RequestParam String search_keyword,
                             @RequestParam String search_target,
                             HttpServletResponse response, // [추가] 캐시 방지용
                             Model model) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        PageResponseDTO<PostResponseDTO> pageResult = postService.search(search_keyword, search_target,page);
        model.addAttribute("posts", pageResult.getContent()); // 글 목록
        model.addAttribute("pageInfo", pageResult);           // 페이지 정보 (1,2,3 버튼용)

        model.addAttribute("search_keyword", search_keyword);
        model.addAttribute("search_target", search_target);

        return "posts/list";
    }


    // 글쓰기 버튼 - 게시글 생성 폼 이동
    @GetMapping("/new")
    public String createPost(Model model) {
        model.addAttribute("postRequestDTO", new PostRequestDTO());
        model.addAttribute("post", null);
        return "posts/form";
    }

    // 게시글 생성
    @PostMapping()
    public String createPost(@Valid PostRequestDTO requestDTO,
                             BindingResult bindingResult,
                             @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                             RedirectAttributes rd,
                             Model model) {
        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생: {}", bindingResult);
            model.addAttribute("post", null);
            return "posts/form";
        }

        PostResponseDTO responseDTO = postService.create(requestDTO, userId);
        rd.addFlashAttribute("status", true);
        return "redirect:/posts";
    }

    // 수정 버튼 - 게시글 수정 폼 이동
    @GetMapping("/{postId}/edit")
    public String updatePost(@PathVariable Long postId, Model model) {
        PostResponseDTO responseDTO = postService.getPostWithUsername(postId);

        PostRequestDTO requestDTO = new PostRequestDTO();
        requestDTO.setTitle(responseDTO.getTitle());
        requestDTO.setContent(responseDTO.getContent());

        model.addAttribute("postRequestDTO", requestDTO);
        model.addAttribute("post", responseDTO);
        return "posts/form";
    }

    @PostMapping("/{postId}/edit")
    public String updatePost(@PathVariable Long postId,
                             @Valid PostRequestDTO requestDTO,
                             BindingResult bindingResult,
                             Model model,
                             @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                             RedirectAttributes rd) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postService.get(postId));
            return "posts/form";
        }

        PostResponseDTO responseDTO = postService.update(postId, requestDTO, userId);
        rd.addFlashAttribute("status", true);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId,
                             @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId,
                             RedirectAttributes rd) {
        postService.delete(postId, userId);
        rd.addFlashAttribute("status", true);
        return "redirect:/posts";
    }


    @ResponseBody
    @PostMapping("/{postId}/vote")
    public String vote(@PathVariable Long postId,
                       @RequestParam String type,
                       @SessionAttribute(SessionConst.LOGIN_USER_ID) Long userId) {
        postVoteService.toggleVote(postId, userId, VoteType.valueOf(type.toUpperCase()));
        return "ok";
    }
}

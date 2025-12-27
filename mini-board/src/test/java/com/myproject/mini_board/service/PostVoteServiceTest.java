package com.myproject.mini_board.service;
import com.myproject.mini_board.domain.post.PostRepository;
import com.myproject.mini_board.web.dto.page.PageResponseDTO;
import com.myproject.mini_board.web.dto.post.PostResponseDTO;
import com.myproject.mini_board.web.dto.post.PostSearchCond;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostVoteServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("검색 기능이 리포지토리를 잘 호출하고 결과를 주는지 테스트")
    void searchTest() {
        // 1. Given
        String keyword = "테스트";
        String target = "TITLE";
        int page = 1;


        List<PostResponseDTO> fakeList = new ArrayList<>();
        fakeList.add(new PostResponseDTO());

        given(postRepository.searchPosts(any(PostSearchCond.class), anyInt()))
                .willReturn(fakeList);

        given(postRepository.searchCount(any(PostSearchCond.class)))
                .willReturn(10);

        // 2. When (실행)
        PageResponseDTO<PostResponseDTO> result = postService.search(keyword, target, page);

        // 3. Then (검증)
        assertThat(result).isNotNull();

        assertThat(result.getContent().size()).isEqualTo(1);


        verify(postRepository).searchPosts(any(PostSearchCond.class), anyInt());
    }
}
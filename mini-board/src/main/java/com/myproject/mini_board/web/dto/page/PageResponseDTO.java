package com.myproject.mini_board.web.dto.page;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PageResponseDTO<T> {
    private List<T> content;    // 글 목록
    private int currentPage;      // 현재 페이지 (1부터 시작)
    private int totalPages;       // 전체 페이지 수
    private int totalItems;       // 전체 글 개수
    private int startPage;        // 네비게이션 바 시작 번호 (예: 1, 6, 11...)
    private int endPage;          // 네비게이션 바 끝 번호 (예: 5, 10, 15...)

    public PageResponseDTO(List<T> content, int currentPage, int totalItems) {
        int size = PageConst.SIZE;
        this.content = content;
        this.currentPage = currentPage;
        this.totalItems = totalItems;

        // 전체 페이지 수 계산
        this.totalPages = totalItems%size == 0 ? totalItems/size : totalItems/size+1;

        // 하단 페이지 바 계산 (한 번에 5개씩 보여주기: 1~5, 6~10 ...)
        // 복잡해 보이지만 '현재 페이지가 속한 그룹'을 찾는 공식입니다.
        int navSize = 5;
        this.startPage = (currentPage-1)/navSize * navSize + 1;
        this.endPage = Math.min(this.startPage + navSize - 1, this.totalPages);

    }
}

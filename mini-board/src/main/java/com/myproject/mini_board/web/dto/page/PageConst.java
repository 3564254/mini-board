package com.myproject.mini_board.web.dto.page;


public class PageConst {
    public static final Integer SIZE = 10;

    public static int getOffset(int page) {
        return (page - 1) * SIZE;
    }
}

package com.myproject.mini_board.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFoundException(NotFoundException ex, Model model) {
        log.error("[NotFoundException] {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404"; // 404 오류 페이지로 이동
    }

    @ExceptionHandler(DifferentUserException.class)
    public String handleDifferentUserException(DifferentUserException ex, Model model) {
        log.error("[DifferentUserException] {}", ex.getMessage());
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/403"; // 403 오류 페이지로 이동
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error("[Exception] {}", ex.getMessage());
        model.addAttribute("errorMessage", "서버 내부 오류가 발생했습니다.");
        return "error/500"; // 500 오류 페이지로 이동
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}

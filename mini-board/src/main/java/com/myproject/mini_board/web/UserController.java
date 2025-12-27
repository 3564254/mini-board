package com.myproject.mini_board.web;

import com.myproject.mini_board.global.utils.SessionConst;
import com.myproject.mini_board.service.UserService;
import com.myproject.mini_board.web.dto.user.UserLoginDTO;
import com.myproject.mini_board.web.dto.user.UserRegisterDTO;
import com.myproject.mini_board.web.dto.user.UserRequestDTO;
import com.myproject.mini_board.web.dto.user.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userRegisterDTO", new UserRegisterDTO());
        return "users/registerForm";
    }

    @PostMapping("/register")
    public String register(@Valid UserRegisterDTO registerDTO, BindingResult bindingResult) {
        log.info("loginId={}, password={}, username={}", registerDTO.getLoginId(), registerDTO.getPassword(), registerDTO.getUsername());
        if (bindingResult.hasErrors()) {
            return "users/registerForm";
        }
        try {
            userService.register(registerDTO);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("registerFail", e.getMessage());
            return "users/registerForm";
        }
        return "redirect:/users/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "users/loginForm";
    }

    @PostMapping("/login")
    public String login(@Valid UserLoginDTO loginDTO,
                        BindingResult bindingResult,
                        HttpSession session) {
        log.info("로그인 시도 userLoginId={}", loginDTO.getLoginId());
        if (bindingResult.hasErrors()) {
            return "users/loginForm";
        }
        try {
            UserResponseDTO loginUser = userService.login(loginDTO);

            session.setAttribute(SessionConst.LOGIN_USER_ID, loginUser.getId());
            session.setAttribute(SessionConst.LOGIN_USER_NAME, loginUser.getUsername());
            log.info("세션에 회원정보 저장 loginUserId={}, loginusername={}", loginUser.getId(), loginUser.getUsername());
            return "redirect:/posts";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("loginFail", e.getMessage());
            return "users/loginForm";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) session.invalidate(); // 세션 무효화
        return "redirect:/";
    }

    @GetMapping("/update")
    public String updateForm(@SessionAttribute(value = SessionConst.LOGIN_USER_ID, required = false) Long userId, Model model) {
        model.addAttribute("user", userService.findById(userId));
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "users/updateForm";
    }

    @PostMapping("/update")
    public String updateUser(@Valid UserRequestDTO updateDTO,
                             BindingResult bindingResult,
                             @SessionAttribute(value = SessionConst.LOGIN_USER_ID, required = false) Long userId,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userService.findById(userId));
            return "users/updateForm";
        }

        userService.updateUser(userId, updateDTO);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteUser(@SessionAttribute(value = SessionConst.LOGIN_USER_ID, required = false) Long userId,
                             HttpSession session) {

        userService.deleteUser(userId);
        if (session != null) session.invalidate();

        return "redirect:/";
    }
}

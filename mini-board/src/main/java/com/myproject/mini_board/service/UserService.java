package com.myproject.mini_board.service;

import com.myproject.mini_board.domain.user.User;
import com.myproject.mini_board.domain.user.UserRepository;
import com.myproject.mini_board.web.dto.user.UserLoginDTO;
import com.myproject.mini_board.web.dto.user.UserRegisterDTO;
import com.myproject.mini_board.web.dto.user.UserRequestDTO;
import com.myproject.mini_board.web.dto.user.UserResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public UserResponseDTO register(UserRegisterDTO registerDTO) {
        User existingUser = userRepository.findByLoginId(registerDTO.getLoginId());
        if (existingUser != null) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        log.info("암호화된 비밀번호: " + encodedPassword);
        User newUser = userRepository.save(new User(registerDTO.getLoginId(), encodedPassword, registerDTO.getUsername()));
        log.info("register 완료: id={}, LoginId={}, username={}",newUser.getId(), registerDTO.getLoginId(), registerDTO.getUsername());
        return new UserResponseDTO(newUser);
    }

    @Transactional
    public UserResponseDTO login(UserLoginDTO loginDTO) {
        User user = userRepository.findByLoginId(loginDTO.getLoginId());
        if (user == null) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        log.info("로그인 성공 loginId={}", loginDTO.getLoginId());
        return new UserResponseDTO(user);
    }

    public UserResponseDTO findById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다.");
        }
        return new UserResponseDTO(user);
    }

    @Transactional
    public void updateUser(Long userId, UserRequestDTO requestDTO) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다.");
        }
        String encodedPassword = passwordEncoder.encode(requestDTO.getPassword());

        user.setUsername(requestDTO.getUsername());
        user.setPassword(encodedPassword);
        userRepository.update(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다.");
        }
        userRepository.delete(userId);
    }

}


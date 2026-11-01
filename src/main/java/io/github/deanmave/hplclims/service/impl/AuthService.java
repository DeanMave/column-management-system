package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.auth.AuthRequestDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.domain.mapper.UserMapper;
import io.github.deanmave.hplclims.repository.UserRepository;
import io.github.deanmave.hplclims.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    public AuthRequestDto.AuthResponse login(AuthRequestDto.LoginRequest request) {
        log.info("Попытка аутентификации пользователя с логином: {}", request.login());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                )
        );

        User user = userRepository.findByLogin(request.login())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден после успешной аутентификации"));

        String jwtToken = tokenProvider.generateToken(user);

        log.info("Пользователь {} успешно авторизован, токен сгенерирован", request.login());

        UserResponseDto userResponseDto = userMapper.toUserResponseDto(user);

        return AuthRequestDto.AuthResponse.builder()
                .token(jwtToken)
                .user(userResponseDto)
                .build();
    }
}
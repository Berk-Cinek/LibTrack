package com.berk.libtrack.security.services.impl;

import com.berk.libtrack.domain.dto.LoginRequest;
import com.berk.libtrack.domain.dto.LoginResponse;
import com.berk.libtrack.domain.dto.RegisterRequest;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.exceptions.AuthorizationFailedException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.UserRepository;
import com.berk.libtrack.security.services.JwtService;
import com.berk.libtrack.security.services.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public void register(RegisterRequest request) {
        MemberEntity memberRef = new MemberEntity();
        memberRef.setId(request.getMemberId());

        UserEntity user = UserEntity.builder()
                .memberEntity(memberRef)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("MEMBER")
                .build();

        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthorizationFailedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    @Override
    public void promoteToAdmin(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setRole("ADMIN");
        userRepository.save(user);
    }
}
package com.berk.libtrack.security.services.impl;

import com.berk.libtrack.domain.dto.LoginRequest;
import com.berk.libtrack.domain.dto.LoginResponse;
import com.berk.libtrack.domain.dto.RegisterRequest;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.exceptions.AuthorizationFailedException;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.MemberRepository;
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
    private final MemberRepository memberRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService, MemberRepository memberRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataIntegrityException("This username is already taken.");
        }

        MemberEntity member = memberRepository.findByMemberNo(request.getMemberNo())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No member found with member number: " + request.getMemberNo()));

        if (userRepository.existsByMemberEntity_Id(member.getId())) {
            throw new DataIntegrityException("This member already has an account.");
        }

        UserEntity user = UserEntity.builder()
                .memberEntity(member)
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
                .memberId(user.getMemberEntity().getId())
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

    @Override
    public Long getMemberIdForUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getMemberEntity().getId();
    }
}
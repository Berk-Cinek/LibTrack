package com.berk.libtrack.security.services.impl;

import com.berk.libtrack.domain.dto.LoginRequest;
import com.berk.libtrack.domain.dto.RegisterRequest;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.exceptions.AuthorizationFailedException;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.repositories.UserRepository;
import com.berk.libtrack.security.services.AuthService;
import com.berk.libtrack.security.services.JwtService;
import liquibase.license.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthService authService;

    @Test
    void register_rejectsTakenUsername() {
        RegisterRequest request = RegisterRequest.builder()
                .memberNo(6161L)
                .username("berk")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("berk")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DataIntegrityException.class)
                .hasMessageContaining("username is already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_rejectsMembersWithExistingAccount(){
        RegisterRequest request = RegisterRequest.builder()
                .memberNo(6161L)
                .username("berk")
                .password("secret")
                .build();

        MemberEntity member = new MemberEntity();
        member.setMemberNo(6161L);
        member.setId(4L);

        when(userRepository.existsByUsername("berk")).thenReturn(false);
        when(memberRepository.findByMemberNo(6161L)).thenReturn(Optional.of(member));
        when(userRepository.existsByMemberEntity_Id(4L)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DataIntegrityException.class)
                .hasMessageContaining("member already has an account");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_rejectsUnknownMemberNo(){
        RegisterRequest request = RegisterRequest.builder()
                .memberNo(6161L)
                .username("berk")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("berk")).thenReturn(false);
        when(memberRepository.findByMemberNo(6161L)).thenReturn(Optional.empty());
        when(userRepository.existsByMemberEntity_Id(4L)).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No member Found");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashedPasswordAndAssignsMemberRole(){
        RegisterRequest request = RegisterRequest.builder()
                .memberNo(6161L)
                .username("newuser")
                .password("secret")
                .build();

        MemberEntity member = new MemberEntity();
        member.setId(4L);
        member.setMemberNo(6161L);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(memberRepository.findByMemberNo(6161L)).thenReturn(Optional.of(member));
        when(userRepository.existsByMemberEntity_Id(4L)).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("HASHED_VALUE");

        authService.register(request);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.getValue());

        UserEntity saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("HASHED_VALUE");
        assertThat(saved.getPassword()).isNotEqualTo("secret");
        assertThat(saved.getRole()).isEqualTo("MEMBER");
        assertThat(saved.getMemberEntity().getId()).isEqualTo(4L);
    }

    @Test
    void login_rejectsWrongPassword(){
        LoginRequest loginRequest = LoginRequest.builder()
                .username("berk")
                .password("wrong-password")
                .build();

        UserEntity user = UserEntity.builder()
                .username("berk")
                .password("HASHED_PASSWORD")
                .build();

        when(userRepository.findByUsername("berk")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "HASHED_PASSWORD")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthorizationFailedException.class)
                .hasMessageContaining("Invalid username or password");

        verify(jwtService, never()).generateToken(any());
    }
}
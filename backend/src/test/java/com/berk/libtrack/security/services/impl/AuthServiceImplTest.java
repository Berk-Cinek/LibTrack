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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @InjectMocks
    private AuthServiceImpl authService;

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
    void register_rejectsMembersWithExistingAccount() {
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
    void register_rejectsUnknownMemberNo() {
        RegisterRequest request = RegisterRequest.builder()
                .memberNo(6161L)
                .username("berk")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("berk")).thenReturn(false);
        when(memberRepository.findByMemberNo(6161L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("member number");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_hashedPasswordAndAssignsMemberRole() {
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
        verify(userRepository).save(captor.capture());

        UserEntity saved = captor.getValue();
        assertThat(saved.getPassword()).isEqualTo("HASHED_VALUE");
        assertThat(saved.getPassword()).isNotEqualTo("secret");
        assertThat(saved.getRole()).isEqualTo("MEMBER");
        assertThat(saved.getMemberEntity().getId()).isEqualTo(4L);
    }

    @Test
    void login_success() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("berk")
                .password("secret")
                .build();

        MemberEntity member = new MemberEntity();
        member.setId(4L);

        UserEntity user = UserEntity.builder()
                .username("berk")
                .password("HASHED_PASSWORD")
                .role("MEMBER")
                .memberEntity(member)
                .build();

        when(userRepository.findByUsername("berk")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "HASHED_PASSWORD")).thenReturn(true);
        when(jwtService.generateToken("berk")).thenReturn("valid.jwt.token");

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("valid.jwt.token");
        assertThat(response.getMemberId()).isEqualTo(4L);
        assertThat(response.getUsername()).isEqualTo("berk");
        assertThat(response.getRole()).isEqualTo("MEMBER");
    }

    @Test
    void login_rejectsUnknownUsername() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("unknown")
                .password("secret")
                .build();

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthorizationFailedException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void login_rejectsWrongPassword() {
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

    @Test
    void promoteToAdmin_success() {
        UserEntity user = UserEntity.builder()
                .role("MEMBER")
                .build();

        when(userRepository.findByMemberEntity_Id(4L)).thenReturn(Optional.of(user));

        authService.promoteToAdmin(4L);

        assertThat(user.getRole()).isEqualTo("ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    void promoteToAdmin_rejectsUnknownMember() {
        when(userRepository.findByMemberEntity_Id(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.promoteToAdmin(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("no user account to promote");

        verify(userRepository, never()).save(any());
    }

    @Test
    void getMemberIdForUsername_success() {
        MemberEntity member = new MemberEntity();
        member.setId(4L);

        UserEntity user = UserEntity.builder()
                .username("berk")
                .memberEntity(member)
                .build();

        when(userRepository.findByUsername("berk")).thenReturn(Optional.of(user));

        Long memberId = authService.getMemberIdForUsername("berk");

        assertThat(memberId).isEqualTo(4L);
    }

    @Test
    void getMemberIdForUsername_rejectsUnknownUser() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getMemberIdForUsername("unknown"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
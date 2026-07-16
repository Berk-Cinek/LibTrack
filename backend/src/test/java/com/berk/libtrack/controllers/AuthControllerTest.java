package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.LoginResponse;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.domain.entities.UserEntity;
import com.berk.libtrack.repositories.UserRepository;
import com.berk.libtrack.security.JwtAuthFilter;
import com.berk.libtrack.security.services.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(// Keep JwtAuthFilter out of the slice.
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_returns201_andCallsService() {
        assertThat(mvc.post().uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.CREATED);

        verify(authService).register(any());
    }

    @Test
    void login_returns200_setsJwtCookie_andBodyHasUsername() {
        LoginResponse serviceResult = LoginResponse.builder()
                .token("jwt-token")
                .username("berk")
                .build();
        when(authService.login(any())).thenReturn(serviceResult);

        MvcTestResult result = mvc.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.username").isEqualTo("berk");

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie)
                .isNotNull()
                .contains("jwt=jwt-token")
                .contains("HttpOnly")
                .contains("Secure")
                .contains("SameSite=Strict")
                .contains("Path=/");
    }

    @Test
    void promoteToAdmin_returns204_andCallsService() {
        assertThat(mvc.patch().uri("/auth/members/{memberId}/promote", 5L))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(authService).promoteToAdmin(eq(5L));
    }

    @Test
    void logout_returns200_andClearsJwtCookie() {
        MvcTestResult result = mvc.post().uri("/auth/logout").exchange();

        assertThat(result).hasStatus(HttpStatus.OK);

        String setCookie = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertThat(setCookie)
                .isNotNull()
                .contains("jwt=")
                .contains("Max-Age=0");
    }

    @Test
    void me_whenNoAuthentication_returns401() {
        assertThat(mvc.get().uri("/auth/me"))
                .hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void me_whenAuthenticated_returns200_withUserDetails() {
        UserDetails principal = User.withUsername("berk")
                .password("ignored")
                .authorities("ROLE_MEMBER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        MemberEntity member = mock(MemberEntity.class);
        when(member.getId()).thenReturn(10L);

        UserEntity user = mock(UserEntity.class);
        when(user.getUsername()).thenReturn("berk");
        when(user.getMemberEntity()).thenReturn(member);

        when(userRepository.findByUsername("berk")).thenReturn(Optional.of(user));

        MvcTestResult result = mvc.get().uri("/auth/me").exchange();

        assertThat(result).hasStatus(HttpStatus.OK);
        assertThat(result).bodyJson().extractingPath("$.username").isEqualTo("berk");
        assertThat(result).bodyJson().extractingPath("$.memberId").isEqualTo(10);
    }
}
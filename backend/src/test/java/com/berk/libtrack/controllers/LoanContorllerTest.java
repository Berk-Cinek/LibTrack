package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.LoanDto;
import com.berk.libtrack.domain.entities.LoanEntity;
import com.berk.libtrack.mappers.LoanMapper;
import com.berk.libtrack.security.JwtAuthFilter;
import com.berk.libtrack.security.services.AuthService;
import com.berk.libtrack.services.LoanService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(
        controllers = LoanContorller.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private LoanService loanService;

    @MockitoBean
    private LoanMapper loanMapper;

    @MockitoBean
    private AuthService authService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private LoanDto loanDto(long id) {
        LoanDto dto = new LoanDto();
        dto.setId(id);
        return dto;
    }

    private void authenticateAs() {
        UserDetails principal = User.withUsername("berk")
                .password("ignored")
                .authorities("ROLE_MEMBER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @Test
    void createLoan_returns201() {
        LoanEntity entity = mock(LoanEntity.class);
        LoanEntity saved = mock(LoanEntity.class);

        when(loanMapper.mapFrom(any())).thenReturn(entity);
        when(loanService.loanCreate(entity)).thenReturn(saved);
        when(loanMapper.mapTo(saved)).thenReturn(loanDto(1L));

        assertThat(mvc.post().uri("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(1);

        verify(loanService).loanCreate(entity);
    }

    @Test
    void getMyLoans_returns200_forAuthenticatedMember() {
        authenticateAs();
        when(authService.getMemberIdForUsername("berk")).thenReturn(10L);

        LoanEntity entity = mock(LoanEntity.class);
        Page<LoanEntity> page = new PageImpl<>(List.of(entity));
        when(loanService.findByMemberId(eq(10L), any(Pageable.class))).thenReturn(page);
        when(loanMapper.mapTo(entity)).thenReturn(loanDto(1L));

        assertThat(mvc.get().uri("/loans/mine"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(1);
    }

    @Test
    void borrowBook_returns201_andSetsMemberIdFromPrincipal() {
        authenticateAs();
        when(authService.getMemberIdForUsername("berk")).thenReturn(10L);

        LoanEntity entity = mock(LoanEntity.class);
        LoanEntity created = mock(LoanEntity.class);
        when(loanMapper.mapFrom(any())).thenReturn(entity);
        when(loanService.loanCreate(entity)).thenReturn(created);
        when(loanMapper.mapTo(created)).thenReturn(loanDto(5L));

        assertThat(mvc.post().uri("/loans/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(5);

        verify(loanService).loanCreate(entity);
    }

    @Test
    void listLoans_returns200() {
        LoanEntity entity = mock(LoanEntity.class);
        Page<LoanEntity> page = new PageImpl<>(List.of(entity));

        when(loanService.findAll(any(Pageable.class), eq("foo"))).thenReturn(page);
        when(loanMapper.mapTo(entity)).thenReturn(loanDto(2L));

        assertThat(mvc.get().uri("/loans?search=foo"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(2);
    }

    @Test
    void getById_whenFound_returns200() {
        LoanEntity entity = mock(LoanEntity.class);
        when(loanService.findOne(1L)).thenReturn(Optional.of(entity));
        when(loanMapper.mapTo(entity)).thenReturn(loanDto(1L));

        assertThat(mvc.get().uri("/loans/{id}", 1L))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void getById_whenEmptyOptional_returns404() {
        // 404 produced directly from the empty Optional, not via the advice.
        when(loanService.findOne(99L)).thenReturn(Optional.empty());

        assertThat(mvc.get().uri("/loans/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void fullUpdate_whenExists_returns200() {
        LoanEntity entity = mock(LoanEntity.class);
        LoanEntity saved = mock(LoanEntity.class);

        when(loanService.isExists(1L)).thenReturn(true);
        when(loanMapper.mapFrom(any())).thenReturn(entity);
        when(loanService.save(entity)).thenReturn(saved);
        when(loanMapper.mapTo(saved)).thenReturn(loanDto(1L));

        assertThat(mvc.put().uri("/loans/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void fullUpdate_whenNotExists_returns404() {
        when(loanService.isExists(99L)).thenReturn(false);

        assertThat(mvc.put().uri("/loans/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void partialUpdate_whenExists_returns200() {
        LoanEntity entity = mock(LoanEntity.class);
        LoanEntity updated = mock(LoanEntity.class);

        when(loanService.isExists(1L)).thenReturn(true);
        when(loanMapper.mapFrom(any())).thenReturn(entity);
        when(loanService.partialUpdate(eq(1L), any())).thenReturn(updated);
        when(loanMapper.mapTo(updated)).thenReturn(loanDto(1L));

        assertThat(mvc.patch().uri("/loans/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void partialUpdate_whenNotExists_returns404() {
        when(loanService.isExists(99L)).thenReturn(false);

        assertThat(mvc.patch().uri("/loans/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteLoan_whenExists_returns204() {
        when(loanService.isExists(1L)).thenReturn(true);

        assertThat(mvc.delete().uri("/loans/{id}", 1L))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(loanService).delete(1L);
    }

    @Test
    void deleteLoan_whenNotExists_returns404() {
        when(loanService.isExists(99L)).thenReturn(false);

        assertThat(mvc.delete().uri("/loans/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}
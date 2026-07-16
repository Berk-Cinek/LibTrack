package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.MemberDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.mappers.MemberMapper;
import com.berk.libtrack.security.JwtAuthFilter;
import com.berk.libtrack.services.MemberService;
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
        controllers = MemberController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private MemberMapper memberMapper;

    private MemberDto memberDto() {
        MemberDto dto = new MemberDto();
        dto.setId(1L);
        return dto;
    }

    @Test
    void createMember_returns201() {
        MemberEntity entity = mock(MemberEntity.class);
        MemberEntity saved = mock(MemberEntity.class);

        when(memberMapper.mapFrom(any())).thenReturn(entity);
        when(memberService.save(entity)).thenReturn(saved);
        when(memberMapper.mapTo(saved)).thenReturn(memberDto());

        assertThat(mvc.post().uri("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(1);

        verify(memberService).save(entity);
    }

    @Test
    void listMembers_returns200() {
        MemberEntity entity = mock(MemberEntity.class);
        Page<MemberEntity> page = new PageImpl<>(List.of(entity));

        when(memberService.findAll(eq("foo"), any(Pageable.class))).thenReturn(page);
        when(memberMapper.mapTo(entity)).thenReturn(memberDto());

        assertThat(mvc.get().uri("/members?search=foo"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(1);
    }

    @Test
    void getById_whenExists_returns200() {
        MemberEntity entity = mock(MemberEntity.class);
        when(memberService.isExists(1L)).thenReturn(true);
        when(memberService.findOne(1L)).thenReturn(Optional.of(entity));
        when(memberMapper.mapTo(entity)).thenReturn(memberDto());

        assertThat(mvc.get().uri("/member/{id}", 1L))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void getById_whenNotExists_returns404() {
        when(memberService.isExists(99L)).thenReturn(false);

        assertThat(mvc.get().uri("/member/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void fullUpdate_whenExists_returns200() {
        MemberEntity entity = mock(MemberEntity.class);
        MemberEntity saved = mock(MemberEntity.class);

        when(memberService.isExists(1L)).thenReturn(true);
        when(memberMapper.mapFrom(any())).thenReturn(entity);
        when(memberService.save(entity)).thenReturn(saved);
        when(memberMapper.mapTo(saved)).thenReturn(memberDto());

        assertThat(mvc.put().uri("/members/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void fullUpdate_whenNotExists_returns404() {
        when(memberService.isExists(99L)).thenReturn(false);

        assertThat(mvc.put().uri("/members/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void partialUpdate_returns200() {
        MemberEntity entity = mock(MemberEntity.class);
        MemberEntity updated = mock(MemberEntity.class);

        when(memberMapper.mapFrom(any())).thenReturn(entity);
        when(memberService.partialUpdate(eq(1L), any())).thenReturn(updated);
        when(memberMapper.mapTo(updated)).thenReturn(memberDto());

        assertThat(mvc.patch().uri("/members/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void deleteMember_whenExists_returns204() {
        when(memberService.isExists(1L)).thenReturn(true);

        assertThat(mvc.delete().uri("/members/{id}", 1L))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(memberService).delete(1L);
    }

    @Test
    void deleteMember_whenNotExists_returns404() {
        when(memberService.isExists(99L)).thenReturn(false);

        assertThat(mvc.delete().uri("/members/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}
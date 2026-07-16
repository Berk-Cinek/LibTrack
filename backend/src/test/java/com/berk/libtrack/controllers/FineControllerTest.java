package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.FineDto;
import com.berk.libtrack.domain.entities.FineEntity;
import com.berk.libtrack.mappers.FineMapper;
import com.berk.libtrack.security.JwtAuthFilter;
import com.berk.libtrack.services.FineService;
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
        controllers = FineController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class FineControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private FineService fineService;

    @MockitoBean
    private FineMapper fineMapper;

    private FineDto fineDto(long id) {
        FineDto dto = new FineDto();
        dto.setId(id);
        return dto;
    }

    @Test
    void createFine_returns201() {
        FineEntity entity = mock(FineEntity.class);
        FineEntity saved = mock(FineEntity.class);

        when(fineMapper.mapFrom(any())).thenReturn(entity);
        when(fineService.save(entity)).thenReturn(saved);
        when(fineMapper.mapTo(saved)).thenReturn(fineDto(1L));

        assertThat(mvc.post().uri("/fines")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.CREATED)
                .bodyJson().extractingPath("$.id").isEqualTo(1);

        verify(fineService).save(entity);
    }

    @Test
    void listFines_returns200() {
        FineEntity entity = mock(FineEntity.class);
        Page<FineEntity> page = new PageImpl<>(List.of(entity));

        when(fineService.findAll(any(Pageable.class))).thenReturn(page);
        when(fineMapper.mapTo(entity)).thenReturn(fineDto(1L));

        assertThat(mvc.get().uri("/fines"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(1);
    }

    @Test
    void listFinesAdmin_returns200() {
        FineEntity entity = mock(FineEntity.class);
        Page<FineEntity> page = new PageImpl<>(List.of(entity));

        when(fineService.findAll(any(Pageable.class), eq("foo"))).thenReturn(page);
        when(fineMapper.mapTo(entity)).thenReturn(fineDto(2L));

        assertThat(mvc.get().uri("/fines/admin?search=foo"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.content[0].id").isEqualTo(2);
    }

    @Test
    void getById_whenFound_returns200() {
        FineEntity entity = mock(FineEntity.class);
        when(fineService.findOne(1L)).thenReturn(Optional.of(entity));
        when(fineMapper.mapTo(entity)).thenReturn(fineDto(1L));

        assertThat(mvc.get().uri("/fines/{id}", 1L))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void getById_whenEmptyOptional_returns404() {
        // Note: this controller's getById returns 404 directly from the empty Optional,
        // NOT via ResourceNotFoundException / the advice.
        when(fineService.findOne(99L)).thenReturn(Optional.empty());

        assertThat(mvc.get().uri("/fines/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void fullUpdate_whenExists_returns200() {
        FineEntity entity = mock(FineEntity.class);
        FineEntity saved = mock(FineEntity.class);

        when(fineService.isExists(1L)).thenReturn(true);
        when(fineMapper.mapFrom(any())).thenReturn(entity);
        when(fineService.save(entity)).thenReturn(saved);
        when(fineMapper.mapTo(saved)).thenReturn(fineDto(1L));

        assertThat(mvc.put().uri("/fines/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void fullUpdate_whenNotExists_returns404() {
        when(fineService.isExists(99L)).thenReturn(false);

        assertThat(mvc.put().uri("/fines/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void partialUpdate_whenExists_returns200() {
        FineEntity entity = mock(FineEntity.class);
        FineEntity updated = mock(FineEntity.class);

        when(fineService.isExists(1L)).thenReturn(true);
        when(fineMapper.mapFrom(any())).thenReturn(entity);
        when(fineService.partialUpdate(eq(1L), any())).thenReturn(updated);
        when(fineMapper.mapTo(updated)).thenReturn(fineDto(1L));

        assertThat(mvc.patch().uri("/fines/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.id").isEqualTo(1);
    }

    @Test
    void partialUpdate_whenNotExists_returns404() {
        when(fineService.isExists(99L)).thenReturn(false);

        assertThat(mvc.patch().uri("/fines/{id}", 99L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteFine_whenExists_returns204() {
        when(fineService.isExists(1L)).thenReturn(true);

        assertThat(mvc.delete().uri("/fines/{id}", 1L))
                .hasStatus(HttpStatus.NO_CONTENT);

        verify(fineService).delete(1L);
    }

    @Test
    void deleteFine_whenNotExists_returns404() {
        when(fineService.isExists(99L)).thenReturn(false);

        assertThat(mvc.delete().uri("/fines/{id}", 99L))
                .hasStatus(HttpStatus.NOT_FOUND);
    }
}
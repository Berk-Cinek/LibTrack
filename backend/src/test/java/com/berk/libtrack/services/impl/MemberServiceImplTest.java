package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.repositories.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private MemberEntity existingMember;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        existingMember = new MemberEntity();
        existingMember.setId(1L);
        existingMember.setMemberNo(1111L);
        existingMember.setFullName("Old Name");
        existingMember.setEmail("old@email.com");
        existingMember.setIsActive(true);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void partialUpdate_Success_UpdatesAllProvidedFields() {
        MemberEntity updateRequest = new MemberEntity();
        updateRequest.setMemberNo(2222L);
        updateRequest.setFullName("New Name");
        updateRequest.setEmail("new@email.com");
        updateRequest.setIsActive(false);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberEntity updatedMember = memberService.partialUpdate(1L, updateRequest);

        assertEquals(2222L, updatedMember.getMemberNo());
        assertEquals("New Name", updatedMember.getFullName());
        assertEquals("new@email.com", updatedMember.getEmail());
        assertFalse(updatedMember.getIsActive());

        verify(memberRepository).save(existingMember);
    }

    @Test
    void partialUpdate_Success_IgnoresNullFields() {
        MemberEntity updateRequest = new MemberEntity();
        updateRequest.setEmail("only_email_changed@email.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(MemberEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MemberEntity updatedMember = memberService.partialUpdate(1L, updateRequest);

        assertEquals("only_email_changed@email.com", updatedMember.getEmail());
        assertEquals(1111L, updatedMember.getMemberNo());
        assertEquals("Old Name", updatedMember.getFullName());
        assertTrue(updatedMember.getIsActive());
    }

    @Test
    void partialUpdate_ThrowsResourceNotFoundException_WhenMemberDoesNotExist() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> memberService.partialUpdate(99L, new MemberEntity()));

        verify(memberRepository, never()).save(any());
    }

    @Test
    void delete_rejectsSystemAdmin() {
        MemberEntity systemAdmin = new MemberEntity();
        systemAdmin.setId(3L);
        systemAdmin.setMemberNo(999999L);

        when(memberRepository.findById(3L)).thenReturn(Optional.of(systemAdmin));

        assertThatThrownBy(() -> memberService.delete(3L))
                .isInstanceOf(DataIntegrityException.class)
                .hasMessageContaining("system admin");

        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_rejectsMemberWithLoans() {
        MemberEntity member = new MemberEntity();
        member.setId(2L);
        member.setMemberNo(3131L);

        when(memberRepository.findById(2L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberEntity_Id(2L)).thenReturn(true);

        assertThatThrownBy(() -> memberService.delete(2L))
                .isInstanceOf(DataIntegrityException.class)
                .hasMessageContaining("loan records");

        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_deletesUnprotectedMemberWithoutLoans() {
        MemberEntity member = new MemberEntity();
        member.setId(4L);
        member.setMemberNo(1234L);

        when(memberRepository.findById(4L)).thenReturn(Optional.of(member));
        when(loanRepository.existsByMemberEntity_Id(4L)).thenReturn(false);

        memberService.delete(4L);

        verify(memberRepository).deleteById(4L);
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenMemberDoesNotExist() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Member not found");

        verify(memberRepository, never()).deleteById(anyLong());
    }

    @Test
    void findAll_WithSearch_WhenSearchIsNull_CallsStandardFindAll() {
        Page<MemberEntity> mockPage = new PageImpl<>(List.of(existingMember));
        when(memberRepository.findAll(pageable)).thenReturn(mockPage);

        Page<MemberEntity> result = memberService.findAll(null, pageable);

        assertNotNull(result);
        verify(memberRepository).findAll(pageable);
        verify(memberRepository, never()).findByFullNameContainingIgnoreCase(any(), any());
    }

    @Test
    void findAll_WithSearch_WhenSearchIsBlank_CallsStandardFindAll() {
        Page<MemberEntity> mockPage = new PageImpl<>(List.of(existingMember));
        when(memberRepository.findAll(pageable)).thenReturn(mockPage);

        Page<MemberEntity> result = memberService.findAll("   ", pageable);

        assertNotNull(result);
        verify(memberRepository).findAll(pageable);
    }

    @Test
    void findAll_WithSearch_WhenSearchIsValid_CallsCustomRepositoryMethod() {
        Page<MemberEntity> mockPage = new PageImpl<>(List.of(existingMember));
        String searchTerm = "Berk";
        when(memberRepository.findByFullNameContainingIgnoreCase(searchTerm, pageable)).thenReturn(mockPage);

        Page<MemberEntity> result = memberService.findAll(searchTerm, pageable);

        assertNotNull(result);
        verify(memberRepository).findByFullNameContainingIgnoreCase(searchTerm, pageable);
        verify(memberRepository, never()).findAll(pageable);
    }

    @Test
    void save_Success() {
        when(memberRepository.save(existingMember)).thenReturn(existingMember);
        MemberEntity result = memberService.save(existingMember);
        assertEquals(existingMember, result);
    }

    @Test
    void isExists_ReturnsTrueFalse() {
        when(memberRepository.existsById(1L)).thenReturn(true);
        assertTrue(memberService.isExists(1L));

        when(memberRepository.existsById(2L)).thenReturn(false);
        assertFalse(memberService.isExists(2L));
    }

    @Test
    void findAll_PageableOnly_Success() {
        Page<MemberEntity> mockPage = new PageImpl<>(List.of(existingMember));
        when(memberRepository.findAll(pageable)).thenReturn(mockPage);

        Page<MemberEntity> result = memberService.findAll(pageable);
        assertNotNull(result);
    }

    @Test
    void findOne_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        Optional<MemberEntity> result = memberService.findOne(1L);
        assertTrue(result.isPresent());
    }
}
package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.repositories.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

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
}
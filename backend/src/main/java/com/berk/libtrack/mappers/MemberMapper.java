package com.berk.libtrack.mappers;

import com.berk.libtrack.domain.dto.MemberDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MemberMapper implements Mapper<MemberEntity, MemberDto>  {

    private LoanMapper loanMapper;

    public MemberMapper(LoanMapper loanMapper) {
        this.loanMapper = loanMapper;
    }

    @Override
    public MemberDto mapTo(MemberEntity memberEntity) {
        return MemberDto.builder()
                .id(memberEntity.getId())
                .memberNo(memberEntity.getMemberNo())
                .fullName(memberEntity.getFullName())
                .email(memberEntity.getEmail())
                .isActive(memberEntity.getIsActive())
                .createdAt(memberEntity.getCreatedAt())
                .loans(memberEntity.getLoanEntities() == null? new ArrayList<>()
                                : memberEntity.getLoanEntities().stream().map(loanMapper::mapTo).toList())
                .build();
    }

    @Override
    public MemberEntity mapFrom(MemberDto memberDto) {
        return MemberEntity.builder()
                .id(memberDto.getId())
                .memberNo(memberDto.getMemberNo())
                .fullName(memberDto.getFullName())
                .email(memberDto.getEmail())
                .isActive(memberDto.getIsActive())
                .createdAt(memberDto.getCreatedAt())
                .loanEntities(memberDto.getLoans() == null ? new ArrayList<>()
                                : memberDto.getLoans().stream().map(loanMapper::mapFrom).toList())
                .build();
    }
}

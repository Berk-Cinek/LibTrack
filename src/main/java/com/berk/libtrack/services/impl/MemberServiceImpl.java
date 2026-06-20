package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.services.MemberService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public MemberEntity save(MemberEntity memberEntity) {
        return memberRepository.save(memberEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return memberRepository.existsById(id);
    }

    @Override
    public MemberEntity partialUpdate(Long id, MemberEntity memberEntity) {
        memberEntity.setId(id);

        return memberRepository.findById(id).map(existingMember ->{
            Optional.ofNullable(existingMember.getMemberNo()).ifPresent(existingMember::setMemberNo);
            Optional.ofNullable(existingMember.getFullName()).ifPresent(existingMember::setFullName);
            Optional.ofNullable(existingMember.getEmail()).ifPresent(existingMember::setEmail);
            Optional.ofNullable(existingMember.getIsActive()).ifPresent(existingMember::setIsActive);
            return memberRepository.save(existingMember);
        }).orElseThrow(() -> new RuntimeException("Non Existing Member"));
    }

    @Override
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}

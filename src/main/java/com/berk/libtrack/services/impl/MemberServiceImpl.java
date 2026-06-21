package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.services.MemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    @CachePut(value = "MEMBER_CACHE", key = "#result.id()" )
    public MemberEntity save(MemberEntity memberEntity) {
        return memberRepository.save(memberEntity);
    }

    @Override
    public Boolean isExists(Long id) {
        return memberRepository.existsById(id);
    }


    @Override
    @CachePut(value = "MEMBER_CACHE", key = "#result.id()" )
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
    @CacheEvict(value = "MEMBER_CACHE", key = "#id")
    public void delete(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public List<MemberEntity> findAll() {
        return StreamSupport.stream(memberRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "MEMBER_CACHE", key = "#id")
    public Optional<MemberEntity> findOne(Long id) {
        return memberRepository.findById(id);
    }

}

package com.berk.libtrack.services.impl;

import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.exceptions.BorrowingNotAllowedException;
import com.berk.libtrack.exceptions.DataIntegrityException;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.repositories.LoanRepository;
import com.berk.libtrack.repositories.MemberRepository;
import com.berk.libtrack.services.MemberService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    private final LoanRepository loanRepository;
    private MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
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
            Optional.ofNullable(memberEntity.getMemberNo()).ifPresent(existingMember::setMemberNo);
            Optional.ofNullable(memberEntity.getFullName()).ifPresent(existingMember::setFullName);
            Optional.ofNullable(memberEntity.getEmail()).ifPresent(existingMember::setEmail);
            Optional.ofNullable(memberEntity.getIsActive()).ifPresent(existingMember::setIsActive);
            return memberRepository.save(existingMember);
        }).orElseThrow(() -> new ResourceNotFoundException("Non Existing Member with id:" + id));
    }

    @Override
    @CacheEvict(value = "MEMBER_CACHE", key = "#id")
    public void delete(Long id) {
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));

        if (member.getMemberNo() == 999999L) {
            throw new DataIntegrityException("The system admin account cannot be deleted.");
        }

        if (loanRepository.existsByMemberEntity_Id(id)) {
            throw new DataIntegrityException("Cannot delete this member, they have loan records.");
        }

        memberRepository.deleteById(id);
    }

    @Override
    public Page<MemberEntity> findAll(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return memberRepository.findAll(pageable);
        }
        return memberRepository.findByFullNameContainingIgnoreCase (search, pageable);
    }

    @Override
    public Page<MemberEntity> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    @Override
    @Cacheable(value = "MEMBER_CACHE", key = "#id")
    public Optional<MemberEntity> findOne(Long id) {
        return memberRepository.findById(id);
    }

}

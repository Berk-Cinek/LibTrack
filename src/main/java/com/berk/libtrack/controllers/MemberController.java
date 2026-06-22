package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.MemberDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.mappers.MemberMapper;
import com.berk.libtrack.services.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class MemberController {

    private MemberService memberService;
    private MemberMapper memberMapper;

    public MemberController(MemberMapper memberMapper, MemberService memberService) {
        this.memberMapper = memberMapper;
        this.memberService = memberService;
    }


    @PostMapping("/members")
    public ResponseEntity<MemberDto> createMember(@RequestBody MemberDto memberDto){
        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity savedEntity = memberService.save(memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/members")
    public Page<MemberDto> listMembers(Pageable pageable){
        Page<MemberEntity> members = memberService.findAll(pageable);
        return members.map(memberMapper::mapTo);
    }

    @GetMapping(path = "member/{id}")
    public ResponseEntity<MemberDto> getById(@PathVariable("id") Long id){
        Optional<MemberEntity> foundMember = memberService.findOne(id);
        return foundMember.map(memberEntity ->{
            MemberDto memberDto = memberMapper.mapTo(memberEntity);
            return new ResponseEntity<>(memberDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/members/{id}")
    public ResponseEntity<MemberDto> fullUpdate(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){
        if (!memberService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        memberDto.setId(id);
        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity savedMemberEntity = memberService.save(memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(savedMemberEntity), HttpStatus.OK);
    }
    @PatchMapping(path = "/members/{id}")
    public ResponseEntity<MemberDto> partialUpdate(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){
        if (!memberService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity updatedMember = memberService.partialUpdate(id, memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(updatedMember), HttpStatus.OK);
    }

    @DeleteMapping(path = "member/{id}")
    public ResponseEntity deleteMember(@PathVariable("id") Long id){
        if (!memberService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

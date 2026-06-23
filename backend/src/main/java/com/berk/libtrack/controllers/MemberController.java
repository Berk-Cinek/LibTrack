package com.berk.libtrack.controllers;

import com.berk.libtrack.domain.dto.MemberDto;
import com.berk.libtrack.domain.entities.MemberEntity;
import com.berk.libtrack.exceptions.ResourceNotFoundException;
import com.berk.libtrack.mappers.MemberMapper;
import com.berk.libtrack.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Members", description = "Basic CRUD functionality for Members + pagination for return-all")
@RestController
public class MemberController {

    private MemberService memberService;
    private MemberMapper memberMapper;

    public MemberController(MemberMapper memberMapper, MemberService memberService) {
        this.memberMapper = memberMapper;
        this.memberService = memberService;
    }

    @Operation(summary = "Create a Member", description = "Adds a new Member to the catalog." +
            " Fields: id, memberNo, fullName, email, isActive, createdAt.")
    @PostMapping("/members")
    public ResponseEntity<MemberDto> createMember(@RequestBody MemberDto memberDto){
        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity savedEntity = memberService.save(memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }
    @Operation(summary = "Get all Members", description = "Get all Members with pagination")
    @GetMapping(path = "/members")
    public Page<MemberDto> listMembers(Pageable pageable){
        Page<MemberEntity> members = memberService.findAll(pageable);
        return members.map(memberMapper::mapTo);
    }
    @Operation(summary = "Get one Member", description = "Get one Member based on id match")
    @GetMapping(path = "member/{id}")
    public ResponseEntity<MemberDto> getById(@PathVariable("id") Long id){
        if (!memberService.isExists(id)) {
            throw new ResourceNotFoundException("member with id:" + id + "not found for getById");
        }

        Optional<MemberEntity> foundMember = memberService.findOne(id);
        return foundMember.map(memberEntity ->{
            MemberDto memberDto = memberMapper.mapTo(memberEntity);
            return new ResponseEntity<>(memberDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Fully update Member", description = "Fully update one Member based on id match")
    @PutMapping(path = "/members/{id}")
    public ResponseEntity<MemberDto> fullUpdate(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){
        if (!memberService.isExists(id)){
            throw new ResourceNotFoundException("member with id:" + id + "not found for full update");
        }

        memberDto.setId(id);
        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity savedMemberEntity = memberService.save(memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(savedMemberEntity), HttpStatus.OK);
    }

    @Operation(summary = "Partial update Member", description = "Partially update one Member based on id match, " +
            "any given value will change those which are not given stay the same")
    @PatchMapping(path = "/members/{id}")
    public ResponseEntity<MemberDto> partialUpdate(@PathVariable("id") Long id, @RequestBody MemberDto memberDto){

        MemberEntity memberEntity = memberMapper.mapFrom(memberDto);
        MemberEntity updatedMember = memberService.partialUpdate(id, memberEntity);
        return new ResponseEntity<>(memberMapper.mapTo(updatedMember), HttpStatus.OK);
    }

    @Operation(summary = "Delete Member", description = "Delete Member based on id match")
    @DeleteMapping(path = "members/{id}")
    public ResponseEntity deleteMember(@PathVariable("id") Long id){
        if (!memberService.isExists(id)) {
            throw new ResourceNotFoundException("member with id:" + id + "not found for deletion");
        }

        memberService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

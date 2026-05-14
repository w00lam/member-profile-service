package com.woolam.memberprofileservice.member.controller;

import com.woolam.memberprofileservice.member.dto.request.MemberCreateRequest;
import com.woolam.memberprofileservice.member.dto.response.MemberResponse;
import com.woolam.memberprofileservice.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<MemberResponse> createMember(@RequestBody MemberCreateRequest request){
        MemberResponse response = memberService.createMember(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMember(@PathVariable UUID id){
        MemberResponse response = memberService.getMember(id);

        return ResponseEntity.ok().body(response);
    }
}

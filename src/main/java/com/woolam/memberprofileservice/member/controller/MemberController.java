package com.woolam.memberprofileservice.member.controller;

import com.woolam.memberprofileservice.common.response.ApiResponse;
import com.woolam.memberprofileservice.member.dto.request.MemberCreateRequest;
import com.woolam.memberprofileservice.member.dto.response.MemberResponse;
import com.woolam.memberprofileservice.member.service.MemberService;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<MemberResponse>> createMember(@Valid @RequestBody MemberCreateRequest request) {
        MemberResponse response = memberService.createMember(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("팀원 저장 성공", response));
    }

    @GetMapping("/{id}")
    public ApiResponse<MemberResponse> getMember(@PathVariable UUID id) {
        MemberResponse response = memberService.getMember(id);

        return ApiResponse.success("팀원 조회 성공", response);
    }
}

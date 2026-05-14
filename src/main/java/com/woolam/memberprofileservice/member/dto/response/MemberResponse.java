package com.woolam.memberprofileservice.member.dto.response;

import com.woolam.memberprofileservice.member.entity.Member;

import java.util.UUID;

public record MemberResponse(UUID id, String name, Integer age, String mbti) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getAge(),
                member.getMbti()
        );
    }
}

package com.woolam.memberprofileservice.member.service;

import com.woolam.memberprofileservice.common.exception.BusinessException;
import com.woolam.memberprofileservice.member.dto.request.MemberCreateRequest;
import com.woolam.memberprofileservice.member.dto.response.MemberResponse;
import com.woolam.memberprofileservice.member.entity.Member;
import com.woolam.memberprofileservice.member.exception.MemberErrorCode;
import com.woolam.memberprofileservice.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse createMember(MemberCreateRequest request) {
        Member member = Member.create(request.name(), request.age(), request.mbti());
        memberRepository.save(member);

        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(UUID id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        return MemberResponse.from(member);
    }
}

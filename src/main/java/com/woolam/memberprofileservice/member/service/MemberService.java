package com.woolam.memberprofileservice.member.service;

import com.woolam.memberprofileservice.common.exception.BusinessException;
import com.woolam.memberprofileservice.common.s3.S3ProfileImageService;
import com.woolam.memberprofileservice.member.dto.request.MemberCreateRequest;
import com.woolam.memberprofileservice.member.dto.response.ProfileImageResponse;
import com.woolam.memberprofileservice.member.dto.response.ProfileImageUploadResponse;
import com.woolam.memberprofileservice.member.dto.response.MemberResponse;
import com.woolam.memberprofileservice.member.entity.Member;
import com.woolam.memberprofileservice.member.exception.MemberErrorCode;
import com.woolam.memberprofileservice.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3ProfileImageService s3ProfileImageService;

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

    @Transactional
    public ProfileImageUploadResponse uploadProfileImage(UUID id, MultipartFile file) {
        validateProfileImage(file);
        Member member = findMember(id);
        String profileImageKey = s3ProfileImageService.upload(id, file);
        member.updateProfileImageKey(profileImageKey);

        return new ProfileImageUploadResponse(profileImageKey);
    }

    @Transactional(readOnly = true)
    public ProfileImageResponse getProfileImageUrl(UUID id) {
        Member member = findMember(id);
        String profileImageKey = member.getProfileImageKey();
        if (profileImageKey == null || profileImageKey.isBlank()) {
            throw new BusinessException(MemberErrorCode.PROFILE_IMAGE_NOT_FOUND);
        }

        return new ProfileImageResponse(s3ProfileImageService.createPresignedUrl(profileImageKey));
    }

    private Member findMember(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(MemberErrorCode.EMPTY_PROFILE_IMAGE);
        }
    }
}

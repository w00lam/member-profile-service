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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3ProfileImageService s3ProfileImageService;

    @Test
    void createMember() {
        MemberCreateRequest request = new MemberCreateRequest("홍길동", 29, "ISFJ");
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        MemberResponse response = memberService.createMember(request);

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        then(memberRepository).should().save(memberCaptor.capture());
        Member savedMember = memberCaptor.getValue();

        assertThat(savedMember.getId()).isNotNull();
        assertThat(savedMember.getName()).isEqualTo("홍길동");
        assertThat(savedMember.getAge()).isEqualTo(29);
        assertThat(savedMember.getMbti()).isEqualTo("ISFJ");
        assertThat(response.id()).isEqualTo(savedMember.getId());
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.age()).isEqualTo(29);
        assertThat(response.mbti()).isEqualTo("ISFJ");
    }

    @Test
    void getMember() {
        Member member = Member.create("홍길동", 29, "ISFJ");
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));

        MemberResponse response = memberService.getMember(member.getId());

        assertThat(response.id()).isEqualTo(member.getId());
        assertThat(response.name()).isEqualTo("홍길동");
        assertThat(response.age()).isEqualTo(29);
        assertThat(response.mbti()).isEqualTo("ISFJ");
    }

    @Test
    void getMemberThrowsExceptionWhenMemberDoesNotExist() {
        UUID unknownId = UUID.randomUUID();
        given(memberRepository.findById(unknownId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMember(unknownId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    void uploadProfileImage() {
        Member member = Member.create("홍길동", 29, "ISFJ");
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                "image".getBytes()
        );
        String profileImageKey = "profile-images/%s/test-profile.png".formatted(member.getId());
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));
        given(s3ProfileImageService.upload(member.getId(), file))
                .willReturn(profileImageKey);

        ProfileImageUploadResponse response = memberService.uploadProfileImage(member.getId(), file);

        assertThat(response.profileImageKey()).isEqualTo(profileImageKey);
        assertThat(member.getProfileImageKey()).isEqualTo(profileImageKey);
    }

    @Test
    void uploadProfileImageThrowsExceptionWhenFileIsEmpty() {
        Member member = Member.create("홍길동", 29, "ISFJ");
        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "profile.png",
                "image/png",
                new byte[0]
        );

        assertThatThrownBy(() -> memberService.uploadProfileImage(member.getId(), emptyFile))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.EMPTY_PROFILE_IMAGE);
    }

    @Test
    void getProfileImageUrl() {
        Member member = Member.create("홍길동", 29, "ISFJ");
        member.updateProfileImageKey("profile-images/%s/profile.png".formatted(member.getId()));
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));
        given(s3ProfileImageService.createPresignedUrl(member.getProfileImageKey()))
                .willReturn("https://example.com/profile.png");

        ProfileImageResponse response = memberService.getProfileImageUrl(member.getId());

        assertThat(response.presignedUrl()).isEqualTo("https://example.com/profile.png");
    }

    @Test
    void getProfileImageUrlThrowsExceptionWhenProfileImageDoesNotExist() {
        Member member = Member.create("홍길동", 29, "ISFJ");
        given(memberRepository.findById(member.getId()))
                .willReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.getProfileImageUrl(member.getId()))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.PROFILE_IMAGE_NOT_FOUND);
    }
}

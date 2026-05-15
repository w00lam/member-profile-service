package com.woolam.memberprofileservice.member.exception;

import com.woolam.memberprofileservice.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 팀원입니다."),
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 프로필 이미지가 없습니다."),
    EMPTY_PROFILE_IMAGE(HttpStatus.BAD_REQUEST, "프로필 이미지 파일은 필수입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

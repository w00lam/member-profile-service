package com.woolam.memberprofileservice.member.exception;

import com.woolam.memberprofileservice.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 팀원입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

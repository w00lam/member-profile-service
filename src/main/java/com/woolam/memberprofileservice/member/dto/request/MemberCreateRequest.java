package com.woolam.memberprofileservice.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MemberCreateRequest(

        @NotBlank(message = "이름 입력은 필수 입니다.")
        String name,

        @Min(value = 1, message = "나이는 0보다 커야합니다.")
        @Max(value = 150, message = "나이는 150보다 작아야합니다.")
        Integer age,

        @NotBlank(message = "MBTI 입력은 필수입니다.")
        String mbti) {
}

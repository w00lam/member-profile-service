package com.woolam.memberprofileservice.member.controller;

import com.woolam.memberprofileservice.member.entity.Member;
import com.woolam.memberprofileservice.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Test
    void createMember() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "홍길동",
                                  "age": 29,
                                  "mbti": "ISFJ"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀원 저장 성공"))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.age").value(29))
                .andExpect(jsonPath("$.data.mbti").value("ISFJ"));
    }

    @Test
    void getMember() throws Exception {
        Member member = memberRepository.save(Member.create("홍길동", 29, "ISFJ"));

        mockMvc.perform(get("/api/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("팀원 조회 성공"))
                .andExpect(jsonPath("$.data.id").value(member.getId().toString()))
                .andExpect(jsonPath("$.data.name").value("홍길동"))
                .andExpect(jsonPath("$.data.age").value(29))
                .andExpect(jsonPath("$.data.mbti").value("ISFJ"));
    }

    @Test
    void getMemberReturnsNotFoundWhenMemberDoesNotExist() throws Exception {
        UUID unknownId = UUID.randomUUID();

        mockMvc.perform(get("/api/members/{id}", unknownId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("FAIL"))
                .andExpect(jsonPath("$.message").value("존재하지 않는 팀원입니다."));
    }

    @Test
    void createMemberReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "age": 0,
                                  "mbti": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAIL"))
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.data", hasItem("이름 입력은 필수 입니다.")))
                .andExpect(jsonPath("$.data", hasItem("나이는 0보다 커야합니다.")))
                .andExpect(jsonPath("$.data", hasItem("MBTI 입력은 필수입니다.")));
    }
}

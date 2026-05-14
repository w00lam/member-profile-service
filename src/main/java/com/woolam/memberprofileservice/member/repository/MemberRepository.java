package com.woolam.memberprofileservice.member.repository;

import com.woolam.memberprofileservice.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
}

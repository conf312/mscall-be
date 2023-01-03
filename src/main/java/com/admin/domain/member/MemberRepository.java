package com.admin.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    public Member findByEmail(String email);
    public int countByEmail(String email);
    public int countByEmailAndPhoneNumber(String email, String phoneNumber);
    @Modifying
    @Query(value = "update member set login_fail_count = NVL(login_fail_count, 0) + 1 where email = :email", nativeQuery = true)
    public Integer updateLoginFailCount(@Param("email") String email);
}


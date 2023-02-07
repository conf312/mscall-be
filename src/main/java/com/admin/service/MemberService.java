package com.admin.service;

import com.admin.domain.member.Member;
import com.admin.domain.member.MemberRepository;
import com.admin.domain.member.QMember;
import com.admin.util.AuthorizationUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QMember member = QMember.member;

    public BooleanExpression eqRole(String role) {
        if (!StringUtils.hasText(role)) return null;
        return member.role.eq(role);
    }

    public BooleanExpression eqIsAccountNonLocked(String isAccountNonLocked) {
        if (!StringUtils.hasText(isAccountNonLocked)) return null;
        return member.isAccountNonLocked.eq(isAccountNonLocked);
    }

    public BooleanExpression eqIsCredentialsNonExpired(String isCredentialsNonExpired) {
        if (!StringUtils.hasText(isCredentialsNonExpired)) return null;
        return member.isCredentialsNonExpired.eq(isCredentialsNonExpired);
    }

    public BooleanExpression eqIsEnabled(String isEnabled) {
        if (!StringUtils.hasText(isEnabled)) return null;
        return member.isEnabled.eq(isEnabled);
    }

    public BooleanExpression containsSearch(String search) {
        if (!StringUtils.hasText(search)) return null;
        return member.name.containsIgnoreCase(search)
            .or(member.email.containsIgnoreCase(search))
            .or(member.phoneNumber.contains(search));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member.Response result = jpaQueryFactory
            .select(Projections.constructor(Member.Response.class,
                member.id,
                member.name,
                member.email,
                member.password,
                member.role,
                member.loginFailCount,
                member.lastLoginTime,
                member.isAccountNonLocked,
                member.isCredentialsNonExpired,
                member.isEnabled,
                member.ip
            ))
            .from(member)
            .where(member.email.eq(email))
            .fetchOne();

        if (result == null) throw new UsernameNotFoundException("==> Not Found Member.");
        return result;
    }

    public Long save(Member.Request request) {
        request.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
        return memberRepository.save(request.toEntity()).getId();
    }

    public int countByEmail(Member.Request request) {
        return memberRepository.countByEmail(request.getEmail());
    }

    @Transactional
    public long updateLastLoginTime(Long id, String ip) {
        return jpaQueryFactory.update(member)
            .set(member.loginFailCount, 0)
            .set(member.ip, ip)
            .set(member.lastLoginTime, LocalDateTime.now())
            .where(member.id.eq(id))
            .execute();
    }

    public HashMap<String, Object> findByAll(Member.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Member.Response> list = jpaQueryFactory
            .from(member)
            .where(
                containsSearch(request.getSearch()),
                eqRole(request.getRole()),
                eqIsEnabled(request.getIsEnabled()),
                eqIsAccountNonLocked(request.getIsAccountNonLocked()),
                eqIsCredentialsNonExpired(request.getIsCredentialsNonExpired())
            )
            .orderBy(member.lastLoginTime.desc())
            .offset(page)
            .limit(pageSize)
            .fetch()
            .stream()
            .map(Member.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(
                containsSearch(request.getSearch()),
                eqRole(request.getRole()),
                eqIsEnabled(request.getIsEnabled()),
                eqIsAccountNonLocked(request.getIsAccountNonLocked()),
                eqIsCredentialsNonExpired(request.getIsCredentialsNonExpired())
            )
            .fetchOne();

        int totalPage = (int) Math.ceil((float) totalCnt / pageSize);
        totalPage = totalPage == 0 ? 1 : totalPage;

        resultMap.put("list", list);
        resultMap.put("request", request);
        resultMap.put("page", page);
        resultMap.put("pageSize", pageSize);
        resultMap.put("totalCnt", totalCnt);
        resultMap.put("totalPage", totalPage);

        return resultMap;
    }

    public HashMap<String, Object> findById(Long id) {
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("info", new Member.Response(memberRepository.findById(id).get()));

        return resultMap;
    }

    @Transactional
    public long updateAccountLock(Member.Request request) {
        return jpaQueryFactory.update(member)
            .set(member.isAccountNonLocked, request.getIsAccountNonLocked())
            .set(member.modifyId, AuthorizationUtil.getMember().getId())
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long updateRole(Member.Request request) {
        return jpaQueryFactory.update(member)
            .set(member.role, request.getRole())
            .set(member.modifyId, AuthorizationUtil.getMember().getId())
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long updatePassword(Member.Request request) {
        return jpaQueryFactory.update(member)
            .set(member.password, new BCryptPasswordEncoder().encode(request.getPassword()))
            .set(member.modifyId, request.getId())
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long updatePassword(String email, String password) {
        return jpaQueryFactory.update(member)
            .set(member.password, new BCryptPasswordEncoder().encode(password))
            .set(member.isCredentialsNonExpired, "Y")
            .set(member.loginFailCount, 0)
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.email.eq(email))
            .execute();
    }

    @Transactional
    public long updateStatusMessage(Member.Request request) {
        return jpaQueryFactory.update(member)
            .set(member.statusMessage, request.getStatusMessage())
            .set(member.modifyId, request.getId())
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public Integer updateLoginFailCount(String email) {
        return memberRepository.updateLoginFailCount(email);
    }

    @Transactional
    public long updateCredentialsNonExpired(Long id, String isCredentialsNonExpired) {
        return jpaQueryFactory.update(member)
            .set(member.isCredentialsNonExpired, isCredentialsNonExpired)
            .set(member.modifyId, id)
            .set(member.modifyTime, LocalDateTime.now())
            .where(member.id.eq(id))
            .execute();
    }

    public int countByEmailAndPhoneNumber(String email, String phoneNumber) {
        return memberRepository.countByEmailAndPhoneNumber(email, phoneNumber);
    }
}

package com.admin.service;

import com.admin.domain.member.QMember;
import com.admin.domain.terms.QTerms;
import com.admin.domain.terms.Terms;
import com.admin.domain.terms.TermsRepository;
import com.admin.util.AuthorizationUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TermsService {
    private final TermsRepository termsRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QTerms terms = QTerms.terms;
    private final QMember member = QMember.member;

    public BooleanExpression betweenRegisterTime(String start, String end) {
        if (!StringUtils.hasText(start) && !StringUtils.hasText(end)) return null;
        TimeZone tzSeoul = TimeZone.getTimeZone("Asia/Seoul");
        LocalDateTime startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(tzSeoul.toZoneId())).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(tzSeoul.toZoneId())).atStartOfDay();
        return terms.registerTime.between(startDate, endDate);
    }

    public BooleanExpression eqUseYn(String useYn) {
        if (!StringUtils.hasText(useYn)) return null;
        return terms.useYn.eq(useYn);
    }

    public HashMap<String, Object> findAll(Terms.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();
        List<Terms.Response> list = jpaQueryFactory
            .from(terms)
            .where (
                betweenRegisterTime(request.getStartDate(), request.getEndDate()),
                eqUseYn(request.getUseYn())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(terms.registerTime.desc())
            .fetch()
            .stream()
            .map(Terms.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(terms.count())
            .from(terms)
            .where (
                betweenRegisterTime(request.getStartDate(), request.getEndDate()),
                eqUseYn(request.getUseYn())
            ).fetchOne();

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

    @Transactional
    public Long save(Terms.Request request) {
        request.setRegisterId(AuthorizationUtil.getMember().getId());
        return termsRepository.save(request.toEntity()).getId();
    }

    public Terms.Response findById(Long id) {
        return jpaQueryFactory.select(Projections.constructor(Terms.Response.class,
            terms.id,
            terms.title,
            terms.contents,
            terms.version,
            terms.useYn,
            ExpressionUtils.as(
                JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where (
                        member.id.eq(terms.registerId)
                    ), "registerName"),
            ExpressionUtils.as(
                JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where (
                        member.id.eq(terms.registerId)
                    ), "modifyName"),
            terms.registerTime,
            terms.modifyTime))
            .from(terms)
            .where(terms.id.eq(id))
            .fetchOne();
    }

    @Transactional
    public Long updateTerms(Terms.Request request) {
        return jpaQueryFactory.update(terms)
            .set(terms.title, request.getTitle())
            .set(terms.contents, request.getContents())
            .set(terms.version, request.getVersion())
            .set(terms.useYn, request.getUseYn())
            .set(terms.modifyId, AuthorizationUtil.getMember().getId())
            .set(terms.modifyTime, LocalDateTime.now())
        .where(terms.id.eq(request.getId()))
        .execute();
    }

    @Transactional
    public void deleteById(Terms.Request request) {
        termsRepository.deleteById(request.getId());
    }

    public List<Terms.ContentsDTO> findTop1() {
        return jpaQueryFactory.select(Projections.constructor(Terms.ContentsDTO.class,
                        terms.title,
                        terms.contents,
                        terms.version))
                .from(terms)
                .where(terms.useYn.eq("Y"))
                .orderBy(terms.version.desc())
                .limit(1)
                .fetch();
    }

    public String findByVersion(Terms.Request request) {
        return jpaQueryFactory.select(Projections.constructor(String.class,
            terms.contents))
            .from(terms)
            .where(terms.version.eq(request.getVersion()))
            .fetchOne();
    }

    public List<String> findVersionList() {
        return jpaQueryFactory.select(Projections.constructor(String.class,
            terms.version))
            .from(terms)
            .orderBy(terms.version.desc())
            .fetch();
    }


}

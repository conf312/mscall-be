package com.admin.service;

import com.admin.domain.inquiry.Inquiry;
import com.admin.domain.inquiry.InquiryRepository;
import com.admin.domain.inquiry.QInquiry;
import com.admin.domain.member.QMember;
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
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QInquiry inquiry = QInquiry.inquiry;
    private final QMember member = QMember.member;

    public BooleanExpression betweenRegisterTime(String start, String end) {
        if (!StringUtils.hasText(start) && !StringUtils.hasText(end)) return null;
        TimeZone tzSeoul = TimeZone.getTimeZone("Asia/Seoul");
        LocalDateTime startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(tzSeoul.toZoneId())).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(tzSeoul.toZoneId())).atStartOfDay();
        return inquiry.registerTime.between(startDate, endDate);
    }

    public BooleanExpression eqAnswerStatus(String answerStatus) {
        if (!StringUtils.hasText(answerStatus)) return null;
        return inquiry.answerStatus.eq(answerStatus);
    }

    public BooleanExpression containsSearch(String searchType, String search) {
        if (!StringUtils.hasText(search)) return null;
        switch (searchType) {
            case "company":
                return inquiry.company.containsIgnoreCase(search);
            case "industry":
                return inquiry.industry.containsIgnoreCase(search);
            case "name":
                return inquiry.name.containsIgnoreCase(search);
            case "email":
                return inquiry.email.containsIgnoreCase(search);
            case "contents":
                return inquiry.contents.containsIgnoreCase(search);
            default:
                return null;
        }
    }

    public HashMap<String, Object> findAll(Inquiry.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Inquiry.Response> list = jpaQueryFactory
            .from(inquiry)
            .where (
                betweenRegisterTime(request.getStartDate(), request.getEndDate()),
                eqAnswerStatus(request.getAnswerStatus()),
                containsSearch(request.getSearchType(), request.getSearch())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(inquiry.registerTime.desc())
            .fetch()
            .stream()
            .map(Inquiry.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(inquiry.count())
            .from(inquiry)
            .where (
                betweenRegisterTime(request.getStartDate(), request.getEndDate()),
                eqAnswerStatus(request.getAnswerStatus()),
                containsSearch(request.getSearchType(), request.getSearch())
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

    public Inquiry.Response findById(Long id) {
        return jpaQueryFactory
            .select(Projections.constructor(Inquiry.Response.class,
                inquiry.id,
                inquiry.company,
                inquiry.industry,
                inquiry.name,
                inquiry.tel,
                inquiry.email,
                inquiry.workType,
                inquiry.workLoad,
                inquiry.contents,
                inquiry.registerTime,
                inquiry.note,
                inquiry.answerStatus,
                inquiry.answerTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where (
                        member.id.eq(inquiry.answerId)
                    ), "answerName")
            ))
            .from(inquiry)
            .where(inquiry.id.eq(id))
            .fetchOne();
    }

    @Transactional
    public long updateAnswer(Inquiry.Request request) {
        return jpaQueryFactory
            .update(inquiry)
            .set(inquiry.note, request.getNote())
            .set(inquiry.answerStatus, request.getAnswerStatus())
            .set(inquiry.answerId, AuthorizationUtil.getMember().getId())
            .set(inquiry.answerTime, LocalDateTime.now())
            .where(inquiry.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public Long save(Inquiry.Request request) {
        return inquiryRepository.save(request.toEntity()).getId();
    }

    public Long countByGoe30() {
        return jpaQueryFactory
            .select(inquiry.count())
            .from(inquiry)
            .where(inquiry.registerTime.goe(LocalDateTime.now().minusDays(30)))
            .fetchOne();
    }

}

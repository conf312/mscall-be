package com.admin.domain.inquiry;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@DynamicInsert
@NoArgsConstructor
@Getter
@Entity(name = "inquiry")
public class Inquiry extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String company;
    private String industry;
    private String name;
    private String tel;
    private String email;
    private String workType;
    private String workLoad;
    private String contents;
    private String note;
    private String answerStatus;
    private Long answerId;
    private LocalDateTime answerTime;

    @Builder
    public Inquiry(Long id, String company, String industry, String name, String tel, String email, String workType, String workLoad, String contents, String note, String answerStatus, Long answerId, LocalDateTime answerTime) {
        this.id = id;
        this.company = company;
        this.industry = industry;
        this.name = name;
        this.tel = tel;
        this.email = email;
        this.workType = workType;
        this.workLoad = workLoad;
        this.note = note;
        this.contents = contents;
        this.answerStatus = answerStatus;
        this.answerId = answerId;
        this.answerTime = answerTime;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private String company;
        private String industry;
        private String name;
        private String tel;
        private String email;
        private String workType;
        private String workLoad;
        private String contents;
        private String note;
        private String answerStatus;
        private Long answerId;
        private LocalDateTime answerTime;
        private String searchType;
        private String search;
        private String startDate;
        private String endDate;
        private String reCaptchaToken;

        public Inquiry toEntity() {
            return Inquiry.builder()
                .id(id)
                .company(company)
                .industry(industry)
                .name(name)
                .tel(tel)
                .email(email)
                .workType(workType)
                .workLoad(workLoad)
                .contents(contents)
                .note(note)
                .answerStatus(answerStatus)
                .answerId(answerId)
                .answerTime(answerTime)
                .build();
        }

    }

    @Getter
    public static class Response {
        private Long id;
        private String company;
        private String industry;
        private String name;
        private String tel;
        private String email;
        private String workType;
        private String workLoad;
        private String contents;
        private String registerTime;
        private String modifyTime;
        private String note;
        private String answerStatus;
        private Long answerId;
        private String answerTime;
        private String answerName;

        public Response(Object o) {
            Inquiry inquiry = (Inquiry)o;
            id = inquiry.getId();
            company = inquiry.getCompany();
            industry = inquiry.getIndustry();
            name = inquiry.getName();
            tel = inquiry.getTel();
            email = inquiry.getEmail();
            workType = inquiry.getWorkType();
            workLoad = inquiry.getWorkLoad();
            contents = inquiry.getContents();
            registerTime = toStringDateTime(inquiry.getRegisterTime());
            modifyTime = toStringDateTime(inquiry.getModifyTime());
            note = inquiry.getNote();
            answerStatus = inquiry.getAnswerStatus();
            answerId = inquiry.getAnswerId();
            answerTime = toStringDateTime(inquiry.getAnswerTime());
        }

        public Response(Long id, String company, String industry, String name, String tel, String email, String workType, String workLoad, String contents, LocalDateTime registerTime, String note, String answerStatus, LocalDateTime answerTime, String answerName) {
            this.id = id;
            this.company = company;
            this.industry = industry;
            this.name = name;
            this.tel = tel;
            this.email = email;
            this.workType = workType;
            this.workLoad = workLoad;
            this.contents = contents;
            this.registerTime = toStringDateTime(registerTime);
            this.note = note;
            this.answerStatus = answerStatus;
            this.answerTime = toStringDateTime(answerTime);
            this.answerName = answerName;
        }
    }
}

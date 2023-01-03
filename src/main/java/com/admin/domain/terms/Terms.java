package com.admin.domain.terms;

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
@Entity(name = "terms")
public class Terms extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String contents;
    private String version;
    private String useYn;
    private Long registerId;
    private Long modifyId;

    @Builder
    public Terms(Long id, String title, String contents, String version, String useYn, Long registerId, Long modifyId) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.version = version;
        this.useYn = useYn;
        this.registerId = registerId;
        this.modifyId = modifyId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private String title;
        private String contents;
        private String version;
        private String useYn;
        private Long registerId;
        private Long modifyId;
        private String startDate;
        private String endDate;
        private Long[] idArr;

        public Terms toEntity() {
            return Terms.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .version(version)
                .useYn(useYn)
                .registerId(registerId)
                .modifyId(modifyId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String contents;
        private String version;
        private String useYn;
        private Long registerId;
        private Long modifyId;
        private String registerName;
        private String modifyName;
        private String registerTime;
        private String modifyTime;

        public Response(Object o) {
            Terms terms = (Terms)o;
            id = terms.getId();
            title = terms.getTitle();
            version = terms.getVersion();
            useYn = terms.getUseYn();
            registerTime = toStringDateTime(terms.getRegisterTime());
            modifyTime = toStringDateTime(terms.getModifyTime());
        }

        public Response(Long id, String title, String contents, String version, String useYn, String registerName, String modifyName, LocalDateTime registerTime, LocalDateTime modifyTime) {
            this.id = id;
            this.title = title;
            this.contents = contents;
            this.version = version;
            this.useYn = useYn;
            this.registerName = registerName;
            this.modifyName = modifyName;
            this.registerTime = toStringDateTime(registerTime);
            this.modifyTime = toStringDateTime(modifyTime);
        }
    }

    @Getter
    public static class ContentsDTO {
        private String title;
        private String contents;
        private String version;

        public ContentsDTO(String title, String contents, String version) {
            this.title = title;
            this.contents = contents;
            this.version = version;
        }

        public ContentsDTO(String title, String version) {
            this.title = title;
            this.version = version;
        }
    }

}

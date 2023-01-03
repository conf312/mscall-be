package com.admin.domain.member;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Collection;

@DynamicInsert
@NoArgsConstructor
@Getter
@Entity(name = "member")
public class Member extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String role;
    private String statusMessage;
    private int loginFailCount;
    private LocalDateTime lastLoginTime;
    private String isAccountNonLocked;
    private String isCredentialsNonExpired;
    private String isEnabled;
    private String ip;
    private Long modifyId;

    @Builder
    public Member(Long id, String name, String email, String password, String phoneNumber, String role, String statusMessage, int loginFailCount, LocalDateTime lastLoginTime, String isAccountNonLocked, String isCredentialsNonExpired, String isEnabled, String ip, Long modifyId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.statusMessage = statusMessage;
        this.loginFailCount = loginFailCount;
        this.lastLoginTime = lastLoginTime;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
        this.ip = ip;
        this.modifyId = modifyId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
        private String role;
        private String statusMessage;
        private int loginFailCount;
        private LocalDateTime lastLoginTime;
        private String isAccountNonLocked;
        private String isCredentialsNonExpired;
        private String isEnabled;
        private String ip;
        private Long modifyId;
        private String search;

        public Member toEntity() {
            return Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .phoneNumber(phoneNumber)
                .role(role)
                .statusMessage(statusMessage)
                .loginFailCount(loginFailCount)
                .lastLoginTime(lastLoginTime)
                .isAccountNonLocked(isAccountNonLocked)
                .isCredentialsNonExpired(isCredentialsNonExpired)
                .isEnabled(isEnabled)
                .ip(ip)
                .modifyId(modifyId)
                .build();
        }
    }

    @Getter
    public static class Response implements UserDetails {
        private final Long id;
        private final String name;
        private final String email;
        private String password;
        private String phoneNumber;
        private final String role;
        private String statusMessage;
        private final int loginFailCount;
        private final String lastLoginTime;
        private final String isAccountNonLocked;
        private final String isCredentialsNonExpired;
        private final String isEnabled;
        private final String ip;
        private Long modifyId;
        private String registerTime;
        private String modifyTime;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public String getUsername() {
            return email;
        }

        //계정이 만료되지 않았는지 리턴 (true: 만료 안됨)
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        //계정이 잠겨있는지 않았는지 리턴. (true: 잠기지 않음)
        @Override
        public boolean isAccountNonLocked() {
            return "Y".equals(isAccountNonLocked);
        }

        //비밀번호가 만료되지 않았는지 리턴한다. (true: 만료 안됨)
        @Override
        public boolean isCredentialsNonExpired() {
            return "Y".equals(isCredentialsNonExpired);
        }

        //계정이 활성화(사용가능)인지 리턴 (true: 활성화)
        @Override
        public boolean isEnabled() {
            return "Y".equals(isEnabled);
        }

        public Response(Object o) {
            Member member = (Member)o;
            id = member.getId();
            name = member.getName();
            email = member.getEmail();
            phoneNumber = member.getPhoneNumber();
            role = member.getRole();
            statusMessage = member.getStatusMessage();
            loginFailCount = member.getLoginFailCount();
            lastLoginTime = toStringDateTime(member.getLastLoginTime());
            isAccountNonLocked = member.isAccountNonLocked;
            isCredentialsNonExpired = member.isCredentialsNonExpired;
            isEnabled = member.isEnabled;
            ip = member.getIp();
            modifyId = member.getModifyId();
            registerTime = toStringDateTime(member.getRegisterTime());
            modifyTime = toStringDateTime(member.getModifyTime());
        }

        public Response(Long id, String name, String email, String password, String role, int loginFailCount, LocalDateTime lastLoginTime, String isAccountNonLocked, String isCredentialsNonExpired, String isEnabled, String ip) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.password = password;
            this.role = role;
            this.loginFailCount = loginFailCount;
            this.lastLoginTime = toStringDateTime(lastLoginTime);
            this.isAccountNonLocked = isAccountNonLocked;
            this.isCredentialsNonExpired = isCredentialsNonExpired;
            this.isEnabled = isEnabled;
            this.ip = ip;
        }
    }
}

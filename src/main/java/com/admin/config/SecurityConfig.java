package com.admin.config;

import com.admin.handler.AuthFailureHandler;
import com.admin.handler.AuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private static final String[] ALLOW_PATH = {
        "/", "/favicon.ico", "/member/public/**", "/js/**", "/css/**", "/images/**","/fonts/**", "/scss/**", "/uploadFiles/**", "/api/**"
    };

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public BCryptPasswordEncoder encryptPassword() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /*
         csrf 토큰 활성화시 사용
         쿠키를 생성할 때 HttpOnly 태그를 사용하면 클라이언트 스크립트가 보호된 쿠키에 액세스하는 위험을 줄일 수 있으므로 쿠키의 보안을 강화할 수 있다.
         csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and() */
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers(ALLOW_PATH).permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
            .loginPage("/member/public/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .loginProcessingUrl("/member/public/login/action")
            .successHandler(authSuccessHandler)
            .failureHandler(authFailureHandler)
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
            .logoutSuccessUrl("/member/public/login")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID", "remember-me")
            .permitAll()
            .and()
            .sessionManagement()
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false) // true면 중복 로그인을 막고, false면 이전 로그인의 세션을 해제
            .expiredUrl("/member/public/login?msg=msg.member.sessionExpired")
            .and()
            .and().rememberMe()
            .alwaysRemember(false)
            .tokenValiditySeconds(43200) // in seconds, 12시간 유지
            .rememberMeParameter("remember-me");
        return http.build();
    }
}

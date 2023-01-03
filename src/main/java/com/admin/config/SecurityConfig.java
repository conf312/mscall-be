package com.admin.config;

import com.admin.handler.AuthFailureHandler;
import com.admin.handler.AuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
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
        */
        http.csrf().disable() //.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
            .authorizeRequests() // 요청 URL에 따라 접근 권한을 설정
            .antMatchers(ALLOW_PATH).permitAll() // 해당 경로들은 접근을 허용
            .anyRequest() // 다른 모든 요청은
            .authenticated() // 인증된 유저만 접근을 허용
            .and()
            .formLogin() // 로그인 폼은
            .loginPage("/member/public/login") // 해당 주소로 로그인 페이지를 호출한다.
            .usernameParameter("email")
            .passwordParameter("password")
            .loginProcessingUrl("/member/public/login/action") // 해당 URL로 요청이 오면 스프링 시큐리티가 가로채서 로그인처리를 한다. -> loadUserByName
            .successHandler(authSuccessHandler) // 성공시 요청을 처리할 핸들러
            .failureHandler(authFailureHandler) // 실패시 요청을 처리할 핸들러
            .and()
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 로그아웃 URL
            .logoutSuccessUrl("/member/public/login") // 성공시 리턴 URL
            .invalidateHttpSession(true) // 인증정보를 지우하고 세션을 무효화
            .deleteCookies("JSESSIONID", "remember-me") // JSESSIONID, remember-me 쿠키 삭제
            .permitAll()
            .and()
            .sessionManagement()
            .maximumSessions(1) // 세션 최대 허용 수 1, -1인 경우 무제한 세션 허용
            .maxSessionsPreventsLogin(false) // true면 중복 로그인을 막고, false면 이전 로그인의 세션을 해제
            .expiredUrl("/member/public/login?msg=msg.member.sessionExpired") // 세션이 만료된 경우 이동 할 페이지를 지정
            .and()
            .and().rememberMe() // 로그인 유지
            .alwaysRemember(false) // 항상 기억할 것인지 여부
            .tokenValiditySeconds(43200) // in seconds, 12시간 유지
            .rememberMeParameter("remember-me");
        return http.build();
    }
}

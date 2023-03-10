package com.admin.web;

import com.admin.domain.inquiry.Inquiry;
import com.admin.domain.terms.Terms;
import com.admin.message.MessageType;
import com.admin.message.RestMessage;
import com.admin.service.InquiryService;
import com.admin.service.TermsService;
import com.admin.util.ReCAPTCHAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {

    @Value("${global.google.recaptcha.url}")
    private String url;
    @Value("${global.google.recaptcha.secretKey}")
    private String secretKey;
    private final InquiryService inquiryService;
    private final TermsService termsService;

    @Value("${global.xClientKey}")
    private String xClientKey;

    public boolean certify(HttpServletRequest httpRequest) {
        if (httpRequest.getHeader("X-CLIENT-KEY") != null)
            return httpRequest.getHeader("X-CLIENT-KEY").equals(xClientKey);
        return false;
    }

    @PostMapping("/inquiry/save")
    public ResponseEntity<RestMessage> inquirySave(HttpServletRequest httpRequest, Inquiry.Request request) throws Exception {
        String msg = MessageType.ERROR.label();
        Long result = 0L;

        if (!certify(httpRequest)) {
            msg = MessageType.NOT_MATCH.label();
        } else {
            if (ReCAPTCHAUtil.getSiteVerify(url, secretKey, request.getReCaptchaToken())) {
                result = inquiryService.save(request);
                if (result > 0) msg = MessageType.SUCCESS.label();
            } else {
                msg = MessageType.FAIL_TOKEN.label();
            }
        }

        return ResponseEntity.ok()
            .headers(new HttpHeaders())
            .body(new RestMessage(HttpStatus.OK, result, msg));
    }

    @GetMapping("/terms/top1")
    public ResponseEntity<RestMessage> findTop1(HttpServletRequest httpRequest) throws Exception {
        String msg = MessageType.ERROR.label();
        List<Terms.ContentsDTO> result = null;

        if (!certify(httpRequest))
            msg = MessageType.NOT_MATCH.label();
        else {
            result = termsService.findTop1();
            msg = MessageType.SUCCESS.label();
        }

        return ResponseEntity.ok()
            .headers(new HttpHeaders())
            .body(new RestMessage(HttpStatus.OK, result, msg));
    }

    @GetMapping("/terms/findByVersion")
    public ResponseEntity<RestMessage> findByVersion(HttpServletRequest httpRequest, Terms.Request request) throws Exception {
        String msg = MessageType.ERROR.label();
        String result = null;

        if (!certify(httpRequest))
            msg = MessageType.NOT_MATCH.label();
        else {
            result = termsService.findByVersion(request);
            msg = MessageType.SUCCESS.label();
        }

        return ResponseEntity.ok()
                .headers(new HttpHeaders())
                .body(new RestMessage(HttpStatus.OK, result, msg));
    }

    @GetMapping("/terms/findVersionList")
    public ResponseEntity<RestMessage> findVersionList(HttpServletRequest httpRequest, Inquiry.Request request) throws Exception {
        String msg = MessageType.ERROR.label();
        List<String> result = null;

        if (!certify(httpRequest))
            msg = MessageType.NOT_MATCH.label();
        else {
            result = termsService.findVersionList();
            msg = MessageType.SUCCESS.label();
        }

        return ResponseEntity.ok()
            .headers(new HttpHeaders())
            .body(new RestMessage(HttpStatus.OK, result, msg));
    }
}

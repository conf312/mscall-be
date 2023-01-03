package com.admin.web;

import com.admin.domain.member.Member;
import com.admin.service.MemberService;
import com.admin.util.AuthorizationUtil;
import com.admin.util.FileReadUtil;
import com.admin.util.MailUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SecureRandom;

@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {
    @Value("${global.url.admin}")
    private String httpUrlAdmin;
    @Value("${global.mail.templates.path}")
    private String mailTemplatesPath;
    private final MemberService memberService;
    private final MailUtil mailUtil;

    @GetMapping("/public/login")
    public String getLoginPage() {
        return "member/login";
    }

    @GetMapping("/public/join")
    public String getJoinPage() {
        return "member/join";
    }

    @GetMapping("/profile")
    public String getProfilePage(Model model) {
        model.addAttribute("resultMap", memberService.findById(AuthorizationUtil.getMember().getId()));
        return "member/profile";
    }

    @PostMapping("/public/join/action")
    public String save(Model model, Member.Request request) {
        if (memberService.save(request) > 0) {
            model.addAttribute("url", "/member/login");
            model.addAttribute("msg", "msg.register");
        }
        return "error/blank";
    }

    @PostMapping("/public/count-by/email")
    public String countByEmail(Model model, Member.Request request) {
        model.addAttribute("count", memberService.countByEmail(request));
        return "jsonView";
    }

    @GetMapping("/public/forgot-password")
    public String getForgotPasswordPage() {
        return "member/forgot-password";
    }

    @GetMapping("/list")
    public String getMemberListPage(Model model, Member.Request request,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", memberService.findByAll(request, page, pageSize));
        return "member/list";
    }

    @GetMapping("/detail")
    public String getMemberDetailPage(Model model, Member.Request request) {
        if (request.getId() != null)
            model.addAttribute("resultMap", memberService.findById(request.getId()));
        return "member/detail";
    }

    @PostMapping("/update/account-lock")
    public String updateAccountLock(Model model, Member.Request request) {
        model.addAttribute("result", memberService.updateAccountLock(request));
        return "jsonView";
    }

    @PostMapping("/update/role")
    public String updateRole(Model model, Member.Request request) {
        model.addAttribute("result", memberService.updateRole(request));
        return "jsonView";
    }

    @PostMapping("/update/password")
    public String updatePassword(Model model, Member.Request request) {
        model.addAttribute("result", memberService.updatePassword(request));
        return "jsonView";
    }

    @PostMapping("/update/status-message")
    public String updateStatusMessage(Model model, Member.Request request) {
        model.addAttribute("result", memberService.updateStatusMessage(request));
        return "jsonView";
    }

    @GetMapping("/public/forgot-password/complete")
    public String getForgotPasswordComplete(Model model) {
        if (RequestContextHolder.getRequestAttributes().getAttribute("forgotPassword", RequestAttributes.SCOPE_SESSION) != null) {
            RequestContextHolder.getRequestAttributes().removeAttribute("forgotPassword", RequestAttributes.SCOPE_SESSION);
            return "member/forgot-password-complete";
        } else {
            model.addAttribute("url", "/member/login");
            model.addAttribute("msg", "msg.page.expired");
            return "error/blank";
        }
    }

    @PostMapping("/public/forgot-password/action")
    public String countByEmailAndPhoneNumber(Model model, Member.Request request) throws IOException, URISyntaxException {
        model.addAttribute("result", false);

        if (memberService.countByEmailAndPhoneNumber(request.getEmail(), request.getPhoneNumber()) > 0) {
            String text = new FileReadUtil(mailTemplatesPath).getHtmlToString("mail-password.html");
            String tempPassword = RandomStringUtils.random(8,0,0,true,true,null, new SecureRandom());

            if (memberService.updatePassword(request.getEmail(), tempPassword) > 0) {
                text = text.replace("$httpUrlAdmin", httpUrlAdmin).replace("$number", tempPassword);
                mailUtil.sendMailHtml(request.getEmail(), "[Admin] 임시 비밀번호 발급", text);
            }

            RequestContextHolder.getRequestAttributes().setAttribute("forgotPassword", true, RequestAttributes.SCOPE_SESSION);
            model.addAttribute("result", true);
        }

        return "jsonView";
    }
}

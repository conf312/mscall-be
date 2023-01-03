package com.admin.web;

import com.admin.domain.inquiry.Inquiry;
import com.admin.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/inquiry")
@Controller
public class InquiryController {
    private final InquiryService inquiryService;

    @GetMapping("/list")
    public String getListPage(Model model, Inquiry.Request request,
                        @RequestParam(required = false, defaultValue = "0") Integer page,
                        @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", inquiryService.findAll(request, page, pageSize));
        return "inquiry/list";
    }

    @GetMapping("/detail")
    public String getWritePage(Model model, Inquiry.Request request) {
        model.addAttribute("info", inquiryService.findById(request.getId()));
        return "inquiry/detail";
    }

    @PostMapping("/update-answer")
    public String updateAnswer(Model model, Inquiry.Request request) {
        if (inquiryService.updateAnswer(request) > 0 ) {
            model.addAttribute("msg", "msg.register");
            model.addAttribute("url", "/inquiry/detail?id=" + request.getId());
        }
        return "error/blank";
    }
}

package com.admin.web;

import com.admin.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final InquiryService inquiryService;

    @GetMapping("/dashboard")
    public String getDashBoardPage(Model model){
        model.addAttribute("countByGoe30", inquiryService.countByGoe30());
        return "main/dashboard";
    }
}

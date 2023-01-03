package com.admin.web;

import com.admin.domain.terms.Terms;
import com.admin.service.TermsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/terms")
@Controller
public class TermsController {
    private final TermsService termsService;

    @GetMapping("/list")
    public String getListPage(Model model, Terms.Request request,
                              @RequestParam(required = false, defaultValue = "0") Integer page,
                              @RequestParam(required = false, defaultValue = "8") Integer pageSize) {
        model.addAttribute("resultMap", termsService.findAll(request, page, pageSize));
        return "terms/list";
    }

    @GetMapping("/detail")
    public String getDetailPage(Model model, Terms.Request request) {
        Terms.Response response = null;
        if (request.getId() != null) response = termsService.findById(request.getId());
        model.addAttribute("info", response);
        return "terms/detail";
    }

    @PostMapping("/save")
    public String save(Model model, Terms.Request request) {
        if (termsService.save(request) > 0 ) {
            model.addAttribute("msg", "msg.register");
            model.addAttribute("url", "/terms/list");
        }
        return "error/blank";
    }

    @PostMapping("/update")
    public String updateTerms(Model model, Terms.Request request) {
        if (termsService.updateTerms(request) > 0 ) {
            model.addAttribute("msg", "msg.modify");
            model.addAttribute("url", "/terms/detail?id=" + request.getId());
        }
        return "error/blank";
    }

    @PostMapping("/deleteById")
    public String deleteById(Model model, Terms.Request request) {
        termsService.deleteById(request);
        model.addAttribute("msg", "msg.delete");
        model.addAttribute("url", "/terms/list");
        return "error/blank";
    }
}

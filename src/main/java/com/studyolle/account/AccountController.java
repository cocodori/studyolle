package com.studyolle.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute("signupForm", new SignupForm());
        return "/account/sign-up";
    }
}

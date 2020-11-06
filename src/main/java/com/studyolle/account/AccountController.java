package com.studyolle.account;

import com.studyolle.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;

    @InitBinder("signupForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signupForm(Model model) {
        model.addAttribute("signupForm",new SignupForm());
        return "/account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid SignupForm signupForm, Errors errors) {
        if (errors.hasErrors()) {
            return "account/sign-up";
        }
        //회원 등록 서비스 호출
        accountService.processNewAccount(signupForm);
        return "redirect:/";
    }


}

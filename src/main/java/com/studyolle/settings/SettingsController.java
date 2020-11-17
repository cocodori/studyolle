package com.studyolle.settings;

import com.studyolle.account.AccountService;
import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Tag;
import com.studyolle.settings.form.*;
import com.studyolle.settings.validator.NicknameValidator;
import com.studyolle.settings.validator.PasswordFormValidator;
import com.studyolle.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SettingsController {
    static final String SETTINGS_PROFILE_VIEW_NAME = "settings/profile";
    static final String SETTINGS_PROFILE_VIEW_URL = "/" + SETTINGS_PROFILE_VIEW_NAME;
    static final String SETTINGS_PASSWORD_VIEW_NAME = "settings/password";
    static final String SETTINGS_PASSWORD_VIEW_URL = "/"+SETTINGS_PASSWORD_VIEW_NAME;
    static final String SETTINGS_NOTIFICATIONS_VIEW_NAME = "settings/notifications";
    static final String SETTINGS_NOTIFICATIONS_VIEW_URL = "/"+SETTINGS_NOTIFICATIONS_VIEW_NAME;
    static final String SETTINGS_ACCOUNT_VIEW_NAME = "settings/account";
    static final String SETTINGS_ACCOUNT_URL = "/" + SETTINGS_ACCOUNT_VIEW_NAME;
    static final String SETTINGS_TAGS_VIEW_NAME = "settings/tags";
    static final String SETTINGS_TAGS_URL = "/" + SETTINGS_TAGS_VIEW_NAME;

    private final ModelMapper modelMapper;
    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final TagRepository tagRepository;

    @InitBinder("passwordForm")
    public void passwordFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    /*
    *   닉네임 폼을 추가할 때 이 발리데이터를 추가해달라.
    * */
    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }

    @GetMapping(SETTINGS_PROFILE_VIEW_URL)
    public String updateProfileForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        /*
        *   Account에 들어있는 데이터로 Profile을 채운다.
        * */
        model.addAttribute(modelMapper.map(account, Profile.class));
        return SETTINGS_PROFILE_VIEW_NAME;
    }

    /*
    *   form에서 입력한 값을 @ModelAttribute를 사용해서 Profile로 받는다. (생략 가능)
    *   @ModelAttribute로 데이터를 바인딩하고 Validation할 때 발생하는 에러를 받아주는
    *   Errors는 @ModelAttribute를 사용해서 받는 객체 옆에다 둬야한다.
    *
    * */
    @PostMapping(SETTINGS_PROFILE_VIEW_URL)
    public String updateProfile(@CurrentUser Account account, @Valid @ModelAttribute Profile profile, Errors errors,
                                Model model, RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PROFILE_VIEW_NAME;
        }

        redirect.addFlashAttribute("message", "프로필을 수정했습니다.");

        log.info("profileImage : " + profile.getProfileImage());
        accountService.updateProfile(account, profile);
        return "redirect:"+SETTINGS_PROFILE_VIEW_URL;
    }

    @GetMapping(SETTINGS_PASSWORD_VIEW_URL)
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return SETTINGS_PASSWORD_VIEW_NAME;
    }

    @PostMapping(SETTINGS_PASSWORD_VIEW_URL)
    public String updatePassword(@CurrentUser Account account, @Valid @ModelAttribute PasswordForm passwordForm
            , Errors errors, Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_PASSWORD_VIEW_NAME;
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "패스워드를 변경했습니다.");
        return "redirect:" + SETTINGS_PASSWORD_VIEW_URL;
    }

    @GetMapping(SETTINGS_NOTIFICATIONS_VIEW_URL)
    public String notificationsForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, Notifications.class));

        return SETTINGS_NOTIFICATIONS_VIEW_NAME;
    }

    @PostMapping(SETTINGS_NOTIFICATIONS_VIEW_URL)
    public String updateNotifications(@CurrentUser Account account, @Valid @ModelAttribute Notifications notifications,
                                      Model model,Errors errors, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_NOTIFICATIONS_VIEW_NAME;
        }
        accountService.updateNotifications(account, notifications);
        attributes.addFlashAttribute("message", "알림 설정을 변경했습니다.");
        return "redirect:" + SETTINGS_NOTIFICATIONS_VIEW_URL;
    }

    @GetMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccountForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(modelMapper.map(account, NicknameForm.class));
        return SETTINGS_ACCOUNT_VIEW_NAME;
    }

    @PostMapping(SETTINGS_ACCOUNT_URL)
    public String updateAccount(@CurrentUser Account account, @Valid @ModelAttribute NicknameForm nicknameForm, Errors errors,
                                Model model, RedirectAttributes attributes) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return SETTINGS_ACCOUNT_VIEW_NAME;
        }

        accountService.updateNickname(account, nicknameForm.getNickname());
        attributes.addFlashAttribute("message", "닉네임을 수정했습니다.");
        return "redirect:"+SETTINGS_ACCOUNT_URL;
    }

    @GetMapping(SETTINGS_TAGS_URL)
    public String updateTags(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        Set<Tag> tags = accountService.getTags(account);
        model.addAttribute("tags", tags.stream()
                .map(Tag::getTitle)
                .collect(Collectors.toList()));

        return SETTINGS_TAGS_VIEW_NAME;
    }

    @ResponseBody
    @PostMapping(SETTINGS_TAGS_URL+"/add")
    public ResponseEntity addTag(@CurrentUser Account account,
                                 @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Tag tag = tagRepository.findByTitle(title)
                .orElseGet(() -> tagRepository.save(Tag.builder()
                                            .title(title)
                                            .build()));

        accountService.addTag(account, tag);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping(SETTINGS_TAGS_URL+"/remove")
    public ResponseEntity removeTag(@CurrentUser Account account,
                                 @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();

        Optional<Tag> tag = tagRepository.findByTitle(title);

        if (tag.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        accountService.removeTag(account, tag.get());
        return ResponseEntity.ok().build();
    }


}

package com.studyolle.settings;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PasswordForm {
    @Length(min = 8, max = 50)
    private String newPassowrdConfirm;

    @Length(min = 8, max = 50)
    private String newPassword;
}

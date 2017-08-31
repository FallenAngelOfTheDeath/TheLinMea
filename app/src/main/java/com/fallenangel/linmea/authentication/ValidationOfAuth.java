package com.fallenangel.linmea.authentication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NineB on 8/30/2017.
 */

public class ValidationOfAuth {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private Matcher matcher;

    public boolean validatePassword(String password) {
        return password.length() > 6;
    }

    public boolean validateEmail(String email) {
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean passwordComparison (String password1, String password2){
//        boolean validate;
//        if (password1 == password2){
//            validate = true;
//        } else validate = false;
        return  password1 == null ? password2 == null : password1.equals(password2);
    }
}

/*
 * Created by Кондрашов Дмитрий Эдуардович
 * Copyright (C) 2018. All rights reserved.
 * email: kondrashovde@gmail.com
 *
 * Last modified 1/26/18 5:59 PM
 */

package com.fallenangel.linmea._modulus.auth.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NineB on 8/30/2017.
 */

public class ValidationOfAuth {

    public static boolean validatePassword(String password) {
        return password.length() > 6;
    }

    public static boolean validateEmail(String email) {
        final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean passwordComparison (String password1, String password2){
        return  password1 == null ? password2 == null : password1.equals(password2);
    }
}

package com.alexcorp.bloggers.utils;

public class ValidateUtils {

    public static String validatePhoneNumber(String phone) {
        if(isPhoneNumber(phone)) return phone.substring(phone.length() - 9);

        return "";
    }

    public static boolean isPhoneNumber (String phone) {
        phone = phone.replaceAll("\\(\\)\\+ ", "");

        return isNumber(phone);
    }

    public static boolean isNumber(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }

        return true;
    }
}

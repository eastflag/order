package com.spider.order.util;

import java.io.UnsupportedEncodingException;

public class CommonUtil {
    public static String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;
        if (hexadecimal.indexOf("1B401B2118202020202020202020202020202020B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) {
            orderAppKind = "BM";
        }

        return orderAppKind;
    }

    public static String decode(String hexadecimal) {
        String result = null;

        // 특수문자 제거
        // 1B: <-
        // 40: @
        // 21: !
        // 18: 위로 화살표
        // 20: 스페이스
        // 0A0D: 엔터
        // 배민 ---------------------------------------------------
        hexadecimal = hexadecimal.replace("1B401B2118", "");
        hexadecimal = hexadecimal.replace("1B21001B2118", "");
        hexadecimal = hexadecimal.replace("1B2100", "");
        hexadecimal = hexadecimal.replace("1B2118", "");

        int len = hexadecimal.length();
        byte[] ans = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            // using left shift operator on every character
            ans[i / 2] = (byte) ((Character.digit(hexadecimal.charAt(i), 16) << 4)
                    + Character.digit(hexadecimal.charAt(i + 1), 16));
        }

        try {
            result = new String(ans, "euc-kr");
        } catch (UnsupportedEncodingException e) {

        }

        return result;
    }
}

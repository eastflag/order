package com.spider.order.util;

import java.io.UnsupportedEncodingException;

public class CommonUtil {

    public static String decode(String hexadecimal) {
        String result = null;

        // 특수문자 제거
        // 1B: <-
        // 40: @
        // 21: !
        // 18: 위로 화살표
        // 45: E
        // 20: 스페이스
        // 0A0D: 엔터

        hexadecimal = hexadecimal.replace("1B21001D2400001D7630302C0004000A0D", ""); // 구배민
        hexadecimal = hexadecimal.replace("1B401D427920", ""); // 구배민
        hexadecimal = hexadecimal.replace("1B21001B2118", ""); // 신배민
        hexadecimal = hexadecimal.replace("1B401B2118", "");   // 신배민
        hexadecimal = hexadecimal.replace("1B2100", "");       // 신배민: 일반텍스트
        hexadecimal = hexadecimal.replace("1B2118", "");       // 신배민: 인쇄 모드 설정
        hexadecimal = hexadecimal.replace("1B4500", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B4501", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B40", "");         // 구배민: 프린트 초기화
        hexadecimal = hexadecimal.replace("1B61", "");         // 구배민: 프린트 초기화
        hexadecimal = hexadecimal.replace("201D42", "");       // 구배민: 사전결부 여부 오른쪽 특수문자

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

    public static String decodeYG(String hexadecimal) {
        String result = null;

        // 특수문자 제거
        // 1B: <-
        // 40: @
        // 21: !
        // 18: 위로 화살표
        // 45: E
        // 20: 스페이스
        // 0A0D: 엔터

        hexadecimal = hexadecimal.replace("1B4501", "");
        hexadecimal = hexadecimal.replace("1B45", "");
        hexadecimal = hexadecimal.replace("1B32", "");
        hexadecimal = hexadecimal.replace("1B61", "");
        hexadecimal = hexadecimal.replace("1B21", "");
        hexadecimal = hexadecimal.replace("1B4D", "");
        hexadecimal = hexadecimal.replace("1D42", "");
        hexadecimal = hexadecimal.replace("1D2101", "");
        hexadecimal = hexadecimal.replace("1D21", "");
        hexadecimal = hexadecimal.replace("00", "");


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

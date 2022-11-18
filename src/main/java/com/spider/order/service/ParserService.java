package com.spider.order.service;

import com.spider.order.dto.MenuDTO;
import com.spider.order.dto.OptionDTO;
import com.spider.order.dto.ServerRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ParserService {
    public String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;
        if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A4231") >= 0) { // 주문번호:B1
            orderAppKind = "BM";
        }

        return orderAppKind;
    }

    public List<String> getSplit(String hexadecimal) {
        List<String> encodedList = Arrays.asList(hexadecimal.split("0A0D"));

        return encodedList;
    }

/*    public List<String> splitAndDecode(String hexadecimal) {
        List<String> resultList = new ArrayList<>();
        List<String> encodedList = Arrays.asList(hexadecimal.split("0A0D"));

        for (String encoded : encodedList) {
            resultList.add(decode(encoded));
        }
        return resultList;
    }*/

    public String decode(String hexadecimal) {
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
        hexadecimal = hexadecimal.replace("1B21001B2118", ""); // 신배민
        hexadecimal = hexadecimal.replace("1B401B2118", "");   // 신배민
        hexadecimal = hexadecimal.replace("1B2100", "");       // 신배민
        hexadecimal = hexadecimal.replace("1B2118", "");       // 신배민
        hexadecimal = hexadecimal.replace("1B4500", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B4501", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B40", "");         // 구배민

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

    public ServerRequestDTO parseBM(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);
            System.out.println("encoded length: " + encoded.length());

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                     수량       금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
                if (order.indexOf("메뉴명                     수량       금액") >= 0 ||
                        order.indexOf("------------------------------------------") >= 0 ||
                        order.indexOf("배달팁") >= 0) { // do nothing

                } else if (order.indexOf("+ ") >= 0) { // 메뉴 옵션 파싱
                    OptionDTO optionDTO = new OptionDTO();
                    optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
                    // 옵션 수량, 금액 파싱
                    this.parseOption(order, optionDTO);

                    // 메뉴에 옵션 추가
                    menuDTO.getOptionList().add(optionDTO);
                } else if (order.indexOf("합계") >= 0) { // 메뉴 파싱 종료
                    builder.orderMenuList(menuList);
                    menuDTO = null;
                    menuList = null;
                } else { // 메뉴 파싱
                    menuDTO = new MenuDTO();
                    menuDTO.setNum(String.valueOf(menuList.size() + 1));
                    menuDTO.setOptionList(new ArrayList<>());
                    this.parseMenu(order, menuDTO);
                    // 메뉴 리스트에 메뉴 추가
                    menuList.add(menuDTO);
                }
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim());

        return builder.build();
    }

    private void parseMenu(String order, MenuDTO menuDTO) {
        List<String> menuList = new ArrayList<>();

        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                menuList.add(item.trim());
            }
        }

        if (menuList.size() == 0) {
            return;
        }

        menuDTO.setMenu(menuList.get(0));
        if (menuList.size() == 3) {
            menuDTO.setQuantity(menuList.get(1));
            menuDTO.setPrice(menuList.get(2));
        }
    }

    private void parseOption(String order, OptionDTO optionDTO) {
        List<String> optionList = new ArrayList<>();

        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                optionList.add(item.trim());
            }
        }

        if (optionList.size() == 0) {
            return;
        }

        optionDTO.setMenu(optionList.get(0).replace(" +", "").trim());
        if (optionList.size() == 3) {
            optionDTO.setQuantity(optionList.get(1));
            optionDTO.setPrice(optionList.get(2));
        }
    }
}

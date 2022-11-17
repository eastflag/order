package com.spider.order.service;

import com.spider.order.dto.MenuDTO;
import com.spider.order.dto.OptionDTO;
import com.spider.order.dto.ServerRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ParserService {
    public String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;
        if (hexadecimal.indexOf("1B401B2118202020202020202020202020202020B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) {
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

    public ServerRequestDTO parseBM(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

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
                System.out.println("decoded: " + order);
                System.out.println("encoded: " + encoded);
                System.out.println("encoded length: " + encoded.length());
                if (order.indexOf("메뉴명                     수량       금액") >= 0 ||
                        order.indexOf("------------------------------------------") >= 0 ||
                        order.indexOf("배달팁") >= 0) { // do nothing


                } else if (order.indexOf("+ ") >= 0) { // 메뉴 옵션 파싱
                    OptionDTO optionDTO = new OptionDTO();
                    optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
                    // 옵션 수량, 금액 파싱
                    if (order.length() >= 19) { //  있는 경우
                        optionDTO.setMenu(order.substring(3, 19).trim());
                        String[] quantityAndPrice = order.substring(19).split(" ");
                        optionDTO.setQuantity(quantityAndPrice[0].trim());
                        optionDTO.setPrice(quantityAndPrice[quantityAndPrice.length - 1].trim());
                    } else {  // 없는 경우
                        optionDTO.setMenu(order.substring(3).trim());
                    }

                    // 메뉴에 옵션 추가
                    menuDTO.getOptionList().add(optionDTO);
                } else if (order.indexOf("합계") >= 0) { // 메뉴 파싱 종료
                    builder.orderMenuList(menuList);
                    menuDTO = null;
                    menuList = null;
                } else { // 메뉴 파싱
                    menuDTO = new MenuDTO();
                    menuDTO.setNum(String.valueOf(menuList.size() + 1));
                    menuDTO.setMenu(order.substring(0, 18).trim());
                    // 메뉴 수량, 금액 파싱
                    String[] quantityAndPrice = order.substring(18).split(" ");
                    menuDTO.setQuantity(quantityAndPrice[0].trim());
                    menuDTO.setPrice(quantityAndPrice[quantityAndPrice.length - 1].trim());
                    menuDTO.setOptionList(new ArrayList<>());
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
}

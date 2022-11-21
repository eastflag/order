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

        if (hexadecimal.indexOf("B9E8B9CE3120C1D6B9AEC0FCC7A5") >= 0) { // 배민1 주문전표, 신배민-배민원
            orderAppKind = "BM_one_new";
        } else if (hexadecimal.indexOf("B9E8B9CE31C1D6B9AE") >= 0) { // 배민1주문, 구배민-배민원
            orderAppKind = "BM_one_old";
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A4231") >= 0) { // 주문번호:B1, 신배민
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 배달 주문전표, 신배민/배달
                orderAppKind = "BM_new_del";
            } else if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 포장 주문전표, 신배민/포장
                orderAppKind = "BM_new_wrap";
            }
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A3203A204231") >= 0) { // 주문번호 : B1, 구배민
            if (hexadecimal.indexOf("C6F7C0E520C1A2BCF6B9F8C8A3") >= 0) { // 포장 접수번호, 구배민/포장
                orderAppKind = "BM_old_wrap";
            } else {                                                      // 구배민/배달
                orderAppKind = "BM_old_del";
            }
        }

        return orderAppKind;
    }

    public List<String> getSplit(String hexadecimal) {
        List<String> encodedList = Arrays.asList(hexadecimal.split("0A0D"));

        return encodedList;
    }

    public ServerRequestDTO parse(List<String> encodingList, String orderAppKind) {
        ServerRequestDTO serverRequestDTO = null;

        switch (orderAppKind) {
            case "BM_one_new":
                serverRequestDTO = parseBM_one_new(encodingList);
                break;
            case "BM_one_old":
                serverRequestDTO = parseBM_one_old(encodingList);
                break;
            case "BM_new_del":
                serverRequestDTO = parseBM_new_del(encodingList);
                break;
            case "BM_new_wrap":
                serverRequestDTO = parseBM_new_wrap(encodingList);
                break;
            case "BM_old_del":
                serverRequestDTO = parseBM_old_del(encodingList);
                break;
            case "BM_old_wrap":
                serverRequestDTO = parseBM_old_wrap(encodingList);
                break;
        }

        if (serverRequestDTO != null) {
            serverRequestDTO.setOrderAppKind(orderAppKind.substring(0, 2));
        }

        return serverRequestDTO;
    }

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
        hexadecimal = hexadecimal.replace("1B401D427920", ""); // 구배민
        hexadecimal = hexadecimal.replace("1B21001B2118", ""); // 신배민
        hexadecimal = hexadecimal.replace("1B401B2118", "");   // 신배민
        hexadecimal = hexadecimal.replace("1B2100", "");       // 신배민: 일반텍스트
        hexadecimal = hexadecimal.replace("1B2118", "");       // 신배민: 인쇄 모드 설정
        hexadecimal = hexadecimal.replace("1B4500", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B4501", "");       // 구배민
        hexadecimal = hexadecimal.replace("1B40", "");         // 구배민: 프린트 초기화

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


    // 신배민 - 배달
    private ServerRequestDTO parseBM_new_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

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

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(order.replace("결제방식", "").trim());
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(splitOrders[splitOrders.length - 1]);
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
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
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_new_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

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

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(order.replace("결제방식", "").trim());
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(splitOrders[splitOrders.length - 1]);
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
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
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    // 구배민 배달
    private ServerRequestDTO parseBM_old_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        int addressIndex = -1;
        int shopRemarkIndex = -1;
        int orderRemarkIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);
            System.out.println("encoded length: " + encoded.length());

            // 주문번호 파싱
            if (order.indexOf("주문번호 :") >= 0) {
                builder.orderNumber(order.replace("주문번호 :", "").trim());
            }

            // 주문일자 파싱
            if (order.indexOf("주문일시 :") >= 0) {
                builder.orderNumber(order.replace("주문일시 :", "").trim());
            }

            // 결제방식 파싱
            if (order.indexOf("사전결제 여부:") >= 0) {
                if (order.indexOf("사전결제 여부: O") >= 0) {
                    builder.orderPayKind(order.replace("사전결제 여부: O", "").replace("[고객용]", "").trim());
                } else if (order.indexOf("사전결제 여부: X") >= 0) {
                    builder.orderPayKind(order.replace("사전결제 여부: X", "").replace("[고객용]", "").trim());
                }
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계 :") >= 0) {
                builder.orderSum(order.replace("합계 :", "").trim());
            }

            // 가게 요청 사항
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemarkIndex = index;
            }
            // 배달 요청 사항
            if (order.indexOf("배달 요청사항:") >= 0) {
                orderRemarkIndex = index;
            }

            // 주소
            System.out.println(originalJibunAddress);
            if (order.indexOf("배달주소 :") >= 0 && order.indexOf("배달주소 : ") < 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("배달주소 :") >= 0) {
                    originalJibunAddress = order.replace("배달주소 :", "").trim();
                } else if (order.indexOf("(도로명)") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order;
                }
            }
            if (order.indexOf("(도로명)") >= 0) {
                originalRoadAddress = "";
            }
            if (originalRoadAddress != null) {
                if (order.indexOf("(도로명)") >= 0) {
                    // 도로명 주소 시작
                    originalRoadAddress = order.replace("(도로명)", "").trim();
                } else if (order.indexOf("연락처") >= 0) {
                    // 도로명 주소 끝
                    builder.originalRoadAddress(originalRoadAddress);
                    originalRoadAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalRoadAddress += order;
                }
            }

            // 연락처
            if (order.indexOf("연락처 :") >= 0) {
                builder.orderPhone(order.replace("연락처 :", "").trim());
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴                        수량      금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
                if (order.indexOf("메뉴                        수량      금액") >= 0 ||
                        order.indexOf("------------------------------------------") >= 0 ||
                        order.indexOf("배달팁") >= 0) { // do nothing

                } else if (order.indexOf(" +") >= 0) { // 메뉴 옵션 파싱
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 가게 요청 사항
        builder.shopRemark(this.decode(encodingList.get(shopRemarkIndex + 1)).trim());
        // 주문 요청 사항
        builder.orderRemark(this.decode(encodingList.get(orderRemarkIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_old_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

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

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(order.replace("결제방식", "").trim());
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(splitOrders[splitOrders.length - 1]);
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
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
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_one_new(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

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

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(order.replace("결제방식", "").trim());
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(splitOrders[splitOrders.length - 1]);
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
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
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_one_old(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        MenuDTO menuDTO = null;
        String orderMenu = "";

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

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(order.replace("결제방식", "").trim());
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(order.replace("배달팁", "").trim());
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(splitOrders[splitOrders.length - 1]);
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
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
                    orderMenu += order; // 원본 메뉴
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
                } else if (order.startsWith("    ")) { // 메뉴가 개행 된경우: 수량, 가격이 한줄 아래로 밀린다.
                    orderMenu += order;
                    this.parseMenu(order, menuDTO);
                } else { // 메뉴 파싱
                    orderMenu += order; // 원본 메뉴
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

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim());

        // 원본 메뉴
        builder.orderMenu(orderMenu);

        builder.orderCarryType("D"); // 배달
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

        if (menuList.size() == 1) {
            menuDTO.setMenu(menuList.get(0));
        }
        if (menuList.size() == 2) {
            menuDTO.setQuantity(menuList.get(0));
            menuDTO.setPrice(menuList.get(1));
        }
        if (menuList.size() == 3) {
            menuDTO.setMenu(menuList.get(0));
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

        optionDTO.setMenu(optionList.get(0).replace("+", "").trim());
        if (optionList.size() == 3) {
            optionDTO.setQuantity(optionList.get(1));
            optionDTO.setPrice(optionList.get(2));
        }
    }
}

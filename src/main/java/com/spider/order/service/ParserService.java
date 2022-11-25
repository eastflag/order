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
    private static final int INITIAL = 0;
    private static final int MENU = 10;       // 메뉴 (3)
    private static final int MENU_TITLE = 11; // 메뉴 제목 (1)
    private static final int MENU_PRICE = 12;   // 메뉴 수량, 가격 개행 (2)
    private static final int MENU_TITLE_ADD = 13;      // 메뉴 제목 개행 (1)
    private static final int OPTION = 20; // 옵션 (1)
    private static final int OPTION_ADD = 21; // 옵션 개행 (1)

    public String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;

        if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A5430") >= 0) { // C1D6B9AEB9F8C8A33A5430, 배민라이더스
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 배달 주문전표, 배달
                orderAppKind = "BR_del";
            } else { // 포장 주문전표, 신배민/포장
                orderAppKind = "BR_wrap";
            }
        } else if (hexadecimal.indexOf("B9E8B9CE3120C1D6B9AEC0FCC7A5") >= 0) { // 배민1 주문전표, 신배민-배민원
            orderAppKind = "BM_one_new";
        } else if (hexadecimal.indexOf("B9E8B9CE31C1D6B9AE") >= 0) { // 배민1주문, 구배민-배민원
            orderAppKind = "BM_one_old";
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A4231") >= 0) { // 주문번호:B1, 신배민
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 배달 주문전표, 신배민/배달
                orderAppKind = "BM_new_del";
            } else { // 포장 주문전표, 신배민/포장
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
            case "BR_del":
                serverRequestDTO = parseBR_del(encodingList);
                break;
            case "BR_wrap":
                serverRequestDTO = parseBR_wrap(encodingList);
                break;
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


    // 신배민 - 배달
    private ServerRequestDTO parseBM_new_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달팁", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(this.convertPrice(splitOrders[splitOrders.length - 1]));
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
            if (menuList != null && order.indexOf("메뉴명                     수량       금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(this.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.convertOrderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim()));

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_new_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String shopRemark = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 합계 파싱 & 결제 방식
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(this.convertPrice(splitOrders[splitOrders.length - 1]));
                // 결제방식
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 가게 요청 사항
            if (order.indexOf("요청사항:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("요청사항:") >= 0) {
                    // do nothing
                } else if (order.indexOf("----") >= 0) { // 파싱 종료
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                } else {  // 파싱
                    shopRemark += order.replace("가게 :", "").trim();
                }
            }

            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                     수량       금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴명                     수량       금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 연락처
        builder.orderPhone(this.convertOrderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim()));

        builder.orderCarryType("P"); // 픽업
        return builder.build();
    }

    // 구배민 배달
    private ServerRequestDTO parseBM_old_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        int shopRemarkIndex = -1;
        int orderRemarkIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호 :") >= 0) {
                builder.orderNumber(order.replace("주문번호 :", "").trim());
            }

            // 주문일자 파싱
            if (order.indexOf("주문일시 :") >= 0) {
                builder.orderDate(this.convertOrderDate(order.replace("주문일시 :", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("사전결제 여부:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달팁", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계 :") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계 :", "").trim()));
            }

            // 가게 요청 사항
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemarkIndex = index;
            }
            // 배달 요청 사항
            if (order.indexOf("배달 요청사항:") >= 0) {
                orderRemarkIndex = index;
            }

            // 지번 주소
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
            // 도로명 주소
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
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처 :", "").trim()));
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴                        수량      금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴                        수량      금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 가게 요청 사항
        builder.shopRemark(this.decode(encodingList.get(shopRemarkIndex + 1)).trim());
        // 주문 요청 사항
        builder.orderRemark(this.decode(encodingList.get(orderRemarkIndex + 1)).trim());

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    // 구배민 - 포장
    private ServerRequestDTO parseBM_old_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String shopRemark = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호 :") >= 0) {
                builder.orderNumber(order.replace("주문번호 :", "").trim());
            }

            // 주문일자 파싱
            if (order.indexOf("주문일시 :") >= 0) {
                builder.orderDate(this.convertOrderDate(order.replace("주문일시 :", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("사전결제 여부:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 합계 파싱
            if (order.indexOf("합계 :") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계 :", "").trim()));
            }

            // 가게 요청 사항
            if (order.indexOf("요청사항:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("요청사항:") >= 0) {
                    // do nothing
                } else if (order.indexOf("----") >= 0) { // 파싱 종료
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                } else {  // 파싱
                    shopRemark += order.replace("가게 :", "").trim();
                }
            }

            // 주소
            if (order.indexOf("배달주소 :") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("배달주소 :") >= 0) {
                    originalJibunAddress = order.replace("배달주소 :", "").trim();
                } else if (order.indexOf("연락처 :") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order;
                }
            }

            // 연락처
            if (order.indexOf("연락처 :") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처 :", "").trim()));
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴                        수량      금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴                        수량      금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        builder.orderCarryType("P"); // 픽업
        return builder.build();
    }

    private ServerRequestDTO parseBM_one_new(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("총 배달팁") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("총 배달팁", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(this.convertPrice(splitOrders[splitOrders.length - 1]));
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.replace("가게 :", "").trim());

            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.replace("배달 :", "").trim());
            }

            // 주소
            if (order.indexOf("배달주소:") >= 0) {
                addressIndex = index;
            }
            // 연락처

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                     수량       금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴명                     수량       금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());

        builder.orderCarryType("A"); // 자체배달
        return builder.build();
    }

    private ServerRequestDTO parseBM_one_old(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        int shopRemarkIndex = -1;
        int orderRemarkIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String ingredientOrigins = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호 :") >= 0) {
                builder.orderNumber(order.replace("주문번호 :", "").trim());
            }

            // 주문일자 파싱
            if (order.indexOf("주문일시 :") >= 0) {
                builder.orderDate(this.convertOrderDate(order.replace("주문일시 :", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("사전결제 여부:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("총 배달팁") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("총 배달팁", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계 :") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계 :", "").trim()));
            }

            // 가게 요청 사항
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemarkIndex = index;
            }
            // 배달 요청 사항
            if (order.indexOf("배달 요청사항:") >= 0) {
                orderRemarkIndex = index;
            }

            // 지번 주소
            if (order.indexOf("배달주소 :") >= 0) {
                if (builder.build().getOriginalJibunAddress() == null) {
                    originalJibunAddress = "";
                }
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("배달주소 :") >= 0) {
                    originalJibunAddress = order.replace("배달주소 :", "").trim();
                } else if (order.indexOf("안심번호는") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order;
                }
            }
            // 도로명 주소

            // 연락처

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴                        수량      금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴                        수량      금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            // 원산지 파싱
            if (order.indexOf("주문번호 :") >= 0) {
                ingredientOrigins = "";
            }
            if (ingredientOrigins != null) {
                if (order.indexOf("주문번호 :") >= 0 || order.indexOf("----") >= 0) {
                    // nothing
                } else if (encoded.indexOf("1B69") >= 0) { // 종료
                    builder.ingredientOrigins(ingredientOrigins);
                    ingredientOrigins = null;
                } else {
                    ingredientOrigins += order;
                }
            }

            ++index;
        }

        // 가게 요청 사항
        builder.shopRemark(this.decode(encodingList.get(shopRemarkIndex + 1)).trim());
        // 주문 요청 사항
        builder.orderRemark(this.decode(encodingList.get(orderRemarkIndex + 1)).trim());

        builder.orderCarryType("A"); // 자체 배달
        return builder.build();
    }

    private ServerRequestDTO parseBR_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 결제방식 파싱
            if (order.indexOf("결제방식") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(this.convertPrice(splitOrders[splitOrders.length - 1]));
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

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                     수량       금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴명                     수량       금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 원산지 파싱
        builder.ingredientOrigins(this.decode(encodingList.get(orderNumberIndex + 3)).trim());

        // 주소 파싱
        builder.originalJibunAddress(this.decode(encodingList.get(addressIndex + 1)).trim());

        builder.orderCarryType("A"); // 자체 배달
        return builder.build();
    }

    private ServerRequestDTO parseBR_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = this.decode(encoded);

            System.out.println("decoded: " + order);
            System.out.println("encoded: " + encoded);

            // 주문번호 파싱
            if (order.indexOf("주문번호:") >= 0) {
                builder.orderNumber(order.replace("주문번호:", "").trim());
                orderNumberIndex = index;
            }

            // 결제방식 파싱

            // 합계 파싱
            if (order.indexOf("합계(") >= 0) {
                String[] splitOrders = order.split("  ");
                builder.orderSum(this.convertPrice(splitOrders[splitOrders.length - 1]));
            }

            // 가게 요청 사항
            if (order.indexOf("가게 :") >= 0) {
                builder.shopRemark(order.split(":")[1].trim());
            }
            // 배달 요청 사항
            if (order.indexOf("배달 :") >= 0) {
                builder.orderRemark(order.split(":")[1].trim());
            }

            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                phoneIndex = index;
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                     수량       금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null && order.indexOf("메뉴명                     수량       금액") < 0) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(this.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 원산지 파싱
        builder.ingredientOrigins(this.decode(encodingList.get(orderNumberIndex + 3)).trim());

        // 연락처 파싱
        builder.orderPhone(this.convertOrderPhone(this.decode(encodingList.get(phoneIndex + 1)).trim()));

        builder.orderCarryType("P"); // 픽업(포장)
        return builder.build();
    }

    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if (order.indexOf("------------------------------------------") >= 0 ||
                order.indexOf("배달팁") >= 0) { // do nothing

        } else if (order.startsWith(" +")) { // 메뉴 옵션 파싱
            orderMenu.append(order); // 원본 메뉴

            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
            // 옵션 수량, 금액 파싱
            this.parseOption(order, optionDTO);

            // 메뉴에 옵션 추가
            menuDTO.getOptionList().add(optionDTO);
        } else if (order.indexOf("합계") >= 0) { // 메뉴 파싱 종료
            builder.orderMenuList(menuList);
            builder.orderMenu(orderMenu.toString());
        } else { // 메뉴 파싱: 메뉴이면 menuDTO 교체, 개행이면 기존 menuDTO 사용
            orderMenu.append(order); // 원본 메뉴
            this.parseMenu(order, menuList);
        }
    }

    // 메뉴 파싱이 되면 새로 생성된 MenuDTO가 리턴
    private MenuDTO parseMenu(String order, ArrayList<MenuDTO> menuList) {
        MenuDTO menuDTO = null;
        if (menuList.size() > 0) {
            menuDTO = menuList.get(menuList.size() - 1);
        }

        List<String> menuParsingList = new ArrayList<>();
        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                menuParsingList.add(item.trim());
            }
        }

        if (menuParsingList.size() == 1) { // 메뉴제목, 메뉴제목 개행이 구분이 안된다. 그 다음에거롤 보고 판단한다.
            // 메뉴 제목 개행이라고 가정하고 기존 menuDTO에 추가한다. 그리고 tempTitle에도 추가한다.
            if (menuDTO == null) {
                // 첫번째 줄인 경우
                MenuDTO newMenuDTO = new MenuDTO();
                newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
                newMenuDTO.setOptionList(new ArrayList<>());
                newMenuDTO.setMenu(menuParsingList.get(0));
                menuList.add(newMenuDTO); // 메뉴 리스트에 메뉴 추가
            } else {
                // 두번째 줄 이상인 경우: 기존 menuDTO에 넣는다.
                menuDTO.setMenu(menuDTO.getMenu() + menuParsingList.get(0));
                menuDTO.setTempTitle(menuParsingList.get(0));
            }
        }
        if (menuParsingList.size() == 2) { // 수량, 가격이 개행되는 경우
            if (menuDTO.getPrice() == null) {
                // 두번째 줄인 경우
                menuDTO.setQuantity(menuParsingList.get(0));
                menuDTO.setPrice(this.convertPrice(menuParsingList.get(1)));
            } else {
                // MenuDTO의 menu에 추가된 것을 지운다.
                menuDTO.setMenu(menuDTO.getMenu().replace(menuDTO.getTempTitle(), ""));
                // 신규 MenuDTO를 만들어서 추가한다.
                MenuDTO newMenuDTO = new MenuDTO();
                newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
                newMenuDTO.setOptionList(new ArrayList<>());
                newMenuDTO.setMenu(menuDTO.getTempTitle());
                newMenuDTO.setQuantity(menuParsingList.get(0));
                newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(1)));
                menuList.add(newMenuDTO); // 메뉴 리스트에 메뉴 추가
            }
        }

        if (menuParsingList.size() == 3) { // 메뉴 파싱, 신규 메뉴 생성하고 리턴
            MenuDTO newMenuDTO = new MenuDTO();
            newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
            newMenuDTO.setOptionList(new ArrayList<>());
            newMenuDTO.setMenu(menuParsingList.get(0));
            newMenuDTO.setQuantity(menuParsingList.get(1));
            newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
            // 메뉴 리스트에 메뉴 추가
            menuList.add(newMenuDTO);
            return newMenuDTO;
        } else {
            return null;
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

    private String convertOrderDate(String orderDate) {
        if (orderDate.indexOf("(") >= 0) {
            // 구배민 "2022-11-16(수) 12:17",   ==> 202211161217
            return orderDate.substring(0, 10).replace("-", "") + orderDate.substring(14).replace(":", "");
        } else if (orderDate.indexOf(".") >= 0) {
            // 신배민 "2022.11.17 11:02"
            return orderDate.replace(".", "").replace(":", "").replace(" ", "").trim();
        } else {
            return "";
        }
    }

    private String convertOrderPhone(String orderPhone) {
        // 구배민 (안심번호)050-71252-9487 => 050712529487
        return orderPhone.replace("(안심번호)", "").replace("-", "").split("\n")[0];
    }

    private String convertPrice(String price) {
        return price.replace("원", "").replace(",", "").trim();
    }

    private String convertOrderPayKind(String order) {
        if (order.indexOf("사전결제 여부:") >= 0) { // 구배민
            if (order.indexOf("O") >= 0) {
                return "사전";
            } else if (order.indexOf("현금") >= 0) {
                return "현금";
            } else if (order.indexOf("카드") >= 0) {
                return "카드";
            }
        } else { // 신배민
            if (order.indexOf("결제완료") >= 0) {
                return "사전";
            } else if (order.indexOf("현금") >= 0) {
                return "현금";
            } else if (order.indexOf("카드") >= 0) {
                return "카드";
            }
        }
        return "";
    }
}

package com.spider.order.service;

import com.spider.order.dto.MenuDTO;
import com.spider.order.dto.OptionDTO;
import com.spider.order.dto.ServerRequestDTO;
import com.spider.order.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ParserBMoldService {
    private static final int INITIAL = 0;
    private static final int MENU = 10;       // 메뉴 (3)
    private static final int MENU_TITLE = 11; // 메뉴 제목 (1)
    private static final int MENU_PRICE = 12;   // 메뉴 수량, 가격 개행 (2)
    private static final int MENU_TITLE_ADD = 13;      // 메뉴 제목 개행 (1)
    private static final int OPTION = 20; // 옵션 (1)
    private static final int OPTION_ADD = 21; // 옵션 개행 (1)

    // 구배민 배달
    public ServerRequestDTO parseDel(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        String shopRemark = null;
        String orderRemark = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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

            // 가게 요청 사항, 배달 요청 사항
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("배달 요청사항:") >= 0) {
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                    orderRemark = "";
                } else {
                    shopRemark += order.replace("가게 요청사항:", "").trim();
                }
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    // do nothing
                } else if (order.indexOf("메뉴") >= 0) {
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("배달 요청사항: ", "").trim();
                }
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
            if (order.indexOf("메뉴") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    // 구배민 - 포장
    public ServerRequestDTO parseWrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String shopRemark = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("----") >= 0) { // 파싱 종료
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                } else {  // 파싱
                    shopRemark += order.replace("가게 요청사항:", "").trim();
                }
            }

            // 주소
            if (order.indexOf("배달주소 :") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("연락처 :") >= 0) { // 지번 주소 끝
                    // 주문 정보는 고객의\n개인 정보 를 삭제한다.
                    builder.originalJibunAddress(originalJibunAddress.replace("주문 정보는 고객의", "").replace("개인 정보", "").trim());
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("배달주소 :", "").trim();
                }
            }

            // 연락처
            if (order.indexOf("연락처 :") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처 :", "").trim()));
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
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

    public ServerRequestDTO parseOne(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String shopRemark = null;
        String orderRemark = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String ingredientOrigins = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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

            // 가게 요청 사항, 배달 요청 사항
            if (order.indexOf("가게 요청사항:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("배달 요청사항:") >= 0) { // 파싱 종료
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                    orderRemark = "";
                } else {
                    shopRemark += order.replace("가게 요청사항:", "").trim();
                }
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    // do nothing
                } else if (order.indexOf("메뉴") >= 0) { // 파싱 종료
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("배달 요청사항: ", "").trim();
                }
            }

            // 지번 주소
            if (order.indexOf("배달주소 :") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("가게 요청사항:") >= 0) { // 지번 주소 끝
                    // 주문 정보는 고객의\n개인 정보 를 삭제한다.
                    int rightIndex = originalJibunAddress.indexOf("주문 정보는 고객의 개인 정보");
                    builder.originalJibunAddress(originalJibunAddress.substring(0, rightIndex).trim());
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("배달주소 :", "");
                }
            }
            // 도로명 주소

            // 연락처

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

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("합계") >= 0) {
                menuList = null;
            }

            ++index;
        }

        builder.orderCarryType("A"); // 자체 배달
        return builder.build();
    }

    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if ((order.indexOf("메뉴") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0)
                || order.indexOf("-----") >= 0
                || order.indexOf("배달팁") >= 0) { // do nothing

        } else if (order.startsWith(" +") || order.startsWith("  ")) {
            // 옵션 파싱
            orderMenu.append(order); // 원본 메뉴

            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            if (order.startsWith(" +")) { // 옵션 추가
                OptionDTO optionDTO = new OptionDTO();
                optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
                optionDTO.setMenu(order.replace(" +", ""));
                this.parseOptionPrice(order, optionDTO);
                menuDTO.getOptionList().add(optionDTO);         // 메뉴에 옵션 추가
            } else { // 옵션 개행
                // 마지막 option 가져오기
                OptionDTO optionDTO = menuDTO.getOptionList().get(menuDTO.getOptionList().size() - 1);
                optionDTO.setMenu(optionDTO.getMenu() + order.substring(2)); //4000원 이 개행될 경우 파싱하도록
                this.parseOptionPrice(optionDTO.getMenu(), optionDTO);
            }
        } else if (order.indexOf("합계") >= 0) { // 메뉴 파싱 종료
            builder.orderMenuList(menuList);
            builder.orderMenu(orderMenu.toString());
        } else { // 메뉴 파싱
            orderMenu.append(order); // 원본 메뉴

            this.parseMenu(order, menuList);
        }
    }

    // 메뉴 파싱이 되면 새로 생성된 MenuDTO가 리턴
    private void parseMenu(String order, ArrayList<MenuDTO> menuList) {
        List<String> menuParsingList = new ArrayList<>();
        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                menuParsingList.add(item.trim());
            }
        }

        if (menuParsingList.size() == 1) { // 메뉴제목 개행
            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            menuDTO.setMenu(menuDTO.getMenu() + menuParsingList.get(0));
        }

        if (menuParsingList.size() == 3) { // 메뉴 파싱, 신규 메뉴 생성하고 리턴
            MenuDTO newMenuDTO = new MenuDTO();
            newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
            newMenuDTO.setOptionList(new ArrayList<>());
            newMenuDTO.setMenu(menuParsingList.get(0));
            newMenuDTO.setQuantity(menuParsingList.get(1));
            newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
            menuList.add(newMenuDTO); // 메뉴 리스트에 메뉴 추가
        }
    }

    private void parseOptionPrice(String order, OptionDTO optionDTO) {

        // 옵션(2,000원) 에서 price 파싱
        if (order.indexOf("(") >= 0 && order.indexOf(")") >= 0) {
            int left = order.lastIndexOf("(");
            int right = order.lastIndexOf(")");
            String strPrice = this.convertPrice(order.substring(left + 1, right));
            try {
                int price = Integer.parseInt(strPrice);
                optionDTO.setMenu(order.substring(0, left).replace(" +", ""));
                optionDTO.setPrice(String.valueOf(price));
            } catch (Exception e) {
                // do nothing
            }
        }

        // 옵션메뉴 400원 에서 price 파싱
        if (order.endsWith("원")) {
            int left = order.lastIndexOf(" ");
            String strPrice = this.convertPrice(order.substring(left + 1));
            try {
                int price = Integer.parseInt(strPrice);
                optionDTO.setMenu(order.substring(0, left).replace(" +", ""));
                optionDTO.setPrice(String.valueOf(price));
            } catch (Exception e) {
                // do nothing
            }
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

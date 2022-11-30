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
public class ParserSHService {

    public ServerRequestDTO parseSH(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderTableIndex = -1; // 주문 전표
        int orderPhoneIndex = -1;
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        String orderRemark = null;
        String shopRemark = null;
        String ingredientOrigins = null;
        int orderAppIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            log.info("encoded: {}", encoded);
            String order = CommonUtil.decodeSH(encoded);
            log.info("decoded: {}", order);

            // 주문 번호, 배달 or 포장
            if (order.indexOf("주문 전표") >= 0) {
                orderTableIndex = index;
            }

            // 연락처
            if (order.indexOf("[고객 연락처]") >= 0) {
                orderPhoneIndex = index;
            }

            // 도로명 주소
            if (order.indexOf("(도로명)") >= 0) {
                originalRoadAddress = "";
            }
            if (originalRoadAddress != null) {
                if (order.indexOf("(지번)") >= 0) {
                    // 도로명 주소 끝
                    builder.originalRoadAddress(originalRoadAddress);
                    originalRoadAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalRoadAddress += order.replace("(도로명)", "");
                }
            }

            // 지번 주소
            if (order.indexOf("(지번)") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("[가게 사장님에게]") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("(지번)", "");
                }
            }

            // 가게 요청 사항
            if (order.indexOf("[가게 사장님에게]") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("[배달 기사님에게]") >= 0 || order.indexOf("-----") >= 0) { // 포장의 경우 고려
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                } else {
                    shopRemark += order.replace("[가게 사장님에게]", "").trim();
                }
            }

            // 배달 요청 사항
            if (order.indexOf("[배달 기사님에게]") >= 0) {
                orderRemark = "";
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("[배달 기사님에게]", "").trim();
                }
            }

            // 배달팁 파싱
            if (order.indexOf("배달비") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달비", "").trim()));
            }

            // 할인금액 파싱
            if (order.indexOf("총 할인금액") >= 0) {
                builder.orderDiscount(this.convertPrice(order.replace("총 할인금액", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("총 결제금액") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("총 결제금액", "").trim()));
            }

            // 원산지 파싱
            if (order.indexOf("[원산지 표시]") >= 0) {
                ingredientOrigins = "";
            }
            if (ingredientOrigins != null) {
                if (order.indexOf("------") >= 0) { // 종료
                    builder.ingredientOrigins(ingredientOrigins.trim());
                    ingredientOrigins = null;
                } else {
                    ingredientOrigins += order.replace("[원산지 표시]", "");
                }
            }

            if (order.indexOf("[땡겨요]") >= 0 && order.indexOf("주문 전표") < 0) {
                orderAppIndex = index;
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
                menuList = new ArrayList<>();
            }
            if (menuList != null) {
                this.parserMenu(order, builder, menuList, orderMenu);
            }
            if (order.indexOf("총 결제금액") >= 0) {
                menuList = null;
            }

            ++index;
        }

        if (orderTableIndex >= 0) {
            String order = CommonUtil.decodeSH(encodingList.get(orderTableIndex + 1));
            String[] splitParser = order.split(" ");
            if (splitParser[0].indexOf("배달") >= 0) {
                builder.orderCarryType("D"); // 배달
            } else {
                builder.orderCarryType("P"); // 포장
            }
            // 주문 번호 파싱
            builder.orderNumber(splitParser[1]);

            // 결제방식 파싱
            order = CommonUtil.decodeSH(encodingList.get(orderTableIndex + 2));
            builder.orderPayKind(this.convertOrderPayKind(order));
        }

        if (orderPhoneIndex >= 0) {
            String order = CommonUtil.decodeSH(encodingList.get(orderPhoneIndex + 1));
            builder.orderPhone(this.convertOrderPhone(order));
        }

        if (orderAppIndex >= 0) {
            // 주문 일자
            String order = CommonUtil.decodeSH(encodingList.get(orderAppIndex - 1));
            builder.orderDate(this.convertOrderDate(order.trim()));
            // 주문 매장
            order = CommonUtil.decodeSH(encodingList.get(orderAppIndex - 2));
            builder.orderStore(order.trim());
        }

        return builder.build();
    }

    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if ((order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0)
                || order.indexOf("-----") >= 0
                || order.indexOf("주문금액") >= 0
                || order.indexOf("할인금액") >= 0
                || order.indexOf("배달비") >= 0) {
            // do nothing
        } else if (order.startsWith(" +")) {
            // 메뉴 옵션 파싱, 메뉴 수량 개행은 제외
            orderMenu.append(order); // 원본 메뉴

            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            if (order.startsWith(" +")) {
                OptionDTO optionDTO = new OptionDTO();
                optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
                optionDTO.setMenu(order.replace(" + ", ""));
                this.parseOptionPrice(order.replace(" + ", ""), optionDTO);
                menuDTO.getOptionList().add(optionDTO);         // 메뉴에 옵션 추가
            } else { // 옵션 개행
                // 마지막 option 가져오기
                OptionDTO optionDTO = menuDTO.getOptionList().get(menuDTO.getOptionList().size() - 1);
                optionDTO.setMenu(optionDTO.getMenu() + order.trim());
                this.parseOptionPrice(optionDTO.getMenu(), optionDTO);
            }
        } else if (order.indexOf("총 결제금액") >= 0) { // 메뉴 파싱 종료
            builder.orderMenuList(menuList);
            builder.orderMenu(orderMenu.toString());
        } else { // 메뉴 파싱
            orderMenu.append(order); // 원본 메뉴
            this.parseMenu(order, menuList);
        }
    }

    // 메뉴 파싱이 되면 새로 생성된 MenuDTO가 리턴
    private void parseMenu(String order, ArrayList<MenuDTO> menuList) {
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

        if (menuParsingList.size() == 1) { // 존재 안함.

        }
        if (menuParsingList.size() == 2) { //
            // 할인 메뉴가 나열됨.
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
                optionDTO.setMenu(order.substring(0, left).trim());
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
                optionDTO.setMenu(order.substring(0, left));
                optionDTO.setPrice(String.valueOf(price));
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    private String convertOrderDate(String orderDate) {
        return orderDate.replace(".", "").replace(":", "").replace(" ", "").trim();
    }

    private String convertOrderPhone(String orderPhone) {
        // 구배민 (안심번호)050-71252-9487 => 050712529487
        return orderPhone.replace("(안심번호)", "").replace("-", "").split("\n")[0];
    }

    private String convertPrice(String price) {
        return price.replace("원", "").replace(",", "").trim();
    }

    private String convertOrderPayKind(String order) {
        if (order.indexOf("결제완료") >= 0) {
            return "사전";
        } else if (order.indexOf("현금") >= 0) {
            return "현금";
        } else if (order.indexOf("카드") >= 0) {
            return "카드";
        } else {
            return "";
        }
    }
}

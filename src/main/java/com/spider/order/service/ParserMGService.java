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
public class ParserMGService {

    public ServerRequestDTO parse(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String originalJibunAddress = null;
        String shopRemark = null;
        String orderRemark = null;
        String ingredientOrigins = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            log.info("encoded: {}", encoded);
            String order = CommonUtil.decodeMG(encoded);
            log.info("decoded: {}", order);

            // 배달계산서, 포장계산서
            if (order.indexOf("계산서") >= 0) {
                if (order.indexOf("배달") >= 0) {
                    builder.orderCarryType("D");
                } else if (order.indexOf("포장") >= 0) {
                    builder.orderCarryType("P");
                }
            }

            // 주문 번호
            if (order.indexOf("No:") >= 0) {
                builder.orderNumber(order.replace("No:", "").replaceAll("^\\s+", ""));
            }

            // 연락처
            if (order.indexOf("전화:") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("전화:", "").trim()));
            }

            // 지번 주소
            if (order.indexOf("주소:") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("사업자정보") >= 0) { // 규격 바뀜, 매장이 내려감.
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("주소: ", "");
                }
            }

            // 가게 요청 사항
            if (order.indexOf("매장:") >= 0) {
                shopRemark = "";
            }
            if (shopRemark != null) {
                if (order.indexOf("기사님 요청:") >= 0 || order.indexOf("먹깨비") >= 0) { // 기사요청 없는 경우 고려
                    builder.shopRemark(shopRemark);
                    shopRemark = null;
                } else {
                    shopRemark += order.replace("매장: ", "");
                }
            }

            // 배달 요청 사항
            if (order.indexOf("기사님 요청:") >= 0) {
                orderRemark = "";
            }
            if (orderRemark != null) {
                if (order.indexOf("먹깨비") >= 0) { // 파싱 종료
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("기사님 요청: ", "");
                }
            }

            // 배달팁 파싱
            if (order.indexOf("배달팁") >= 0) {
                List<String> menuParsingList = new ArrayList<>();
                for (String item : order.split("  ")) {
                    if (StringUtils.hasText(item.trim())) {
                        menuParsingList.add(item.trim());
                    }
                }
                builder.orderFee(this.convertPrice(menuParsingList.get(2).trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계", "").trim()));
            }

            // 원산지 파싱
//            if (order.indexOf("합계") >= 0) {
//                ingredientOrigins = "";
//            }
//            if (ingredientOrigins != null) {
//                if (order.indexOf("합계") >= 0 || order.indexOf("=====") >= 0) {
//
//                } else if (order.indexOf("먹깨비") >= 0) { // 종료
//                    builder.ingredientOrigins(ingredientOrigins.trim());
//                    ingredientOrigins = null;
//                } else {
//                    ingredientOrigins += order;
//                }
//            }

            // 결제 여부
            if (order.indexOf("먹깨비") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("주문내역") >= 0) {
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

        builder.orderDate(this.convertOrderDate(CommonUtil.decodeMG(encodingList.get(2))));

        return builder.build();
    }

    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if (order.indexOf("주문내역") >= 0
                || order.indexOf("-----") >= 0
                || order.indexOf("=====") >= 0
                || order.indexOf("배달팁") >= 0) {
            // do nothing
        } else if (order.startsWith(" +") || (order.startsWith("  ") && !order.startsWith("        "))) {
            // 메뉴 옵션 파싱, 메뉴 수량 개행은 제외
            orderMenu.append(order); // 원본 메뉴

            List<String> menuParsingList = new ArrayList<>();
            for (String item : order.split("  ")) {
                if (StringUtils.hasText(item.trim())) {
                    menuParsingList.add(item.trim());
                }
            }

            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            if (menuParsingList.size() >= 2) {
                OptionDTO optionDTO = new OptionDTO();
                optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
                optionDTO.setMenu(menuParsingList.get(0).replace("+", "").replaceAll("^\\s+", ""));
                optionDTO.setQuantity(menuParsingList.get(1));
                if (menuParsingList.size() == 3) {
                    optionDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
                }
                menuDTO.getOptionList().add(optionDTO);
            }
            if (menuParsingList.size() == 1) { // 옵션 개행
                // 확인 안됨
                OptionDTO optionDTO = menuDTO.getOptionList().get(menuDTO.getOptionList().size() - 1);
                optionDTO.setMenu(optionDTO.getMenu() + menuParsingList.get(0));
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

        if (menuParsingList.size() == 1) { // 메뉴명이 긴 경우
            MenuDTO newMenuDTO = new MenuDTO();
            newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
            newMenuDTO.setOptionList(new ArrayList<>());
            newMenuDTO.setMenu(menuParsingList.get(0));
            menuList.add(newMenuDTO);             // 메뉴 리스트에 메뉴 추가
        }
        if (order.startsWith("        ") && menuParsingList.size() == 2) { // 수량, 가격이 개행
            menuDTO.setQuantity(menuParsingList.get(0));
            menuDTO.setPrice(this.convertPrice(menuParsingList.get(1)));
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
        return orderDate.replace("배달", "").replace("포장", "").replace("-", "").replace(":", "").replace(" ", "").trim();
    }

    private String convertOrderPhone(String orderPhone) {
        return orderPhone.replace("(안심번호)", "").replace("-", "");
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

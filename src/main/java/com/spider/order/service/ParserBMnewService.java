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
public class ParserBMnewService {
    private static final int INITIAL = 0;
    private static final int MENU = 10;       // 메뉴 (3)
    private static final int MENU_TITLE = 11; // 메뉴 제목 (1)
    private static final int MENU_PRICE = 12;   // 메뉴 수량, 가격 개행 (2)
    private static final int MENU_TITLE_ADD = 13;      // 메뉴 제목 개행 (1)
    private static final int OPTION = 20; // 옵션 (1)
    private static final int OPTION_ADD = 21; // 옵션 개행 (1)


    // 신배민 - 배달
    public ServerRequestDTO parseDel(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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
            if (order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
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

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(CommonUtil.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 주소 파싱
        builder.originalJibunAddress(CommonUtil.decode(encodingList.get(addressIndex + 1)).trim());
        builder.originalRoadAddress(CommonUtil.decode(encodingList.get(addressIndex + 2)).trim());

        // 연락처
        builder.orderPhone(this.convertOrderPhone(CommonUtil.decode(encodingList.get(phoneIndex + 1)).trim()));

        builder.orderCarryType("D"); // 배달
        return builder.build();
    }

    public ServerRequestDTO parseWrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int phoneIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();
        String shopRemark = null;

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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
            if (order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
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

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(CommonUtil.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 연락처
        builder.orderPhone(this.convertOrderPhone(CommonUtil.decode(encodingList.get(phoneIndex + 1)).trim()));

        builder.orderCarryType("P"); // 픽업
        return builder.build();
    }

    public ServerRequestDTO parseOne(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        int orderNumberIndex = -1;
        int addressIndex = -1;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            String order = CommonUtil.decode(encoded);

            log.info("decoded: {}", order);
            log.info("encoded: {}", encoded);

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
            if (order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0) {
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

        // 주문일자 파싱
        builder.orderDate(this.convertOrderDate(CommonUtil.decode(encodingList.get(orderNumberIndex + 1)).trim()));

        // 주소 파싱
        builder.originalJibunAddress(CommonUtil.decode(encodingList.get(addressIndex + 1)).trim());

        builder.orderCarryType("A"); // 자체배달
        return builder.build();
    }

    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if ((order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("금액") >= 0)
                || order.indexOf("-----") >= 0
                || order.indexOf("배달팁") >= 0) { // do nothing

        } else if (order.startsWith(" +") || (order.startsWith("  ") && !order.startsWith("     "))) {
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

    private void parseMenu(String order, ArrayList<MenuDTO> menuList) {
        List<String> menuParsingList = new ArrayList<>();
        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                menuParsingList.add(item.trim());
            }
        }

        if (menuParsingList.size() == 1) { // 메뉴명만 존재하는 경우
            MenuDTO newMenuDTO = new MenuDTO();
            newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
            newMenuDTO.setOptionList(new ArrayList<>());
            newMenuDTO.setMenu(menuParsingList.get(0));
            menuList.add(newMenuDTO); // 메뉴 리스트에 메뉴 추가
        }
        if (menuParsingList.size() == 2) {
            if (order.startsWith("     ")) { // 수량, 가격이 개행되는 경우,
                MenuDTO menuDTO = menuList.get(menuList.size() - 1);
                menuDTO.setQuantity(menuParsingList.get(0));
                menuDTO.setPrice(this.convertPrice(menuParsingList.get(1)));
            } else { // 메뉴명과 수량이 붙어서 오는 경우: 프리미엄 프루타 멜론(600g) 1        24,500
                int lastIndex = menuParsingList.get(0).lastIndexOf(" ");

                MenuDTO newMenuDTO = new MenuDTO();
                newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
                newMenuDTO.setOptionList(new ArrayList<>());
                newMenuDTO.setMenu(menuParsingList.get(0).substring(0, lastIndex));
                newMenuDTO.setQuantity(menuParsingList.get(0).substring(lastIndex + 1));
                newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(1)));
                menuList.add(newMenuDTO);
            }
        }
        if (menuParsingList.size() == 3) { // 메뉴 파싱
            MenuDTO newMenuDTO = new MenuDTO();
            newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
            newMenuDTO.setOptionList(new ArrayList<>());
            newMenuDTO.setMenu(menuParsingList.get(0));
            newMenuDTO.setQuantity(menuParsingList.get(1));
            newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
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

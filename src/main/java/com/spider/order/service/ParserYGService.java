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
public class ParserYGService {

    private int menuState = 0; // 1: 메뉴 2: 옵션

    public ServerRequestDTO parseYG_del(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String orderRemark = null;
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        String ingredientOrigins = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            log.info("encoded: {}", encoded);
            String order = CommonUtil.decodeYG(encoded);
            log.info("decoded: {}", order);

            // 주문 번호
            if (order.indexOf("주문 번호:") >= 0 && order.indexOf("주문 번호: #") < 0) {
                builder.orderNumber(order.replace("주문 번호:", "").trim());
            }

            // 주문 일자
            if (order.indexOf("주문 일자:") >= 0) {
                builder.orderDate(this.convertOrderDate(order.replace("주문 일자:", "").trim()));
            }

            // 주문 매장
            if (order.indexOf("주문 매장:") >= 0) {
                builder.orderStore(order.replace("주문 매장:", "").trim());
            }

            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처:", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("결제 방법:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("배달료:") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달료:", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계:") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계:", "").trim()));
            }

            // 배달 요청 사항
            if (order.indexOf("요청 사항:") >= 0) {
                orderRemark = "";
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("요청 사항:", "").trim();
                }
            }

            // 도로명 주소
            if (order.indexOf("(도로명) ") >= 0) {
                originalRoadAddress = "";
            }
            if (originalRoadAddress != null) {
                if (order.indexOf("(지번)") >= 0) {
                    // 도로명 주소 끝
                    builder.originalRoadAddress(originalRoadAddress);
                    originalRoadAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalRoadAddress += order.replace("(도로명) ", "");
                }
            }

            // 지번 주소
            if (order.indexOf("(지번) ") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("-----") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("(지번) ", "");
                }
            }

            // 원산지 파싱
            if (order.indexOf("원산지: ") >= 0) {
                ingredientOrigins = "";
            }
            if (ingredientOrigins != null) {
                if (encoded.indexOf("1D5601") >= 0) { // 종료
                    builder.ingredientOrigins(ingredientOrigins.trim());
                    ingredientOrigins = null;
                } else {
                    ingredientOrigins += order;
                }
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                      수량      가격") >= 0) {
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

    public ServerRequestDTO parseYG_wrap(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String orderDate = null;
        String orderRemark = null;
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        String ingredientOrigins = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            log.info("encoded: {}", encoded);
            String order = CommonUtil.decodeYG(encoded);
            log.info("decoded: {}", order);

            // 주문 번호
            if (order.indexOf("주문 번호:") >= 0 && order.indexOf("주문 번호: #") < 0) {
                builder.orderNumber(order.replace("주문 번호:", "").trim());
            }

            // 주문 일자
            if (order.indexOf("주문 일자:") >= 0) {
                orderDate = "";
            }
            if (orderDate != null) {
                if (order.indexOf("방문 시간") >= 0
                ) {
                    builder.orderDate(this.convertOrderDate(orderDate));
                    orderDate = null;
                } else {
                    orderDate += order.replace("주문 일자: ", "");
                }
            }

            // 주문 매장
            if (order.indexOf("주문 매장:") >= 0) {
                builder.orderStore(order.replace("주문 매장:", "").trim());
            }

            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처:", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("결제 방법:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("배달료:") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달료:", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계:") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계:", "").trim()));
            }

            // 배달 요청 사항
            if (order.indexOf("요청 사항:") >= 0) {
                orderRemark = "";
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("요청 사항:", "").trim();
                }
            }

            // 도로명 주소
            if (order.indexOf("(도로명) ") >= 0) {
                originalRoadAddress = "";
            }
            if (originalRoadAddress != null) {
                if (order.indexOf("(지번)") >= 0) {
                    // 도로명 주소 끝
                    builder.originalRoadAddress(originalRoadAddress);
                    originalRoadAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalRoadAddress += order.replace("(도로명) ", "");
                }
            }

            // 지번 주소
            if (order.indexOf("(지번) ") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("-----") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("(지번) ", "");
                }
            }

            // 원산지 파싱
            if (order.indexOf("원산지: ") >= 0) {
                ingredientOrigins = "";
            }
            if (ingredientOrigins != null) {
                if (encoded.indexOf("1D5601") >= 0) { // 종료
                    builder.ingredientOrigins(ingredientOrigins.trim());
                    ingredientOrigins = null;
                } else {
                    ingredientOrigins += order;
                }
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명") >= 0 && order.indexOf("수량") >= 0 && order.indexOf("가격") >= 0) {
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

        builder.orderCarryType("P"); // 포장
        return builder.build();
    }

    public ServerRequestDTO parseYE(List<String> encodingList) {
        ServerRequestDTO.ServerRequestDTOBuilder builder = ServerRequestDTO.builder();
        String orderRemark = null;
        String originalJibunAddress = null;
        String originalRoadAddress = null;
        String ingredientOrigins = null;
        ArrayList<MenuDTO> menuList = null;
        StringBuilder orderMenu = new StringBuilder();

        int index = 0;
        for (String encoded : encodingList) {
            log.info("encoded: {}", encoded);
            String order = CommonUtil.decodeYG(encoded);
            log.info("decoded: {}", order);

            // 주문 번호
            if (order.indexOf("주문 번호:") >= 0 && order.indexOf("주문 번호: #") < 0) {
                builder.orderNumber(order.replace("주문 번호:", "").trim());
            }

            // 주문 일자
            if (order.indexOf("주문 일자:") >= 0) {
                builder.orderDate(this.convertOrderDate(order.replace("주문 일자:", "").trim()));
            }

            // 주문 매장
            if (order.indexOf("주문 매장:") >= 0) {
                builder.orderStore(order.replace("주문 매장:", "").trim());
            }

            // 연락처
            if (order.indexOf("연락처:") >= 0) {
                builder.orderPhone(this.convertOrderPhone(order.replace("연락처:", "").trim()));
            }

            // 결제방식 파싱
            if (order.indexOf("결제 방법:") >= 0) {
                builder.orderPayKind(this.convertOrderPayKind(order));
            }

            // 배달팁 파싱
            if (order.indexOf("배달료:") >= 0) {
                builder.orderFee(this.convertPrice(order.replace("배달료:", "").trim()));
            }

            // 합계 파싱
            if (order.indexOf("합계:") >= 0) {
                builder.orderSum(this.convertPrice(order.replace("합계:", "").trim()));
            }

            // 배달 요청 사항
            if (order.indexOf("요청 사항:") >= 0) {
                orderRemark = "";
            }
            if (orderRemark != null) {
                if (order.indexOf("-----") >= 0) {
                    builder.orderRemark(orderRemark);
                    orderRemark = null;
                } else {
                    orderRemark += order.replace("요청 사항:", "").trim();
                }
            }

            // 도로명 주소
            if (order.indexOf("(도로명) ") >= 0) {
                originalRoadAddress = "";
            }
            if (originalRoadAddress != null) {
                if (order.indexOf("(지번)") >= 0) {
                    // 도로명 주소 끝
                    builder.originalRoadAddress(originalRoadAddress);
                    originalRoadAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalRoadAddress += order.replace("(도로명) ", "");
                }
            }

            // 지번 주소
            if (order.indexOf("(지번) ") >= 0) {
                originalJibunAddress = "";
            }
            if (originalJibunAddress != null) {
                if (order.indexOf("-----") >= 0) {
                    // 지번 주소 끝
                    builder.originalJibunAddress(originalJibunAddress);
                    originalJibunAddress = null;
                } else {
                    // 지번 주소 개행분 추가
                    originalJibunAddress += order.replace("(지번) ", "");
                }
            }

            // 원산지 파싱
            if (order.indexOf("원산지: ") >= 0) {
                ingredientOrigins = "";
            }
            if (ingredientOrigins != null) {
                if (encoded.indexOf("1D5601") >= 0) { // 종료
                    builder.ingredientOrigins(ingredientOrigins.trim());
                    ingredientOrigins = null;
                } else {
                    ingredientOrigins += order;
                }
            }

            // 메뉴 리스트 파싱
            if (order.indexOf("메뉴명                      수량      가격") >= 0) {
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

        builder.orderCarryType("A"); // 배달
        return builder.build();
    }


    private void parserMenu(String order, ServerRequestDTO.ServerRequestDTOBuilder builder, ArrayList<MenuDTO> menuList, StringBuilder orderMenu) {
        if (order.indexOf("메뉴명") >= 0
                || order.indexOf("-----") >= 0
                || order.indexOf("최소주문금액") >= 0
                || order.indexOf("배달료") >= 0) {
            // do nothing
        } else if (order.startsWith("- ")) {
            // 메뉴 옵션 파싱, 메뉴 수량 개행은 제외
            orderMenu.append(order); // 원본 메뉴

            MenuDTO menuDTO = menuList.get(menuList.size() - 1);
            this.parseOption(order, menuDTO);

            this.menuState = 2;
        } else if (order.indexOf("합계") >= 0) { // 메뉴 파싱 종료
            builder.orderMenuList(menuList);
            builder.orderMenu(orderMenu.toString());

            this.menuState = 0;
        } else { // 메뉴, 메뉴 개행 or 옵션 개행
            orderMenu.append(order); // 원본 메뉴

            List<String> menuParsingList = new ArrayList<>();
            for (String item : order.split("  ")) {
                if (StringUtils.hasText(item.trim())) {
                    menuParsingList.add(item.trim());
                }
            }

            if (menuParsingList.size() == 1) { // 메뉴개행, 옵션개행 구분이 안된다. 이전꺼를 보고 판단.
                MenuDTO menuDTO = menuList.get(menuList.size() - 1);

                if (this.menuState == 1) { // 메뉴 개행
                    menuDTO.setMenu(menuDTO.getMenu() + order.trim());

                } else if (this.menuState == 2) { // 옵션 개행
                    List<OptionDTO> optionList = menuDTO.getOptionList();
                    OptionDTO optionDTO = optionList.get(optionList.size() - 1);
                    optionDTO.setMenu(optionDTO.getMenu() + order.trim());
                }
            } else if (menuParsingList.size() == 3) { // 메뉴
                MenuDTO newMenuDTO = new MenuDTO();
                newMenuDTO.setNum(String.valueOf(menuList.size() + 1));
                newMenuDTO.setOptionList(new ArrayList<>());
                newMenuDTO.setMenu(menuParsingList.get(0));
                newMenuDTO.setQuantity(menuParsingList.get(1));
                newMenuDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
                // 메뉴 리스트에 메뉴 추가
                menuList.add(newMenuDTO);

                this.menuState = 1;
            }
        }
    }

    // 메뉴 파싱이 되면 새로 생성된 MenuDTO가 리턴
    private void parseOption(String order, MenuDTO menuDTO) {
        List<String> menuParsingList = new ArrayList<>();
        for (String item : order.split("  ")) {
            if (StringUtils.hasText(item.trim())) {
                menuParsingList.add(item.trim());
            }
        }

        if (menuParsingList.size() == 1) {  // 옵션명
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
            optionDTO.setMenu(menuParsingList.get(0).replace("- ", ""));
            menuDTO.getOptionList().add(optionDTO);
        }
        if (menuParsingList.size() == 2) { // 옵션명, 수량
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
            optionDTO.setMenu(menuParsingList.get(0).replace("- ", ""));
            optionDTO.setQuantity(menuParsingList.get(1).trim());
            menuDTO.getOptionList().add(optionDTO);
        }

        if (menuParsingList.size() == 3) { // 옵션명, 수량, 가격
            OptionDTO optionDTO = new OptionDTO();
            optionDTO.setNum(String.valueOf(menuDTO.getOptionList().size() + 1));
            optionDTO.setMenu(menuParsingList.get(0).replace("- ", ""));
            optionDTO.setQuantity(menuParsingList.get(1).trim());
            optionDTO.setPrice(this.convertPrice(menuParsingList.get(2)));
            menuDTO.getOptionList().add(optionDTO);
        }
    }

    private void parseOptionPrice(String order, OptionDTO optionDTO) {

        // 옵션(2,000원) 에서 price 파싱
/*        if (order.indexOf("(") >= 0 && order.indexOf(")") >= 0) {
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
        }*/

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
        // 요기요: 2022년 08월 24일(수) 오후04:38
        String yyyyMMdd = orderDate.substring(0, 12).replace("년 ", "").replace("월 ", "");
        String amPm = orderDate.substring(17, 19);
        String hh = orderDate.substring(19, 21);
        String mm = orderDate.substring(22);

        if (amPm.indexOf("오후") >= 0) {
            int intHH = Integer.parseInt(hh) + 12;
            hh = String.valueOf(intHH);
        }
        return yyyyMMdd + hh + mm;
    }

    private String convertOrderPhone(String orderPhone) {
        // 구배민 (안심번호)050-71252-9487 => 050712529487
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

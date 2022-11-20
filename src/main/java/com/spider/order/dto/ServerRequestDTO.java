package com.spider.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerRequestDTO {
    @JsonProperty("OrderSEQ")
    private String orderSeq;                // 주문접수번호

    @JsonProperty("ClientID")
    private String clientId;                // 가맹점코드

    @JsonProperty("ClientToken")
    private String clientToken;                // 가맹점토큰

    @JsonProperty("ServiceProvider")
    private String serviceProvider;            // 서비스사업자구분

    @JsonProperty("ClientName")
    private String clientName;                // 가맹점명


    @JsonProperty("OrderAppKind")
    private String orderAppKind;            // "BM",                  // 배달앱종류 – BM,YG

    @JsonProperty("OrderNumber")
    private String orderNumber;            // "B0HD00ENEG",        // 주문번호 –

    @JsonProperty("OrderCarryType")
    private String orderCarryType;            // “D”              // 전달방식 : D, P

    @JsonProperty("OriginalJibunAddress")
    private String originalJibunAddress;    // "서울특별시 노원구 공릉동 111-3 화랑타운아파트 710동104호 ",  // 원본 지번주소, (요기요 작업 시, 해당 주소 입력)

    @JsonProperty("OriginalRoadAddress")
    private String originalRoadAddress;    // "서울특별시 노원구 화랑로51길 17 화랑타운아파트 710동104호 ", // 원본 도로명 주소

    @JsonProperty("OrderJibunAddress")
    private String orderJibunAddress;        // "서울 노원구 공릉동 111-3",                // 파싱된 지번주소 –

    @JsonProperty("OrderRoadAddress")
    private String orderRoadAddress;        // "서울특별시 노원구 화랑로51길 17 ",   // 파싱된 도로명주소 –

    @JsonProperty("OrderAddressDetail")
    private String orderAddressDetail;        // "화랑타운아파트 710동104호 “             // 지번 상세주소 -

    @JsonProperty("OrderDate")
    private String orderDate;                // "201909181831",        // 주문일시 –

    @JsonProperty("OrderPayKind")
    private String orderPayKind;            // "카드",                // 결제종류 - (카드, 현금, 사전)결제

    @JsonProperty("OrderPhone")
    private String orderPhone;                // "01099999999",        // 연락처 -

    @JsonProperty("OrderStore")
    private String orderStore;                // "부산국밥",            // 주문매장 -

    @JsonProperty("OrderDiscount")
    private String orderDiscount;            // "1000",                    // 할인금액 -

    @JsonProperty("OrderFee")
    private String orderFee;                // "1000",                    // 배달료 -

    @JsonProperty("DepositAmt")
    private String depositAmt;                // "18500",                   // 합계 -

    @JsonProperty("OrderSum")
    private String orderSum;                // "18500",                   // 합계 -

    @JsonProperty("OrderRemark")
    private String orderRemark;            // "",                     // 요청사항 –

    @JsonProperty("ShopRemark")
    private String shopRemark;            // "",                     // 요청사항 –

    @JsonProperty("OrderMenu")
    private String orderMenu;            // "",                     // 요청사항 –

    @JsonProperty("IngredientOrigins")
    private String ingredientOrigins;

    @JsonProperty("OrderMenuList")
    private ArrayList<MenuDTO> orderMenuList;                // "",                     // 원본메뉴
}

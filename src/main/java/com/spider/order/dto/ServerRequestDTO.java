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
    @Builder.Default
    @JsonProperty("OrderSEQ")
    private String orderSeq = "";                // 주문접수번호

    @Builder.Default
    @JsonProperty("ClientID")
    private String clientId = "";                // 가맹점코드

    @Builder.Default
    @JsonProperty("ClientToken")
    private String clientToken = "";                // 가맹점토큰

    @Builder.Default
    @JsonProperty("ServiceProvider")
    private String serviceProvider = "";            // 서비스사업자구분

    @Builder.Default
    @JsonProperty("ClientName")
    private String clientName = "";                // 가맹점명


    @Builder.Default
    @JsonProperty("OrderAppKind")
    private String orderAppKind = "";            // "BM",                  // 배달앱종류 – BM,YG

    @Builder.Default
    @JsonProperty("OrderNumber")
    private String orderNumber = "";            // "B0HD00ENEG",        // 주문번호 –

    @Builder.Default
    @JsonProperty("OrderCarryType")
    private String orderCarryType = "";            // “D”              // 전달방식 : D, P

    @Builder.Default
    @JsonProperty("OriginalJibunAddress")
    private String originalJibunAddress = "";    // "서울특별시 노원구 공릉동 111-3 화랑타운아파트 710동104호 ",  // 원본 지번주소, (요기요 작업 시, 해당 주소 입력)

    @Builder.Default
    @JsonProperty("OriginalRoadAddress")
    private String originalRoadAddress = "";    // "서울특별시 노원구 화랑로51길 17 화랑타운아파트 710동104호 ", // 원본 도로명 주소

    @Builder.Default
    @JsonProperty("OrderJibunAddress")
    private String orderJibunAddress = "";        // "서울 노원구 공릉동 111-3",                // 파싱된 지번주소 –

    @Builder.Default
    @JsonProperty("OrderRoadAddress")
    private String orderRoadAddress = "";        // "서울특별시 노원구 화랑로51길 17 ",   // 파싱된 도로명주소 –

    @Builder.Default
    @JsonProperty("OrderAddressDetail")
    private String orderAddressDetail = "";        // "화랑타운아파트 710동104호 “             // 지번 상세주소 -

    @Builder.Default
    @JsonProperty("OrderDate")
    private String orderDate = "";                // "201909181831",        // 주문일시 –

    @Builder.Default
    @JsonProperty("OrderPayKind")
    private String orderPayKind = "";            // "카드",                // 결제종류 - (카드, 현금, 사전)결제

    @Builder.Default
    @JsonProperty("OrderPhone")
    private String orderPhone = "";                // "01099999999",        // 연락처 -

    @Builder.Default
    @JsonProperty("OrderStore")
    private String orderStore = "";                // "부산국밥",            // 주문매장 -

    @Builder.Default
    @JsonProperty("OrderDiscount")
    private String orderDiscount = "";            // "1000",                    // 할인금액 -

    @Builder.Default
    @JsonProperty("OrderFee")
    private String orderFee = "";                // "1000",                    // 배달료 -

    @Builder.Default
    @JsonProperty("DepositAmt")
    private String depositAmt = "";                // "18500",                   // 합계 -

    @Builder.Default
    @JsonProperty("OrderSum")
    private String orderSum = "";                // "18500",                   // 합계 -

    @Builder.Default
    @JsonProperty("OrderRemark")
    private String orderRemark = "";            // "",                     // 요청사항 –

    @Builder.Default
    @JsonProperty("ShopRemark")
    private String shopRemark = "";            // "",                     // 요청사항 –

    @Builder.Default
    @JsonProperty("OrderMenu")
    private String orderMenu = "";            // "",                     // 요청사항 –

    @Builder.Default
    @JsonProperty("IngredientOrigins")
    private String ingredientOrigins = "";

    @JsonProperty("OrderMenuList")
    private ArrayList<MenuDTO> orderMenuList;                // "",                     // 원본메뉴

    @Builder.Default
    @JsonProperty("HexaData")
    private String hexaData = "";
}

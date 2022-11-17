package com.spider.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerResponseDTO {
    @JsonProperty("ResultCode")
    private String resultCode;                // 결과코드

    @JsonProperty("ResultMessage")
    private String resultMessage;            // 결과메세지

    @JsonProperty("OrderSEQ")
    private String orderSeq;                // 주문접수번호

    @JsonProperty("OrderNumber")
    private String orderNumber;                // 주문업체주문번호
}

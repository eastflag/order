package com.spider.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentRequestDTO {
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

    @JsonProperty("RawData")
    private String rawData;                    // RawData
}

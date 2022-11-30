package com.spider.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    @Builder.Default
    @JsonProperty("Num")
    private String num = "";

    @Builder.Default
    @JsonProperty("Menu")
    private String menu = "";

    @Builder.Default
    @JsonProperty("Quantity")
    private String quantity = "";

    @Builder.Default
    @JsonProperty("Price")
    private String price = "";
}

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
    @JsonProperty("Num")
    private String num;

    @JsonProperty("Menu")
    private String menu;

    @JsonProperty("Quantity")
    private String quantity;

    @JsonProperty("Price")
    private String price;
}

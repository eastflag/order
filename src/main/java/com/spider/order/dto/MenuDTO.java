package com.spider.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class MenuDTO {
    @JsonProperty("Num")
    private String num;

    @JsonProperty("Menu")
    private String menu;

    @JsonProperty("Quantity")
    private String quantity;

    @JsonProperty("Price")
    private String price;

    @JsonProperty("OptionList")
    private ArrayList<OptionDTO> optionList;

    @JsonIgnore
    private String tempTitle = "";
}

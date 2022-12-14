package com.spider.order.controller;

import com.spider.order.client.OrderFeignClient;
import com.spider.order.dto.AgentRequestDTO;
import com.spider.order.dto.AgentResponseDTO;
import com.spider.order.dto.ServerRequestDTO;
import com.spider.order.dto.ServerResponseDTO;
import com.spider.order.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final ParserBMoldService parserBMoldService;
    private final ParserBMnewService parserBMnewService;
    private final ParserBRService parserBMService;
    private final ParserYGService parserYGService;
    private final ParserSHService parserSHService;
    private final ParserDIService parserDIService;
    private final ParserCPService parserCPService;
    private final ParserTGService parserTGService;
    private final ParserMGService parserMGService;

    private final OrderFeignClient orderFeignClient;

    @PostMapping("/order")
    public ServerRequestDTO order(@RequestBody AgentRequestDTO agentRequestDTO) {
        String hexadecimal = agentRequestDTO.getRawData();

        String orderAppKind = this.checkOrderAppKind(hexadecimal);
        log.info("orderAppKind: {}", orderAppKind);

        List<String> resultList = this.getSplit(hexadecimal, orderAppKind.substring(0, 2));

        ServerRequestDTO serverRequestDTO = this.parse(resultList, orderAppKind);
        if (serverRequestDTO != null) {
            serverRequestDTO.setOrderSeq(agentRequestDTO.getOrderSeq());
            serverRequestDTO.setClientId(agentRequestDTO.getClientId());
            serverRequestDTO.setClientToken(agentRequestDTO.getClientToken());
            serverRequestDTO.setServiceProvider(agentRequestDTO.getServiceProvider());
            serverRequestDTO.setClientName(agentRequestDTO.getClientName());
        }

        return serverRequestDTO;
    }

    @PostMapping("/neworder")
    public AgentResponseDTO neworder(@RequestBody AgentRequestDTO agentRequestDTO) {
        AgentResponseDTO agentResponseDTO = null;

        try {
            String hexadecimal = agentRequestDTO.getRawData();

            String orderAppKind = this.checkOrderAppKind(hexadecimal);
            log.info("orderAppKind: {}", orderAppKind);

            List<String> resultList = this.getSplit(hexadecimal, orderAppKind.substring(0, 2));

            ServerRequestDTO serverRequestDTO = this.parse(resultList, orderAppKind);
            if (serverRequestDTO != null) {
                serverRequestDTO.setOrderSeq(agentRequestDTO.getOrderSeq());
                serverRequestDTO.setClientId(agentRequestDTO.getClientId());
                serverRequestDTO.setClientToken(agentRequestDTO.getClientToken());
                serverRequestDTO.setServiceProvider(agentRequestDTO.getServiceProvider());
                serverRequestDTO.setClientName(agentRequestDTO.getClientName());
                serverRequestDTO.setHexaData(hexadecimal);
            }

            ServerResponseDTO serverResponseDTO = orderFeignClient.postNewOrder(serverRequestDTO);

            if (serverResponseDTO.getResultCode().equals("0000")) {
                agentResponseDTO = AgentResponseDTO.builder()
                        .resultCode("0000")
                        .resultMessage("??????")
                        .orderSeq(agentRequestDTO.getOrderSeq())
                        .orderNumber(serverRequestDTO.getOrderNumber())
                        .build();
            } else {
                agentResponseDTO = AgentResponseDTO.builder()
                        .resultCode(serverResponseDTO.getResultCode())
                        .resultMessage(serverResponseDTO.getResultMessage())
                        .orderSeq(serverResponseDTO.getOrderSeq())
                        .orderNumber(serverResponseDTO.getOrderNumber())
                        .build();

            }
        } catch (Exception e) {
            log.error(e.getMessage());

            agentResponseDTO = AgentResponseDTO.builder()
                    .resultCode("0001")
                    .resultMessage(e.getMessage())
                    .orderSeq(agentRequestDTO.getOrderSeq())
                    .build();
        }

        return agentResponseDTO;
    }

    private String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;

        if (hexadecimal.indexOf("BFE400B1E200BFE4") >= 0) { // "??? ??? ???"
            if (hexadecimal.indexOf("BFE400B1E200BFE420C0CD00BDBA00C7C100B7B900BDBA") >= 0) { // "??? ??? ??? ??? ??? ??? ??? ???"
                orderAppKind = "YE";
            } else if (hexadecimal.indexOf("BFE400B1E200BFE420C6F700C0E5") >= 0) { // "??? ??? ??? ??? ???"
                orderAppKind = "YG_wrap";
            } else {
                orderAppKind = "YG_del";
            }
        } else if (hexadecimal.indexOf("B6AFB0DCBFE4") >= 0) { // "?????????"
            orderAppKind = "SH";
        } else if (hexadecimal.indexOf("B5CE2020C0D52020C6C02020B9E82020B4DE") >= 0) { // ???  ???  ???  ???  ???
            orderAppKind = "DI";
        } else if (hexadecimal.indexOf("636F7570616E672065617473") >= 0) { // coupang eats
            orderAppKind = "CP";
        } else if (hexadecimal.indexOf("B9E8B4DEC6AFB1DE") >= 0) { // ????????????
            orderAppKind = "TG";
        } else if (hexadecimal.indexOf("B8D4B1FABAF1") >= 0) { // ?????????
            orderAppKind = "MG";
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A5430") >= 0) { // C1D6B9AEB9F8C8A33A5430, ??????????????????
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // ?????? ????????????, ??????
                orderAppKind = "BR_del";
            } else { // ?????? ????????????, ?????????/??????
                orderAppKind = "BR_wrap";
            }
        } else if (hexadecimal.indexOf("B9E8B9CE3120C1D6B9AEC0FCC7A5") >= 0) { // ??????1 ????????????, ?????????-?????????
            orderAppKind = "BM_one_new";
        } else if (hexadecimal.indexOf("B9E8B9CE31C1D6B9AE") >= 0) { // ??????1??????, ?????????-?????????
            orderAppKind = "BM_one_old";
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A4231") >= 0) { // ????????????:B1, ?????????
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // ?????? ????????????, ?????????/??????
                orderAppKind = "BM_new_del";
            } else { // ?????? ????????????, ?????????/??????
                orderAppKind = "BM_new_wrap";
            }
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A3203A204231") >= 0) { // ???????????? : B1, ?????????
            if (hexadecimal.indexOf("C6F7C0E520C1A2BCF6B9F8C8A3") >= 0) { // ?????? ????????????, ?????????/??????
                orderAppKind = "BM_old_wrap";
            } else {                                                      // ?????????/??????
                orderAppKind = "BM_old_del";
            }
        }

        return orderAppKind;
    }

    private List<String> getSplit(String hexadecimal, String orderAppKind) {
        String splitChar = "";
        switch (orderAppKind) {
            case "YG":
            case "YE":
            case "TG":
            case "CP":
            case "MG":
                splitChar = "0D0A";
                break;
            case "SH":
            case "DI":
                splitChar = "0A";
                break;
            default:
                splitChar = "0A0D";

        }
        List<String> encodedList;

        if (orderAppKind.equals("SH") || orderAppKind.equals("DI")) {
            // SH??? ?????? 10A1 ?????? ?????????????????? ???????????? ??????.
            encodedList = new ArrayList<>();
            StringBuilder stingBuilder = new StringBuilder();
            for (int i = 0; i < hexadecimal.length(); i += 2) {
                // using left shift operator on every character
                String str = hexadecimal.substring(i, i + 2);
                if (str.equals("0A")) {
                    encodedList.add(stingBuilder.toString());
                    stingBuilder = new StringBuilder();
                } else {
                    stingBuilder.append(str);
                }
            }
            encodedList.add(stingBuilder.toString());
        } else {
            encodedList = Arrays.asList(hexadecimal.split(splitChar));
        }

        return encodedList;
    }

    private ServerRequestDTO parse(List<String> encodingList, String orderAppKind) {
        ServerRequestDTO serverRequestDTO = null;

        switch (orderAppKind) {
            case "BR_del":
                serverRequestDTO = parserBMService.parseBR_del(encodingList);
                break;
            case "BR_wrap":
                serverRequestDTO = parserBMService.parseBR_wrap(encodingList);
                break;
            case "BM_one_new":
                serverRequestDTO = parserBMnewService.parseOne(encodingList);
                break;
            case "BM_one_old":
                serverRequestDTO = parserBMoldService.parseOne(encodingList);
                break;
            case "BM_new_del":
                serverRequestDTO = parserBMnewService.parseDel(encodingList);
                break;
            case "BM_new_wrap":
                serverRequestDTO = parserBMnewService.parseWrap(encodingList);
                break;
            case "BM_old_del":
                serverRequestDTO = parserBMoldService.parseDel(encodingList);
                break;
            case "BM_old_wrap":
                serverRequestDTO = parserBMoldService.parseWrap(encodingList);
                break;
            case "YG_del":
                serverRequestDTO = parserYGService.parseYG_del(encodingList);
                break;
            case "YG_wrap":
                serverRequestDTO = parserYGService.parseYG_wrap(encodingList);
                break;
            case "YE":
                serverRequestDTO = parserYGService.parseYE(encodingList);
                break;
            case "SH":
                serverRequestDTO = parserSHService.parseSH(encodingList);
                break;
            case "DI":
                serverRequestDTO = parserDIService.parse(encodingList);
                break;
            case "CP":
                serverRequestDTO = parserCPService.parse(encodingList);
                break;
            case "TG":
                serverRequestDTO = parserTGService.parse(encodingList);
                break;
            case "MG":
                serverRequestDTO = parserMGService.parse(encodingList);
                break;
        }

        if (serverRequestDTO != null) {
            serverRequestDTO.setOrderAppKind(orderAppKind.substring(0, 2));
        }

        return serverRequestDTO;
    }
}

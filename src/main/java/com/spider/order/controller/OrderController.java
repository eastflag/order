package com.spider.order.controller;

import com.spider.order.dto.AgentRequestDTO;
import com.spider.order.dto.ServerRequestDTO;
import com.spider.order.service.ParserBMService;
import com.spider.order.service.ParserYGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final ParserBMService parserBMService;
    private final ParserYGService parserYGService;

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

    private String checkOrderAppKind(String hexadecimal) {
        String orderAppKind = null;

        if (hexadecimal.indexOf("BFE400B1E200BFE4") >= 0) { // "요 기 요"
            if (hexadecimal.indexOf("BFE400B1E200BFE420C0CD00BDBA00C7C100B7B900BDBA") >= 0) { // "요 기 요 익 스 프 레 스"
                orderAppKind = "YG_express";
            } else if (hexadecimal.indexOf("BFE400B1E200BFE420C6F700C0E5") >= 0) { // "요 기 요 포 장"
                orderAppKind = "YG_wrap";
            } else {
                orderAppKind = "YG_del";
            }
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A5430") >= 0) { // C1D6B9AEB9F8C8A33A5430, 배민라이더스
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 배달 주문전표, 배달
                orderAppKind = "BR_del";
            } else { // 포장 주문전표, 신배민/포장
                orderAppKind = "BR_wrap";
            }
        } else if (hexadecimal.indexOf("B9E8B9CE3120C1D6B9AEC0FCC7A5") >= 0) { // 배민1 주문전표, 신배민-배민원
            orderAppKind = "BM_one_new";
        } else if (hexadecimal.indexOf("B9E8B9CE31C1D6B9AE") >= 0) { // 배민1주문, 구배민-배민원
            orderAppKind = "BM_one_old";
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A33A4231") >= 0) { // 주문번호:B1, 신배민
            if (hexadecimal.indexOf("B9E8B4DE20C1D6B9AEC0FCC7A5") >= 0) { // 배달 주문전표, 신배민/배달
                orderAppKind = "BM_new_del";
            } else { // 포장 주문전표, 신배민/포장
                orderAppKind = "BM_new_wrap";
            }
        } else if (hexadecimal.indexOf("C1D6B9AEB9F8C8A3203A204231") >= 0) { // 주문번호 : B1, 구배민
            if (hexadecimal.indexOf("C6F7C0E520C1A2BCF6B9F8C8A3") >= 0) { // 포장 접수번호, 구배민/포장
                orderAppKind = "BM_old_wrap";
            } else {                                                      // 구배민/배달
                orderAppKind = "BM_old_del";
            }
        }

        return orderAppKind;
    }

    private List<String> getSplit(String hexadecimal, String orderAppKind) {
        String splitChar = "";
        switch (orderAppKind) {
            case "YG":
                splitChar = "0D0A";
                break;
            default:
                splitChar = "0A0D";

        }
        List<String> encodedList = Arrays.asList(hexadecimal.split(splitChar));

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
                serverRequestDTO = parserBMService.parseBM_one_new(encodingList);
                break;
            case "BM_one_old":
                serverRequestDTO = parserBMService.parseBM_one_old(encodingList);
                break;
            case "BM_new_del":
                serverRequestDTO = parserBMService.parseBM_new_del(encodingList);
                break;
            case "BM_new_wrap":
                serverRequestDTO = parserBMService.parseBM_new_wrap(encodingList);
                break;
            case "BM_old_del":
                serverRequestDTO = parserBMService.parseBM_old_del(encodingList);
                break;
            case "BM_old_wrap":
                serverRequestDTO = parserBMService.parseBM_old_wrap(encodingList);
                break;
            case "YG_del":
                serverRequestDTO = parserYGService.parseYG_del(encodingList);
                break;
        }

        if (serverRequestDTO != null) {
            serverRequestDTO.setOrderAppKind(orderAppKind.substring(0, 2));
        }

        return serverRequestDTO;
    }
}

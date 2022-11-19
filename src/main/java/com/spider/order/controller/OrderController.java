package com.spider.order.controller;

import com.spider.order.dto.AgentRequestDTO;
import com.spider.order.dto.ServerRequestDTO;
import com.spider.order.service.ParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {
    private final ParserService parserService;

    @PostMapping("/order")
    public ServerRequestDTO order(@RequestBody AgentRequestDTO agentRequestDTO) {
        String hexadecimal = agentRequestDTO.getRawData();

        String orderAppKind = parserService.checkOrderAppKind(hexadecimal);
        List<String> resultList = parserService.getSplit(hexadecimal);

        ServerRequestDTO serverRequestDTO = parserService.parse(resultList, orderAppKind);

        return serverRequestDTO;
    }
}

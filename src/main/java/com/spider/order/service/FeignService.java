package com.spider.order.service;

import com.spider.order.client.OrderFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeignService {
    private final OrderFeignClient orderFeignClient;
}

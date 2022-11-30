package com.spider.order.client;

import com.spider.order.dto.ServerRequestDTO;
import com.spider.order.dto.ServerResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "orderFeignClient", url = "${agent.server.url", configuration = OrderFeignClientConfig.class)
public interface OrderFeignClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/neworder")
    ServerResponseDTO postNewOrder(@RequestBody ServerRequestDTO serverRequestDTO);
}

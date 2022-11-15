package com.spider.order.client;

import feign.Response;
import feign.codec.ErrorDecoder;

public class OrderFeignError implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        return null;
    }
}

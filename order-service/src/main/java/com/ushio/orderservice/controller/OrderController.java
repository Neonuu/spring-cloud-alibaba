package com.ushio.orderservice.controller;

import com.ushio.commonmodule.entity.Order;
import com.ushio.orderservice.feignClient.PointServiceFeignClient;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/order")
@RefreshScope
public class OrderController {
    @Value("${config.info}")
    private String configInfo;

    @Autowired
    private PointServiceFeignClient pointServiceFeignClient;

    @GetMapping(value = "/test")
    public String test (){
        return "this is order-service";
    }

    @GetMapping(value = "/test/getConfigInfo")
    public String getConfigInfo (){
        return configInfo;
    }

    @PostMapping(value = "/add")
    public String addOrder(){
        Order order = new Order();
        order.setId("123");
        order.setProductionName("水杯");
        String res = pointServiceFeignClient.addPoint(order);
        return res;
    }

    @PostMapping(value = "/add2")
    public String addOrder2(){
        String res = pointServiceFeignClient.addPoint2("水杯2");
        return res;
    }

}

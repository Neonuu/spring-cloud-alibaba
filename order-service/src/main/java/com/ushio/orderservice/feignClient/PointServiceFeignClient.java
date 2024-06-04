package com.ushio.orderservice.feignClient;

import com.ushio.commonmodule.entity.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "point-service")
public interface PointServiceFeignClient {

    @PostMapping(value = "/point/add")
    String addPoint(@RequestBody Order order);

    @PostMapping(value = "/point/add2")
    String addPoint2(@RequestParam("productionName") String productionName);
}

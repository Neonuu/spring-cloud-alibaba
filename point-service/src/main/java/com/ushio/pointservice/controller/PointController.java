package com.ushio.pointservice.controller;

import com.ushio.commonmodule.entity.Order;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/point")
public class PointController {

    @GetMapping(value = "/test")
    public String test (){
        return "this is point-service";
    }

    @PostMapping(value = "/add")
    public String addPoint(@RequestBody Order order){
        return "add point success!商品名称222："+order.getProductionName();
    }

    @PostMapping(value = "/add2")
    public String addPoint2(@RequestParam("productionName") String productionName){
        return "add point success!商品名称："+productionName;
    }

}

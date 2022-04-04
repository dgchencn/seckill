package com.wyu.seckill.controller;

import com.wyu.seckill.common.ResponseModel;
import com.wyu.seckill.entity.Item;
import com.wyu.seckill.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/item")
@CrossOrigin(origins = "${seckill.web.path}", allowedHeaders = "*", allowCredentials = "true")
public class ItemController {
    @Autowired
    private ItemService itemService;

    //获取全部商品列表
//    @RequestMapping(path = "/list", method = RequestMethod.GET)
    @GetMapping("/list")
    public ResponseModel getItemList(){
        List<Item> items = itemService.findItemsOnPromotion();
        return new ResponseModel(items);
    }
    //获取商品详情
//    @RequestMapping(path = "/detail/{id}", method = RequestMethod.GET)
    @GetMapping("/detail/{id}")
    public ResponseModel getItemDetail(@PathVariable("id") int id){
        //        Item item = itemService.findItemById(id);
        Item item = itemService.findItemInCache(id);
        return new ResponseModel(item);
    }
}

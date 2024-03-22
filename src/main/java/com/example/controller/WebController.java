package com.example.controller;

import com.example.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 基础前端接口
 */
@RestController
public class WebController {

    @GetMapping("/")
    public Result hello() {
        return Result.success("访问成功");
    }

}

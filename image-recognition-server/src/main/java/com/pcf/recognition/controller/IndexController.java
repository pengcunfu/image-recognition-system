package com.pcf.recognition.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页重定向控制器
 */
@Controller
public class IndexController {

    /**
     * 根路径重定向到Swagger UI
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/swagger-ui/index.html";
    }
}

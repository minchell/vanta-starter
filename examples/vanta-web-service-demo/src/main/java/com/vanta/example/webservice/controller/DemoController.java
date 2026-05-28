package com.vanta.example.webservice.controller;

import com.vanta.example.webservice.model.DemoCreateReq;
import com.vanta.example.webservice.service.DemoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    @GetMapping("/success")
    public Map<String, Object> success() {
        return demoService.success();
    }

    @PostMapping("/validation")
    public DemoCreateReq validation(@Valid @RequestBody DemoCreateReq request) {
        return request;
    }

    @GetMapping("/error")
    public Map<String, Object> error() {
        throw new IllegalStateException("demo error");
    }

    @GetMapping("/time")
    public Map<String, Object> time() {
        return Map.of("time", LocalDateTime.of(2026, 5, 28, 10, 0, 0));
    }
}

package com.vanta.example.webservice.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DemoService {

    public Map<String, Object> success() {
        return Map.of(
                "message", "vanta web service demo",
                "ready", true
        );
    }
}

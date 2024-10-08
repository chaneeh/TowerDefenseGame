package com.example.legacy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class WebSocketController {

    @GetMapping("/api/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "ok!");
    }
}

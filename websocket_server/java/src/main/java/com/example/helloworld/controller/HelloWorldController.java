package com.example.helloworld.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        System.out.println("Hello World!");
        return "Hello World!";
    }

    @PostConstruct
    public void init() {
        System.out.println("HelloWorldController Bean is created!");
    }
}

package com.example.helloworld.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloWorldApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void helloWorldEndpointReturnsHelloWorld() {
        // "/hello" 엔드포인트에 GET 요청을 보내고 결과를 받아옵니다.
        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);
        
        // 응답이 "Hello World!"인지 확인합니다.
        assertThat(response.getBody()).isEqualTo("Hello World!");
    }
}

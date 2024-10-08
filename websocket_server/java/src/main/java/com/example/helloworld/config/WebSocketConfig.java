package com.example.helloworld.config;

import com.example.helloworld.handler.MyWebSocketHandler;
import com.example.helloworld.interceptor.ClientIdInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;



@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MyWebSocketHandler myWebSocketHandler;

    public WebSocketConfig(MyWebSocketHandler myWebSocketHandler) {
        this.myWebSocketHandler = myWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myWebSocketHandler, "/ws/{client_id}") // WebSocket 엔드포인트 등록
                .setAllowedOrigins("*") // CORS 설정
                .addInterceptors(new ClientIdInterceptor()); // 인터셉터 등록
    }
}

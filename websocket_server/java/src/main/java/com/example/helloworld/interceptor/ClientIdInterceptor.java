package com.example.helloworld.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class ClientIdInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // URI에서 client_id 추출
        String uri = request.getURI().toString();
        String clientId = uri.substring(uri.lastIndexOf("/") + 1);
        
        // 세션에 client_id 저장
        attributes.put("client_id", clientId);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                               WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 이후 로직 (필요시 구현)
    }
}

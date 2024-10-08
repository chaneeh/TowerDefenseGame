package com.example.helloworld.handler;

import com.example.helloworld.model.TowerDefenseMessage;
import com.example.helloworld.model.TowerDefenseResponse;
import com.example.helloworld.service.GameLogService;
import com.example.helloworld.service.MLService;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Validated
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;
    private final GameLogService gameLogService;
    private final MLService mlService;

    @Autowired
    public MyWebSocketHandler(ObjectMapper objectMapper, GameLogService gameLogService, MLService mlService) {
        this.objectMapper = objectMapper;
        this.gameLogService = gameLogService;
        this.mlService = mlService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 성공적으로 맺어진 후 호출되는 콜백 메서드
        String clientId = (String) session.getAttributes().get("client_id");
        System.out.println("Connected client with ID: " + clientId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String clientId = (String) session.getAttributes().get("client_id");
        String payload = message.getPayload();
        System.out.println("Received message from client " + clientId);

        try {
            // JSON 문자열을 TowerDefenseMessage 객체로 변환
            TowerDefenseMessage towerDefenseMessage = objectMapper.readValue(payload, TowerDefenseMessage.class);
            towerDefenseMessage.validate();
            System.out.println("Received message: " + towerDefenseMessage);
            
            if (towerDefenseMessage.isAgentAI()) {
                switch (towerDefenseMessage.getType_1()) {
                    case "gold":
                        handleGoldMsg(towerDefenseMessage, session);
                        break;
                    case "tower":
                        if (towerDefenseMessage.getStrategy().equals("ml")) {
                            handleTowerMLMsg(towerDefenseMessage, session);
                        }
                        break;
                    case "wave":
                        handleWaveMsg(towerDefenseMessage, session);
                        break;
                    default:
                        break;
                }
                System.out.println("AI agent: " + towerDefenseMessage.getAgent());
            } else {
                System.out.println("Human agent: " + towerDefenseMessage.getAgent());
            }

            // session.sendMessage(new TextMessage("Hello, " + payload + "!"));

        } catch (ConstraintViolationException e) {
            session.sendMessage(new TextMessage("Validation error: " + e.getMessage()));
            System.out.println("Validation error: " + e.getMessage());
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Invalid message format: " + e.getMessage()));
            System.out.println("Invalid message format: " + e.getMessage());
        }

    }

    private void handleGoldMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        TowerDefenseResponse response = new TowerDefenseResponse(
            dataModel.getAgent(),
            (Integer.parseInt(dataModel.getData()) >= 0) ? "buy" : "stay"
        );
        response.validate();
    
        String responseJson = objectMapper.writeValueAsString(response);
        session.sendMessage(new TextMessage(responseJson));
    
        System.out.println("Send to Client: " + responseJson);
    }

    private void handleTowerMLMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        System.out.println("ML strategy: " + dataModel.getStrategy());
        Map<String, Object> ruleBasedResponse = checkRuleBasedUpgradeCondition(dataModel.getAgent(), dataModel.getCurrentGold(), dataModel.getCurrent_towerstatus());
    
        if (ruleBasedResponse != null) {
            TowerDefenseResponse response = new TowerDefenseResponse(
                    (String) ruleBasedResponse.get("agent"),
                    (String) ruleBasedResponse.get("action"),
                    (String) ruleBasedResponse.getOrDefault("level", null),
                    (String) ruleBasedResponse.getOrDefault("type", null)
            );
            String responseJson = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(responseJson));
            return;
        }

        mlService.mlRequestEndpoint(dataModel)
            .thenAccept(actionItem -> {
                try {
                    TowerDefenseResponse response = new TowerDefenseResponse(
                        (String) actionItem.get("agent"),
                        (String) actionItem.get("action"),
                        (String) actionItem.getOrDefault("level", null),
                        (String) actionItem.getOrDefault("type", null)
                    );
                    String responseJson = objectMapper.writeValueAsString(response);
                    session.sendMessage(new TextMessage(responseJson));
                    System.out.println("Send to Client: " + responseJson);

                    gameLogService.saveGameActionLogJava(
                        dataModel.getClient_id(), 
                        dataModel.getGame_start_timestamp(), 
                        dataModel.getClient_timestamp(), 
                        (List<Integer>) actionItem.get("state"), 
                        (Integer) actionItem.get("action_idx")
                    );

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            });
            
        return;
    
    }

    private void handleWaveMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        String[] waveTimeList = dataModel.getData().split(" ");
        List<Integer> waveReward = new ArrayList<>();

        for (String waveTimeStr : waveTimeList) {
            try {
                Integer currentWaveReward = (int) Math.round(40.0 - Double.parseDouble(waveTimeStr));
                waveReward.add(currentWaveReward);
            } catch (NumberFormatException e) {
                System.out.println("Invalid wave time: " + waveTimeStr);
                waveReward.add(0);
            }
        }


        gameLogService.saveGameActionLogRewardJava(
            dataModel.getClient_id(), 
            dataModel.getGame_start_timestamp(), 
            dataModel.getClient_timestamp(), 
            waveReward
        );
    }

    private Map<String, Object> checkRuleBasedUpgradeCondition(String agent, int gold, Map<String, Map<String, String>> towerStatus) {
        if (gold > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("agent", agent);
            response.put("action", "stay");
            return response;
        } else {
            for (String level : towerStatus.keySet()) {
                Map<String, String> levelStatus = towerStatus.get(level);
                for (String towerType : levelStatus.keySet()) {
                    int towerTypeCount = Integer.parseInt(levelStatus.get(towerType));
                    if (towerTypeCount >= 3) {
                        Map<String, Object> response = new HashMap<>();
                        response.put("agent", agent);
                        response.put("action", "upgrade");
                        response.put("level", level);
                        response.put("type", towerType);
                        return response;
                    }
                }
            }
        }
        return null;
    }
        
}

package com.example.demo.websocket;

import com.example.demo.model.TowerDefenseMessage;
import com.example.demo.model.TowerDefenseResponse;
import com.example.demo.service.DatabaseService;
import com.example.demo.service.MLService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.ArrayList;
import java.util.List;

public class WebSocketHandler extends TextWebSocketHandler {

    private final DatabaseService databaseService;
    private final MLService mlService;

    public WebSocketHandler(DatabaseService databaseService, MLService mlService) {
        this.databaseService = databaseService;
        this.mlService = mlService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String data = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        TowerDefenseMessage dataModel = objectMapper.readValue(data, TowerDefenseMessage.class);

        if (dataModel.isAgentAI()) {
            switch (dataModel.getType1()) {
                case "gold":
                    handleGoldMsg(dataModel, session);
                    break;
                case "tower":
                    if (dataModel.getStrategy().equals("ml")) {
                        handleTowerMLMsg(dataModel, session);
                    }
                    break;
                case "wave":
                    handleWaveMsg(dataModel, session);
                    break;
            }
        }
    }

    private void handleGoldMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        TowerDefenseResponse response = new TowerDefenseResponse(dataModel.getAgent(), "buy");
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
    }

    private void handleTowerMLMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        var actionItem = mlService.mlRequestEndpoint(dataModel);
        TowerDefenseResponse response = new TowerDefenseResponse(actionItem.get("agent"), actionItem.get("action"));
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
        databaseService.insertGameActionLog(dataModel, actionItem);
    }

    private void handleWaveMsg(TowerDefenseMessage dataModel, WebSocketSession session) throws Exception {
        String[] waveTimes = dataModel.getData().split(" ");
        List<Double> waveRewards = new ArrayList<>();

        for (String waveTime : waveTimes) {
            double currentWaveReward = 40 - Double.parseDouble(waveTime);
            waveRewards.add(currentWaveReward);
        }

        databaseService.insertWaveRewardLog(dataModel.getClientId(), dataModel.getGameStartTimestamp(), dataModel.getClientTimestamp(), waveRewards);

        TowerDefenseResponse response = new TowerDefenseResponse(dataModel.getAgent(), "wave_reward_processed");
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(response)));
    }
}

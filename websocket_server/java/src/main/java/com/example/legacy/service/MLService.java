package com.example.legacy.service;

import com.example.legacy.model.TowerDefenseMessage;
import com.example.legacy.model.PredictRequest;
import com.example.legacy.model.PredictResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MLService {

    @Value("${ml_server_url}")
    private String mlServerUrl;

    public Map<String, Object> mlRequestEndpoint(TowerDefenseMessage dataModel) {
        Map<String, Object> actionItem = new HashMap<>();
        try {
            int[] features = createMLFeature(dataModel.getCurrentTowerStatus(), dataModel.getCurrentWaveIndex());
            PredictRequest mlRequest = new PredictRequest(features, 0.0);

            ObjectMapper objectMapper = new ObjectMapper();
            String mlRequestJson = objectMapper.writeValueAsString(mlRequest);

            RestTemplate restTemplate = new RestTemplate();
            PredictResponse mlResponse = restTemplate.postForObject(mlServerUrl + "/predict", mlRequest, PredictResponse.class);

            actionItem = mapActionToGameState(dataModel.getAgent(), mlResponse.getAction(), dataModel.getCurrentTowerStatus());
            actionItem.put("state", mlRequest.getState());
            actionItem.put("action_idx", mlResponse.getAction());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ML Server request failed", e);
        }

        return actionItem;
    }

    public int[] createMLFeature(Map<String, Map<String, String>> towerStatusFeature, int currentWaveIndex) {
        List<Integer> featureList = new ArrayList<>();

        for (int levelFeature = 1; levelFeature <= 4; levelFeature++) {
            int[] featureListByLevel = new int[]{0, 0, 5};  // [num of types with num 2, num of types with num 1, num of types with num 0]

            Map<String, String> towerStatusByLevel = towerStatusFeature.getOrDefault(String.valueOf(levelFeature), Map.of());

            for (Map.Entry<String, String> entry : towerStatusByLevel.entrySet()) {
                String value = entry.getValue();

                if ("2".equals(value)) {
                    featureListByLevel[0] += 1;
                } else if ("1".equals(value)) {
                    featureListByLevel[1] += 1;
                }
            }

            featureListByLevel[2] = 5 - (featureListByLevel[0] + featureListByLevel[1]);

            for (int count : featureListByLevel) {
                featureList.add(count);
            }
        }

        featureList.add(currentWaveIndex);

        return featureList.stream().mapToInt(i -> i).toArray();
    }

    public Map<String, Object> mapActionToGameState(String agent, int actionType, Map<String, Map<String, String>> towerStatus) {
        Map<String, Object> actionItem = new HashMap<>();

        if (actionType == 0) {
            actionItem.put("agent", agent);
            actionItem.put("action", "stay");
        } else {
            actionItem.put("agent", agent);
            actionItem.put("action", "upgrade");
            actionItem.put("level", String.valueOf(actionType));

            String towerType = findTowerTypeToUpgrade(towerStatus, actionType, 2);
            actionItem.put("type", towerType != null ? towerType : "default");
        }

        return actionItem;
    }

    private String findTowerTypeToUpgrade(Map<String, Map<String, String>> towerStatus, int level, int count) {
        Map<String, String> levelTowerStatus = towerStatus.getOrDefault(String.valueOf(level), new HashMap<>());

        for (Map.Entry<String, String> entry : levelTowerStatus.entrySet()) {
            String type = entry.getKey();
            String typeCount = entry.getValue();

            if (Integer.parseInt(typeCount) == count) {
                return type;
            }
        }
        return null;
    }
}

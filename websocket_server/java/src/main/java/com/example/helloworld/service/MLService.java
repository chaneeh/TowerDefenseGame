package com.example.helloworld.service;

import com.example.helloworld.model.PredictRequest;
import com.example.helloworld.model.TowerDefenseMessage;
import com.example.helloworld.utils.HttpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MLService {

    @Autowired
    private HttpClientUtils httpClientUtils;

    public CompletableFuture<Map<String, Object>> mlRequestEndpoint(TowerDefenseMessage dataModel) {
        PredictRequest mlRequest = new PredictRequest(
                createMlFeature(dataModel.getCurrent_towerstatus(), dataModel.getCurrentWaveIndex())
        );

        return httpClientUtils.sendMlRequest(mlRequest)
                .thenApply(mlResponse -> {
                    Map<String, Object> actionItem = mapActionToGameState(dataModel.getAgent(), mlResponse.getAction(), dataModel.getCurrent_towerstatus());
                    actionItem.put("state", mlRequest.getState());
                    actionItem.put("action_idx", mlResponse.getAction());
                    return actionItem;
                });
    }

    public List<Integer> createMlFeature(Map<String, Map<String, String>> towerStatusFeature, int currentWaveIndex) {
        List<Integer> featureList = new ArrayList<>();
        for (int levelFeature = 1; levelFeature <= 4; levelFeature++) {
            int[] featureListByLevel = {0, 0, 5}; // [num of types with num 2, num of types with num 1, num of types with num 0]
            Map<String, String> levelStatus = towerStatusFeature.get(String.valueOf(levelFeature));
            if (levelStatus != null) {
                for (Map.Entry<String, String> entry : levelStatus.entrySet()) {
                    String value = entry.getValue();
                    if (value.equals("2")) {
                        featureListByLevel[0]++;
                    } else if (value.equals("1")) {
                        featureListByLevel[1]++;
                    }
                }
            }
            featureListByLevel[2] = 5 - (featureListByLevel[0] + featureListByLevel[1]);
            for (int feature : featureListByLevel) {
                featureList.add(feature);
            }
        }
        featureList.add(currentWaveIndex);
        return featureList;
    }

    public Map<String, Object> mapActionToGameState(String agent, int actionType, Map<String, Map<String, String>> towerStatus) {
        if (actionType == 0) {
            Map<String, Object> actionItem = new HashMap<>();
            actionItem.put("agent", agent);
            actionItem.put("action", "stay");
            return actionItem;
        } else {
            Map<String, Object> actionItem = new HashMap<>();
            actionItem.put("agent", agent);
            actionItem.put("action", "upgrade");
            actionItem.put("level", String.valueOf(actionType));
            actionItem.put("type", findTowerTypeToUpgrade(towerStatus, actionType, 2));
            return actionItem;
        }
    }

    private String findTowerTypeToUpgrade(Map<String, Map<String, String>> towerStatus, int level, int count) {
        Map<String, String> levelStatus = towerStatus.get(String.valueOf(level));
        for (Map.Entry<String, String> entry : levelStatus.entrySet()) {
            if (Integer.parseInt(entry.getValue()) == count) {
                return entry.getKey();
            }
        }
        return null;
    }
}

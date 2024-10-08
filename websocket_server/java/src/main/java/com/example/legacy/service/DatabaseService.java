package com.example.legacy.service;

import com.example.legacy.model.GameActionLog;
import com.example.legacy.model.GameActionLogReward;
import com.example.legacy.repository.GameActionLogRepository;
import com.example.legacy.repository.GameActionLogRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private GameActionLogRepository gameActionLogRepository;

    @Autowired
    private GameActionLogRewardRepository gameActionLogRewardRepository;

    public void insertGameActionLog(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, List<Integer> state, Integer action) {
        LocalDateTime serverTimestamp = LocalDateTime.now();
        GameActionLog log = new GameActionLog();
        log.setClientId(clientId);
        log.setGameStartTimestamp(gameStartTimestamp);
        log.setClientTimestamp(clientTimestamp);
        log.setServerTimestamp(serverTimestamp);
        log.setState(state);
        log.setAction(action);

        gameActionLogRepository.save(log);
    }

    public void insertWaveRewardLog(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, List<Double> rewards) {
        LocalDateTime serverTimestamp = LocalDateTime.now();
        GameActionLogReward log = new GameActionLogReward();
        log.setClientId(clientId);
        log.setGameStartTimestamp(gameStartTimestamp);
        log.setClientTimestamp(clientTimestamp);
        log.setServerTimestamp(serverTimestamp);
        log.setRewards(rewards);

        gameActionLogRewardRepository.save(log);
    }
}

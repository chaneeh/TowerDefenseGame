package com.example.demo.service;

import com.example.demo.model.GameActionLog;
import com.example.demo.model.GameActionLogReward;
import com.example.demo.repository.GameActionLogRepository;
import com.example.demo.repository.GameActionLogRewardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

@Service
public class DatabaseService {

    @Autowired
    private GameActionLogRepository gameActionLogRepository;

    @Autowired
    private GameActionLogRewardRepository gameActionLogRewardRepository;

    public void insertGameActionLog(String clientId, Timestamp gameStartTimestamp, Timestamp clientTimestamp, List<Integer> state, Integer action) {
        GameActionLog log = new GameActionLog();
        log.setClientId(clientId);
        log.setGameStartTimestamp(gameStartTimestamp);
        log.setClientTimestamp(clientTimestamp);
        log.setServerTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setState(state);
        log.setAction(action);

        gameActionLogRepository.save(log);
    }

    public void insertWaveRewardLog(String clientId, Timestamp gameStartTimestamp, Timestamp clientTimestamp, List<Double> rewards) {
        GameActionLogReward log = new GameActionLogReward();
        log.setClientId(clientId);
        log.setGameStartTimestamp(gameStartTimestamp);
        log.setClientTimestamp(clientTimestamp);
        log.setServerTimestamp(new Timestamp(System.currentTimeMillis()));
        log.setRewards(rewards);

        gameActionLogRewardRepository.save(log);
    }
}

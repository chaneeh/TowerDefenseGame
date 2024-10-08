package com.example.helloworld.service;

import com.example.helloworld.model.GameActionLogJava;
import com.example.helloworld.model.GameActionLogRewardJava;
import com.example.helloworld.repository.GameActionLogJavaRepository;
import com.example.helloworld.repository.GameActionLogRewardJavaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameLogService {

    private final GameActionLogJavaRepository logRepository;
    private final GameActionLogRewardJavaRepository logRewardrepository;

    @Autowired
    public GameLogService(GameActionLogJavaRepository logRepository, GameActionLogRewardJavaRepository logRewardrepository) {
        this.logRepository = logRepository;
        this.logRewardrepository = logRewardrepository;
    }

    @Transactional
    public void saveGameActionLogJava(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, List<Integer> state, Integer action) {
        LocalDateTime serverTimestamp = LocalDateTime.now();
        GameActionLogJava log = new GameActionLogJava(
            clientId, 
            gameStartTimestamp, 
            clientTimestamp, 
            serverTimestamp,
            state,
            action
        );
        logRepository.save(log);
        System.out.println("Log saved: " + log);
    }

    @Transactional
    public void saveGameActionLogRewardJava(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, List<Integer> rewards) {
        LocalDateTime serverTimestamp = LocalDateTime.now();
        GameActionLogRewardJava log = new GameActionLogRewardJava(
            clientId, 
            gameStartTimestamp, 
            clientTimestamp, 
            serverTimestamp,
            rewards
        );
        logRewardrepository.save(log);
        System.out.println("Log Reward saved: " + log);
    }
}

package com.example.helloworld.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game_action_log_reward_java")
public class GameActionLogRewardJava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "game_start_timestamp", nullable = false)
    private LocalDateTime gameStartTimestamp;

    @Column(name = "client_timestamp", nullable = false)
    private LocalDateTime clientTimestamp;

    @Column(name = "server_timestamp", nullable = false)
    private LocalDateTime serverTimestamp;

    @Column(name = "rewards", columnDefinition = "integer[]", nullable = false)
    private List<Integer> rewards;


    // 기본 생성자
    public GameActionLogRewardJava() {}

    // 필드를 모두 포함한 생성자
    public GameActionLogRewardJava(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, LocalDateTime serverTimestamp, List<Integer> rewards) {
        this.clientId = clientId;
        this.gameStartTimestamp = gameStartTimestamp;
        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.rewards = rewards;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getGameStartTimestamp() {
        return gameStartTimestamp;
    }

    public void setGameStartTimestamp(LocalDateTime gameStartTimestamp) {
        this.gameStartTimestamp = gameStartTimestamp;
    }

    public LocalDateTime getClientTimestamp() {
        return clientTimestamp;
    }

    public void setClientTimestamp(LocalDateTime clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }

    public LocalDateTime getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(LocalDateTime serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public List<Integer> getRewards() {
        return rewards;
    }

    public void setRewards(List<Integer> rewards) {
        this.rewards = rewards;
    }
}

package com.example.demo.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "game_action_log_reward")
public class GameActionLogReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "game_start_timestamp", nullable = false)
    private Timestamp gameStartTimestamp;

    @Column(name = "client_timestamp", nullable = false)
    private Timestamp clientTimestamp;

    @Column(name = "server_timestamp", nullable = false)
    private Timestamp serverTimestamp;

    // 보상을 배열 형태로 저장
    @ElementCollection
    @CollectionTable(name = "game_action_reward", joinColumns = @JoinColumn(name = "reward_log_id"))
    @Column(name = "rewards")
    private List<Double> rewards;

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

    public Timestamp getGameStartTimestamp() {
        return gameStartTimestamp;
    }

    public void setGameStartTimestamp(Timestamp gameStartTimestamp) {
        this.gameStartTimestamp = gameStartTimestamp;
    }

    public Timestamp getClientTimestamp() {
        return clientTimestamp;
    }

    public void setClientTimestamp(Timestamp clientTimestamp) {
        this.clientTimestamp = clientTimestamp;
    }

    public Timestamp getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Timestamp serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public List<Double> getRewards() {
        return rewards;
    }

    public void setRewards(List<Double> rewards) {
        this.rewards = rewards;
    }
}

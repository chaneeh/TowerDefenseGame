package com.example.helloworld.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "game_action_log_java")
public class GameActionLogJava {

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

    @Column(name = "state", columnDefinition = "integer[]", nullable = false)
    private List<Integer> state;

    @Column(name = "action", nullable = false)
    private Integer action;


    // 기본 생성자
    public GameActionLogJava() {}

    // 필드를 모두 포함한 생성자
    public GameActionLogJava(String clientId, LocalDateTime gameStartTimestamp, LocalDateTime clientTimestamp, LocalDateTime serverTimestamp, List<Integer> state, Integer action) {
        this.clientId = clientId;
        this.gameStartTimestamp = gameStartTimestamp;
        this.clientTimestamp = clientTimestamp;
        this.serverTimestamp = serverTimestamp;
        this.state = state;
        this.action = action;
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

    public List<Integer> getState() {
        return state;
    }

    public void setState(List<Integer> state) {
        this.state = state;
    }

    public Integer getAction() {
        return action;
    }
    
    public void setAction(Integer action) {
        this.action = action;
    }
}

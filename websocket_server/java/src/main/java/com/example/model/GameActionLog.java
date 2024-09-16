package com.example.demo.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "game_action_log")
public class GameActionLog {

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

    // 배열 형태의 상태 (예: 타워 상태 등)
    @ElementCollection
    @CollectionTable(name = "game_action_state", joinColumns = @JoinColumn(name = "action_log_id"))
    @Column(name = "state")
    private List<Integer> state;

    @Column(name = "action", nullable = false)
    private Integer action;

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

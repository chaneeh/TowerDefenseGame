package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class TowerDefenseMessage {

    @NotBlank
    private String agent;

    @NotBlank
    @JsonProperty("type_1")
    private String type1;

    @NotBlank
    @JsonProperty("type_2")
    private String type2;

    private int currentGold;
    private int currentWaveIndex;

    @NotBlank
    private String strategy;

    private String data;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientTimestampUnix;

    private LocalDateTime clientTimestamp;

    @NotBlank
    private String gameStartTimestampUnix;

    private LocalDateTime gameStartTimestamp;

    private Map<String, Object> currentTowerStatus;

    public void initTimestamps() {
        this.clientTimestamp = LocalDateTime.ofEpochSecond(Long.parseLong(clientTimestampUnix) / 1000, 0, java.time.ZoneOffset.UTC).plusHours(9);
        this.gameStartTimestamp = LocalDateTime.ofEpochSecond(Long.parseLong(gameStartTimestampUnix) / 1000, 0, java.time.ZoneOffset.UTC).plusHours(9);

        if ("tower".equals(this.type1)) {
            this.currentTowerStatus = parseTowerData(this.data);
        }
    }

    private Map<String, Object> parseTowerData(String data) {
        try {
            return new ObjectMapper().readValue(data, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing tower data", e);
        }
    }

    public boolean isAgentValid() {
        return agent != null && (agent.equals("PlayerStats") || agent.equals("TowerSpawner") || agent.equals("EnemySpawner") ||
                agent.equals("PlayerStatsAI") || agent.equals("TowerSpawnerAI") || agent.equals("EnemySpawnerAI"));
    }

    public boolean isType1Valid() {
        return type1 != null && (type1.equals("gold") || type1.equals("tower") || type1.equals("wave"));
    }

    public boolean isStrategyValid() {
        return strategy != null && (strategy.equals("greedy") || strategy.equals("ml"));
    }
}

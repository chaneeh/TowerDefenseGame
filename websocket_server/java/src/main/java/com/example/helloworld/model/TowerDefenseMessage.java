package com.example.helloworld.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Set;

import jakarta.validation.Validator;
import jakarta.validation.Validation;


@Data
@NoArgsConstructor
@Slf4j
public class TowerDefenseMessage {

    @NotNull
    @Pattern(regexp = "PlayerStats|TowerSpawner|EnemySpawner|PlayerStatsAI|TowerSpawnerAI|EnemySpawnerAI", message = "agent check failed")
    private String agent;

    @NotNull
    @Pattern(regexp = "gold|tower|wave", message = "type_1 check failed")
    private String type_1;

    @NotNull
    private String type_2;

    @NotNull
    private Integer currentGold;

    @NotNull
    private Integer currentWaveIndex;

    @NotNull
    @Pattern(regexp = "greedy|ml", message = "strategy check failed")
    private String strategy;

    @NotNull
    private String data;

    @NotNull
    private String client_id;

    @NotNull
    @JsonProperty("game_start_timestamp_unix")
    private String game_start_timestamp_unix;

    @NotNull
    @JsonProperty("client_timestamp_unix")
    private String client_timestamp_unix;

    @JsonIgnore
    private LocalDateTime game_start_timestamp;

    @JsonIgnore 
    private LocalDateTime client_timestamp;

    @JsonIgnore
    private Map<String, Map<String, String>> current_towerstatus;

    public void setGame_start_timestamp_unix(String game_start_timestamp_unix) {
        this.game_start_timestamp_unix = game_start_timestamp_unix;
        long timestampSeconds = Long.parseLong(game_start_timestamp_unix) / 1000;
        this.game_start_timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestampSeconds), ZoneOffset.ofHours(9));
    }

    public void setClient_timestamp_unix(String client_timestamp_unix) {
        this.client_timestamp_unix = client_timestamp_unix;
        long timestampSeconds = Long.parseLong(client_timestamp_unix) / 1000;
        this.client_timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestampSeconds), ZoneOffset.ofHours(9));
    }

    public void setData(String data) {
        this.data = data;
        try {
            if ("tower".equals(this.type_1)) {
                ObjectMapper objectMapper = new ObjectMapper();
                this.current_towerstatus = objectMapper.readValue(data, new TypeReference<Map<String, Map<String, String>>>() {});
            } else {
                this.current_towerstatus = null;
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse data: {}", e.getMessage());
        }
        log.info("towerstatus initialized");
    }

    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<TowerDefenseMessage>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    public boolean isAgentAI() {
        return this.agent.endsWith("AI");
    }
}

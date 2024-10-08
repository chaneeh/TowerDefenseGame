package com.example.helloworld.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Slf4j
public class TowerDefenseResponse {
    @NotNull
    @Pattern(regexp = "PlayerStatsAI|TowerSpawnerAI|EnemySpawnerAI", message = "agent check failed")
    private String agent;

    @NotNull
    @Pattern(regexp = "buy|upgrade|stay", message = "action check failed")
    private String action;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String level;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;

    public TowerDefenseResponse(String agent, String action) {
        this.agent = agent;
        this.action = action;
    }

    public TowerDefenseResponse(String agent, String action, String level, String type) {
        this.agent = agent;
        this.action = action;
        this.level = level;
        this.type = type;
    }
    
    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<TowerDefenseResponse>> violations = validator.validate(this);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

}

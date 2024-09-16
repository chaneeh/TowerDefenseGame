package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class TowerDefenseResponse {

    @NotBlank
    private String agent;

    @NotBlank
    private String action;

    private String level;
    private String type;

    public TowerDefenseResponse(String agent, String action) {
        this.agent = agent;
        this.action = action;
    }
}

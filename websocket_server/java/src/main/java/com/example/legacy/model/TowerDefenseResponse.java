package com.example.legacy.model;

import lombok.Getter;
import lombok.Setter;
import io.micrometer.common.lang.NonNull;

@Getter
@Setter
public class TowerDefenseResponse {

    @NonNull
    private String agent;

    @NonNull
    private String action;

    private String level;
    private String type;

    public TowerDefenseResponse(String agent, String action) {
        this.agent = agent;
        this.action = action;
    }
}

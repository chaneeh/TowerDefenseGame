package com.example.helloworld.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictResponse {

    @JsonProperty("action")
    private int action;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}
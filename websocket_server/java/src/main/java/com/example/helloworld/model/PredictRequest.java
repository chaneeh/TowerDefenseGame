package com.example.helloworld.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PredictRequest {

    @JsonProperty("state")
    private List<Integer> state;

    @JsonProperty("epsilon")
    private float epsilon;

    public PredictRequest(List<Integer> state) {
        this.state = state;
        this.epsilon = 0.0f;
    }

    public List<Integer> getState() {
        return state;
    }

    public void setState(List<Integer> state) {
        this.state = state;
    }

    public float getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(float epsilon) {
        this.epsilon = epsilon;
    }
}
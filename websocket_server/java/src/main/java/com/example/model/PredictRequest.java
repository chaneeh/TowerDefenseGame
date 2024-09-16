package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PredictRequest {
    private int[] state;
    private double epsilon;
}

@Getter
@Setter
@AllArgsConstructor
public class PredictResponse {
    private int action;
}

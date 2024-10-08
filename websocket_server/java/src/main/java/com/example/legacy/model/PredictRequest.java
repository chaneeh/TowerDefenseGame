package com.example.legacy.model;

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
package com.example.HealthPred.controller;

import com.example.HealthPred.model.Patient;
import com.example.HealthPred.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predict")
public class PredictionController {

    @Autowired
    private PredictionService predictionService;

    @PostMapping
    public Patient predict(@RequestBody Patient patient) {
        return predictionService.predictDisease(patient);
    }

    @GetMapping("/stats")
    public java.util.Map<String, Long> getStats() {
        return predictionService.getStats();
    }
}

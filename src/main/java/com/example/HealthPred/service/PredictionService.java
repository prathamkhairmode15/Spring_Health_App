package com.example.HealthPred.service;

import com.example.HealthPred.model.Patient;
import com.example.HealthPred.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PredictionService {

    @Autowired
    private PatientRepository patientRepository;

    private static class DiseaseRule {
        String name;
        Set<String> symptoms;

        DiseaseRule(String name, String... symptomArray) {
            this.name = name;
            this.symptoms = new HashSet<>(Arrays.asList(symptomArray));
        }

        double getCoverage(Set<String> patientSymptoms) {
            long matches = symptoms.stream().filter(patientSymptoms::contains).count();
            return (double) matches / symptoms.size();
        }

        int getMatchCount(Set<String> patientSymptoms) {
            return (int) symptoms.stream().filter(patientSymptoms::contains).count();
        }
    }

    private static final List<DiseaseRule> RULES = Arrays.asList(
        new DiseaseRule("COVID-19", "fever", "cough", "fatigue", "sore throat"),
        new DiseaseRule("Flu", "fever", "cough", "fatigue"),
        new DiseaseRule("Viral Fever", "fever", "headache", "body pain"),
        new DiseaseRule("Stomach Flu", "nausea", "fever", "fatigue"),
        new DiseaseRule("Common Cold", "cold", "cough", "sore throat"),
        new DiseaseRule("Migraine", "headache", "nausea"),
        new DiseaseRule("Throat Infection", "sore throat", "cough"),
        new DiseaseRule("Tension Headache", "headache", "body pain"),
        new DiseaseRule("Exhaustion", "fatigue", "body pain"),
        new DiseaseRule("Food Poisoning", "nausea", "fatigue"),
        new DiseaseRule("Sinusitis", "headache", "cold", "cough"),
        new DiseaseRule("Bronchitis", "cough", "fatigue", "fever")
    );

    public Patient predictDisease(Patient patient) {
        String rawSymptoms = patient.getSymptoms().toLowerCase(Locale.ROOT);
        Set<String> patientSymptomSet = new HashSet<>();
        
        for (String s : rawSymptoms.split(",")) {
            patientSymptomSet.add(s.trim());
        }

        String disease = "Consult Doctor";
        String confidence = "Low";
        double bestCoverage = 0.0;
        int bestMatchCount = 0;

        // Optimized Rule matching
        List<DiseaseRule> currentRules = Arrays.asList(
            new DiseaseRule("COVID-19", "fever", "cough", "fatigue", "sore throat"),
            new DiseaseRule("Flu", "fever", "cough", "fatigue"),
            new DiseaseRule("Viral Fever", "fever", "headache", "body pain"),
            new DiseaseRule("Stomach Flu", "nausea", "fever", "fatigue"),
            new DiseaseRule("Common Cold", "cold", "cough", "sore throat"),
            new DiseaseRule("Migraine", "headache", "nausea"),
            new DiseaseRule("Throat Infection", "sore throat", "cough"),
            new DiseaseRule("Tension Headache", "headache", "body pain"),
            new DiseaseRule("Weakness", "fatigue", "body pain"),
            new DiseaseRule("Food Poisoning", "nausea", "fatigue"),
            new DiseaseRule("Sinusitis", "headache", "cold", "cough"),
            new DiseaseRule("Bronchitis", "cough", "fatigue", "fever"),
            new DiseaseRule("Minor Fever", "fever"),
            new DiseaseRule("Minor Cough", "cough"),
            new DiseaseRule("Minor Headache", "headache"),
            new DiseaseRule("Minor Cold", "cold")
        );

        for (DiseaseRule rule : currentRules) {
            double coverage = rule.getCoverage(patientSymptomSet);
            int matches = rule.getMatchCount(patientSymptomSet);

            if (coverage > bestCoverage || (coverage == bestCoverage && matches > bestMatchCount)) {
                if (matches > 0) {
                    bestCoverage = coverage;
                    bestMatchCount = matches;
                    disease = rule.name;
                }
            }
        }

        // Confidence logic
        if (bestCoverage >= 0.8 && bestMatchCount >= 3) {
            confidence = "High";
        } else if (bestCoverage >= 0.5 && bestMatchCount >= 2) {
            confidence = "Medium";
        } else {
            confidence = "Low";
        }

        if (bestCoverage == 1.0 && bestMatchCount >= 2) {
            confidence = "High";
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = dtf.format(LocalDateTime.now());

        patient.setDisease(disease);
        patient.setConfidence(confidence);
        patient.setTimestamp(timestamp);
        
        return patientRepository.save(patient);
    }

    public Map<String, Long> getStats() {
        List<Object[]> results = patientRepository.getDiseaseStats();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }
}

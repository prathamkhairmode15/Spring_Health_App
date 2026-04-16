package com.example.HealthPred.repository;

import com.example.HealthPred.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p.disease, COUNT(p) FROM Patient p GROUP BY p.disease")
    List<Object[]> getDiseaseStats();
}

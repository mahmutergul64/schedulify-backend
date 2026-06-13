package com.schedulify.api.repository;

import com.schedulify.api.entity.HealthMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, Long> {
    List<HealthMetric> findByClientIdOrderByRecordDateAsc(Long clientId);
}
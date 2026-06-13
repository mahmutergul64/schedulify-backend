package com.schedulify.api.repository;

import com.schedulify.api.entity.Appointment;
import com.schedulify.api.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.provider.id = :providerId " +
            "AND a.status = :status " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasOverlappingAppointment(@Param("providerId") Long providerId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.status = :status AND a.startTime >= :start AND a.startTime <= :end AND a.reminderSent = false")
    List<Appointment> findAppointmentsForTomorrow(
            @Param("status") AppointmentStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT count(a) > 0 FROM Appointment a WHERE a.provider.id = :providerId AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    boolean existsOverlappingAppointment(@Param("providerId") Long providerId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    List<Appointment> findByProviderId(Long providerId);

    List<Appointment> findByClientId(Long clientId);
}
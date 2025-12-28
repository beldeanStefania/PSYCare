package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.Appointment;
import com.goodfellas.backend.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByPsychologistIdAndStatusOrderByStartTimeAsc(int psychologistId, AppointmentStatus status);
    List<Appointment> findByPatientIdAndStatusOrderByStartTimeAsc(int patient, AppointmentStatus status);
    @Query("""
        select count(a) > 0
        from Appointment a
        where a.psychologist.id = :psychologistId
          and a.status = com.goodfellas.backend.model.AppointmentStatus.BOOKED
          and a.startTime < :endTime
          and a.endTime > :startTime
    """)
    boolean existsOverlapForPsychologist(int psychologistId, LocalDateTime startTime, LocalDateTime endTime);
}

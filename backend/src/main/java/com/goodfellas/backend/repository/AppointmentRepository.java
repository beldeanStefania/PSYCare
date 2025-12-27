package com.goodfellas.backend.repository;

import com.goodfellas.backend.model.Appointment;
import com.goodfellas.backend.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByPsychologistIdAndStatusOrderByStartTimeAsc(int psychologistId, AppointmentStatus status);
    @Query("""
        select count(a) > 0
        from Appointment a
        where a.psychologist.id = :psychologistId
          and a.status = com.goodfellas.backend.model.AppointmentStatus.BOOKED
          and a.startTime < :endTime
          and a.endTime > :startTime
    """)
    boolean existsOverlapForPsychologist(int psychologistId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("""
        select a
        from Appointment a
        join fetch a.patient p
        where a.psychologist.id = :psychologistId
          and (:status is null or a.status = :status)
        order by a.startTime desc
    """)
    List<Appointment> findAllForPsychologistWithPatient(int psychologistId, AppointmentStatus status);
}

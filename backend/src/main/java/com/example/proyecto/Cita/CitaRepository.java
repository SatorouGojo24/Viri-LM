package com.example.proyecto.cita;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    // Este método es nuestra regla de negocio para bloquear horarios ya ocupados
    List<Cita> findByFechaHoraInicioBetween(LocalDateTime inicio, LocalDateTime fin);
}
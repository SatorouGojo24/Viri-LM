package com.example.proyecto.cita;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HorarioOcupadoDTO {
    private LocalDateTime inicio;
    private LocalDateTime fin;
}
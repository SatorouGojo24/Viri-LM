package com.example.proyecto.cita;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CitaDTO {
    private String nombreCliente;
    private String telefonoCliente;
    private LocalDateTime fechaHora;
    private List<Long> servicioIds; 
}
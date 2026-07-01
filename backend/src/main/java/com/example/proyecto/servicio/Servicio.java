package com.example.proyecto.servicio;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "servicios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre; 

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private BigDecimal precio; 

    @Column(nullable = false)
    private Integer duracionMinutos; 


    @Column
    private String imagenNombre;
}
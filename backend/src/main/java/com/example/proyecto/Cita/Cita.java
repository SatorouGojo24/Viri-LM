package com.example.proyecto.cita;

import com.example.proyecto.cliente.Cliente;
import com.example.proyecto.servicio.Servicio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciona la cita con la clienta que reservó
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Relaciona la cita con uno o múltiples servicios del carrito
    @ManyToMany
    @JoinTable(
        name = "cita_servicios",
        joinColumns = @JoinColumn(name = "cita_id"),
        inverseJoinColumns = @JoinColumn(name = "servicio_id")
    )
    private List<Servicio> servicios;

    @Column(nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable = false)
    private LocalDateTime fechaHoraFin;

    @Column(nullable = false)
    private BigDecimal totalPagar;

    @Column(nullable = false)
    private String estadoPago; // Ej: "PENDIENTE", "PAGADO_PAYPAL"

    @Column(unique = true)
    private String paypalOrderId;
}
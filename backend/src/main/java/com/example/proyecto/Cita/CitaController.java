package com.example.proyecto.cita;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
@RequiredArgsConstructor
public class CitaController {
    
    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<String> crearCita(@RequestBody CitaDTO dto) {
        citaService.agendar(dto);
        return ResponseEntity.ok("Cita guardada con éxito");
    }

    // NUEVO: Angular llamará a esta ruta cuando la clienta seleccione un día en el calendario
    @GetMapping("/ocupadas")
    public ResponseEntity<List<HorarioOcupadoDTO>> obtenerHorariosOcupados(@RequestParam("fecha") String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        List<HorarioOcupadoDTO> ocupadas = citaService.obtenerHorariosOcupados(fecha);
        return ResponseEntity.ok(ocupadas);
    }
}
package com.example.proyecto.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioService.obtenerCatalogo();
    }

    @PostMapping
    public Servicio agregarServicio(@RequestBody Servicio servicio) {
        return servicioService.guardarServicio(servicio);
    }
}
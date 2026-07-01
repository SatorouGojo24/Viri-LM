package com.example.proyecto.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<Servicio> obtenerCatalogo() {
        return servicioRepository.findAll();
    }

    public Servicio guardarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
}
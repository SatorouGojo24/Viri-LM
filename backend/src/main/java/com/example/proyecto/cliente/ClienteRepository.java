package com.example.proyecto.cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    // Este método lo necesita el CitaService para saber si la clienta ya existe
    Optional<Cliente> findByTelefono(String telefono);
}
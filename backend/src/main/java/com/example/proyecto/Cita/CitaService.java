package com.example.proyecto.cita;

import com.example.proyecto.cliente.Cliente;
import com.example.proyecto.cliente.ClienteRepository;
import com.example.proyecto.servicio.Servicio;
import com.example.proyecto.servicio.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CitaService {
    
    private final CitaRepository citaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final GoogleCalendarService googleCalendarService;

    @Transactional
    public void agendar(CitaDTO dto) {
        // 1. Buscamos al cliente por teléfono o lo creamos
        Cliente cliente = clienteRepository.findByTelefono(dto.getTelefonoCliente())
            .orElseGet(() -> {
                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setNombreCompleto(dto.getNombreCliente());
                nuevoCliente.setTelefono(dto.getTelefonoCliente());
                return clienteRepository.save(nuevoCliente);
            });

        // 2. Buscamos los servicios por ID
        List<Servicio> servicios = servicioRepository.findAllById(dto.getServicioIds());
        
        // 3. Calculamos total y duración
        BigDecimal total = servicios.stream().map(Servicio::getPrecio).reduce(BigDecimal.ZERO, BigDecimal::add);
        int duracionTotal = servicios.stream().mapToInt(Servicio::getDuracionMinutos).sum();

        // 4. Guardamos la cita en la base de datos local
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setServicios(servicios);
        cita.setFechaHoraInicio(dto.getFechaHora());
        cita.setFechaHoraFin(dto.getFechaHora().plusMinutes(duracionTotal));
        cita.setTotalPagar(total);
        cita.setEstadoPago("EFECTIVO_EN_SUCURSAL");
        
        citaRepository.save(cita);
        
       
        String nombresServicios = servicios.stream().map(Servicio::getNombre).collect(Collectors.joining(", "));
        
        // Limpiamos los espacios para que la URL no se rompa
        String nombreUrl = cliente.getNombreCompleto().replace(" ", "%20");
        
        // Extraemos y formateamos la hora de inicio en formato HH:mm 
        String horaFormateada = String.format("%02d:%02d", cita.getFechaHoraInicio().getHour(), cita.getFechaHoraInicio().getMinute());
        
        // Creamos el enlace directo a WhatsApp incluyendo la hora dinámica
        String waLink = "https://wa.me/52" + cliente.getTelefono() + "?text=Hola%20" + nombreUrl + "%2C%20soy%20Viri.%20Te%20escribo%20para%20confirmar%20tu%20cita%20en%20VIRI%20LM%20a%20las%20" + horaFormateada + "%20para%20tus%20servicios.%20%C2%A1Te%20espero!";
        
        String descripcionEvento = "Servicios: " + nombresServicios + "\n\n" +
                                   "Teléfono: " + cliente.getTelefono() + "\n" +
                                   "Hora reservada: " + horaFormateada + " hrs\n\n" +
                                   "📲 Toca aquí para confirmar: " + waLink;

        googleCalendarService.crearEvento(
            cliente.getNombreCompleto(), 
            descripcionEvento, 
            cita.getFechaHoraInicio(), 
            cita.getFechaHoraFin()
        );
    }

    public List<HorarioOcupadoDTO> obtenerHorariosOcupados(LocalDate fecha) {
        // Obtenemos desde las 00:00:00 hasta las 23:59:59 de ese día
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(23, 59, 59);
        
        // Buscamos todas las citas que caen en ese día
        List<Cita> citasDelDia = citaRepository.findByFechaHoraInicioBetween(inicioDia, finDia);
        
        return citasDelDia.stream()
                .map(cita -> new HorarioOcupadoDTO(cita.getFechaHoraInicio(), cita.getFechaHoraFin()))
                .collect(Collectors.toList());
    }
}
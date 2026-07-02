package com.example.proyecto.cita;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleCalendarService {

    @Value("${google.credentials.path}")
    private String credentialsPath;

    
    private static final String CALENDAR_ID = "vimaco091195@gmail.com"; 

    public void crearEvento(String nombreCliente, String servicios, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        try {
            
            InputStream in;
            File secretFile = new File(credentialsPath);
            
            if (secretFile.exists()) {
                
                in = new FileInputStream(secretFile);
            } else {
                // Si no existe, asume que estamos en local y lo busca en el resources/ del proyecto
                String cp = credentialsPath.startsWith("/") ? credentialsPath : "/" + credentialsPath;
                in = GoogleCalendarService.class.getResourceAsStream(cp);
            }

            if (in == null) {
                throw new RuntimeException("No se encontró el archivo de credenciales de Google: " + credentialsPath);
            }

            GoogleCredential credential = GoogleCredential.fromStream(in)
                    .createScoped(Collections.singleton(CalendarScopes.CALENDAR));

            // 2. Conectarse a la nube de Google
            Calendar service = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), 
                    GsonFactory.getDefaultInstance(), 
                    credential)
                    .setApplicationName("Sistema Salon Reservas")
                    .build();

            // 3. Crear los detalles visuales de la cita
            Event event = new Event()
                .setSummary("Cita: " + nombreCliente)
                .setDescription("Servicios solicitados: " + servicios + "\nEstado de pago: Pendiente en sucursal");

            // 4. Configurar las horas usando la zona horaria exacta de México
            ZoneId zonaMexico = ZoneId.of("America/Mexico_City");
            Date startDate = Date.from(fechaInicio.atZone(zonaMexico).toInstant());
            Date endDate = Date.from(fechaFin.atZone(zonaMexico).toInstant());

            EventDateTime start = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(startDate))
                .setTimeZone("America/Mexico_City");
            event.setStart(start);

            EventDateTime end = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(endDate))
                .setTimeZone("America/Mexico_City");
            event.setEnd(end);

            // 5. Guardar en el calendario
            service.events().insert(CALENDAR_ID, event).execute();
            System.out.println("¡ÉXITO! El evento se guardó mágicamente en el Google Calendar.");

        } catch (Exception e) {
            System.err.println("Error al intentar guardar en Google Calendar: " + e.getMessage());
        }
    }
}
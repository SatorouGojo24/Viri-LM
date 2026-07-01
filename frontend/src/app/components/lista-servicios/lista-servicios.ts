import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { CatalogoService, Servicio } from '../../services/catalogo'; 
import { CarritoService } from '../../services/carrito';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-lista-servicios',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './lista-servicios.html',
  styleUrl: './lista-servicios.scss'
})
export class ListaServiciosComponent implements OnInit {
  
  servicios = signal<Servicio[]>([]);
  mostrarModal = signal(false);
  mostrarModalExito = signal(false); 
  
  // NUEVOS SIGNALS PARA ALERTAS Y CARGA
  mostrarModalError = signal(false);
  mensajeError = '';
  cargando = signal(false);

  nombreCliente = '';
  telefonoCliente = '';
  fechaSeleccionada = '';
  horaSeleccionada = '';
  horariosDisponibles: string[] = [];
  hoy = new Date().toISOString().split('T')[0]; 

  constructor(
    private catalogoService: CatalogoService,
    public carritoService: CarritoService,
    private http: HttpClient 
  ) {}

  ngOnInit(): void {
    this.catalogoService.obtenerServicios().subscribe({
      next: (datos) => this.servicios.set(datos),
      error: (err) => console.error('Error con Java:', err)
    });
  }

  agregarAlCarrito(servicio: Servicio) {
    this.carritoService.agregar(servicio);
  }

  vaciarCarrito() {
    this.carritoService.carrito.set([]);
  }

  limpiarFormulario() {
    this.nombreCliente = '';
    this.telefonoCliente = '';
    this.fechaSeleccionada = '';
    this.horaSeleccionada = '';
    this.horariosDisponibles = [];
    this.cargando.set(false);
  }

  abrirModal() { this.mostrarModal.set(true); }
  cerrarModal() { this.limpiarFormulario(); this.mostrarModal.set(false); }

  abrirModalExito() { this.mostrarModalExito.set(true); }
  cerrarModalExito() { this.mostrarModalExito.set(false); }

  // Controladores del nuevo modal de avisos
  mostrarError(mensaje: string) {
    this.mensajeError = mensaje;
    this.mostrarModalError.set(true);
  }
  cerrarModalError() {
    this.mostrarModalError.set(false);
    this.mensajeError = '';
  }

  buscarDisponibilidad() {
    this.horaSeleccionada = '';
    
    if (!this.fechaSeleccionada) return;

    const dateObj = new Date(this.fechaSeleccionada + 'T00:00:00');
    if (dateObj.getDay() === 0) {
      this.mostrarError('✨ Los domingos descansamos en VIRI LM. Por favor elige otro día.');
      this.fechaSeleccionada = '';
      this.horariosDisponibles = [];
      return;
    }

    // Activamos la carga para ocultar avisos falsos
    this.cargando.set(true);
    this.horariosDisponibles = [];

    this.http.get<any[]>(`${environment.apiUrl}/citas/ocupadas?fecha=${this.fechaSeleccionada}`)
      .subscribe({
        next: (ocupadas) => {
          this.generarHorarios(ocupadas);
          this.cargando.set(false); // Carga finalizada con éxito
        },
        error: (err) => {
          console.error('Error al consultar horarios', err);
          this.mostrarError('Hubo un problema al consultar la disponibilidad. Intenta de nuevo.');
          this.cargando.set(false);
        }
      });
  }

  generarHorarios(ocupadas: any[]) {
    const duracionCarrito = this.carritoService.carrito().reduce((acc, s) => acc + s.duracionMinutos, 0);
    const slots: string[] = [];
    
    let horaActual = new Date(`${this.fechaSeleccionada}T09:00:00`);
    const horaCierre = new Date(`${this.fechaSeleccionada}T18:00:00`);

    while (true) {
      const finPosible = new Date(horaActual.getTime() + duracionCarrito * 60000);
      
      if (finPosible > horaCierre) break;

      const choca = ocupadas.some(cita => {
         const oInicio = new Date(cita.inicio);
         const oFin = new Date(cita.fin);
         return horaActual < oFin && finPosible > oInicio;
      });

      if (!choca) {
         const hh = horaActual.getHours().toString().padStart(2, '0');
         const mm = horaActual.getMinutes().toString().padStart(2, '0');
         slots.push(`${hh}:${mm}`);
      }

      horaActual = new Date(horaActual.getTime() + 30 * 60000);
    }
    
    this.horariosDisponibles = slots;
  }

  confirmarCita() {
    if(!this.nombreCliente || !this.telefonoCliente || !this.fechaSeleccionada || !this.horaSeleccionada) {
      this.mostrarError('Por favor llena todos tus campos y selecciona un horario disponible.');
      return;
    }

    const fechaHoraFinal = `${this.fechaSeleccionada}T${this.horaSeleccionada}:00`;

    const payload = {
      nombreCliente: this.nombreCliente,
      telefonoCliente: this.telefonoCliente,
      fechaHora: fechaHoraFinal,
      servicioIds: this.carritoService.carrito().map(s => s.id)
    };

    this.http.post(`${environment.apiUrl}/citas`, payload, { responseType: 'text' }).subscribe({
      next: () => {
        this.carritoService.carrito.set([]); 
        this.limpiarFormulario();
        this.cerrarModal();
        this.abrirModalExito(); 
      },
      error: (err) => {
        console.error(err);
        this.mostrarError('No pudimos registrar tu cita. Por favor verifica tu conexión e intenta de nuevo.');
      }
    });
  }
}
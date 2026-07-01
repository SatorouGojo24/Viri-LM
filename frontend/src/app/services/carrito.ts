import { Injectable, signal, computed } from '@angular/core';
import { Servicio } from './catalogo';

@Injectable({
  providedIn: 'root'
})
export class CarritoService {
  
  // La memoria: Un Signal que guarda la lista de lo que la clienta elige
  carrito = signal<Servicio[]>([]);

  // La calculadora: Un Signal "inteligente" que suma los precios automáticamente
  total = computed(() => {
    return this.carrito().reduce((suma, item) => suma + item.precio, 0);
  });

  // La acción: El método para meter un servicio a la bolsa
  agregar(servicio: Servicio) {
    this.carrito.update(items => [...items, servicio]);
  }

  
  eliminar(index: number) {
    this.carrito.update(items => items.filter((_, i) => i !== index));
  }
}
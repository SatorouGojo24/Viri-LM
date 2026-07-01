import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Servicio {
  id?: number;
  nombre: string;
  descripcion: string;
  precio: number;
  duracionMinutos: number;
  imagenNombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
 
  private apiUrl = `${environment.apiUrl}/servicios`;

  constructor(private http: HttpClient) { }

  obtenerServicios(): Observable<Servicio[]> {
    return this.http.get<Servicio[]>(this.apiUrl);
  }
}
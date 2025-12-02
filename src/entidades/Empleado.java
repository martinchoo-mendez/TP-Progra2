package entidades;

import java.util.ArrayList;

public abstract class Empleado {
	private String nombre;
	private Integer legajo;
	private boolean disponible;
	private Integer cumuloDeRetrasos;
	private ArrayList <Tarea> historialDeTareas;
	private static int ultimo = 100;
	
	public Empleado( String nombre) {
		this.nombre = nombre;
		this.legajo = ultimo;  
		this.disponible = true;
		this.cumuloDeRetrasos = 0;
		this.historialDeTareas = new ArrayList<>();
		incrementarUltimo();
	}
	
	public static void resetearUltimo() {
		ultimo = 100;
	}
	
	public static void incrementarUltimo() {
		ultimo++;
	}
	
	public void cambiarEstado() {
	    this.disponible = !this.disponible;
	}
	
	public void aumentarRetrasos() {
		cumuloDeRetrasos++;
	}
	
	public void agregarTareaHistorial (Tarea tarea) {
		historialDeTareas.add(tarea);
	}
	
	
	public boolean tuvoTareasConRetrasos() {
		boolean tareasConRetraso = false;
		for(Tarea tarea : historialDeTareas) {
			tareasConRetraso = tareasConRetraso || tarea.verTuvoRetraso();
		}
		return tareasConRetraso;
	}
	
	public abstract double calcularCosto(double diasNecesarios);
	
	public boolean verDisponible() {
		return disponible;
	}
	
	public String verNombre() {
		return nombre;
	}
	
	public Integer mostrarRetrasos() {
		return cumuloDeRetrasos;
	}
	
	public int verLegajo() {
		return legajo;
	}

	@Override
	public String toString() {
	    StringBuilder datos = new StringBuilder();
	    datos.append("Empleado = [nombre=");
	    datos.append(nombre);
	    datos.append(", legajo=");
	    datos.append(legajo);
	    datos.append(", disponible=");
	    datos.append(disponible);
	    datos.append(", cumuloDeRetrasos=");
	    datos.append(cumuloDeRetrasos);
	    datos.append(", historialDeTareas=");
	    datos.append(historialDeTareas);
	    return datos.toString();
	}
	
}

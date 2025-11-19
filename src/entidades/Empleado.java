package entidades;

import java.util.ArrayList;

public abstract class Empleado {
	private String nombre;
	private Integer legajo;
	private boolean disponible;
	private int cumuloDeRetrasos;
	private ArrayList <Tarea> historialDeTareas;
	private static int ultimo = 100;
	
	public Empleado( String nombre) { //el constructor lleva el nombre de la clase, no crearClase, es decir, en este caso, no se tiene que llamar crearEmpleado, se tiene que llamar Empleado, y sin poner el void. Porque sino, no es un constructor, y no se puede heredar
		this.nombre = nombre;
		this.legajo = ultimo;  //el legajo no lo puede poner el usuario, sino, el sistema para que este sea único siempre
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
	
	public boolean verDisponible() {
		return disponible;
	}
	
	public void cambiarEstado() {
	    this.disponible = !this.disponible;
	}
	
	public void aumentarRetrasos() {
		cumuloDeRetrasos++;
	}
	
	public int mostrarRetrasos() {
		return cumuloDeRetrasos;
	}
	
	public abstract double calcularCosto(double diasNecesarios);
	
	public boolean tuvoTareasConRetrasos() {
		boolean tareasConRetraso = false;
		for(Tarea tarea : historialDeTareas) {
			tareasConRetraso = tareasConRetraso || tarea.verTuvoRetraso(); //falta agregar tuvoRetraso en Tarea
		}
		return tareasConRetraso;
	}
	
	public int verLegajo() {
		return legajo;
	}
	
	public String verNombre() {
		return nombre;
	}
	
	public void agregarTareaHistorial (Tarea tarea) {
		historialDeTareas.add(tarea);
	}
	
	public boolean tuvoTareas() {
		if(historialDeTareas.size()>0) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Empleado [nombre=" + nombre + ", legajo=" + legajo + ", disponible=" + disponible
				+ ", cumuloDeRetrasos=" + cumuloDeRetrasos + ", historialDeTareas=" + historialDeTareas + "]";
	}
	
}

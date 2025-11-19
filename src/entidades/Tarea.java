package entidades;


public class Tarea {
	private String titulo;
	private String descripcion;
	private double diasNecesarios;
	private Empleado responsable;
	private double costo;
	private double cantidadRetrasos;
	private boolean finalizado;
	private boolean tuvoRetraso;
	
	public Tarea(String titulo, String descripcion, double diasNecesarios) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.diasNecesarios = diasNecesarios;
		this.finalizado = false;
		this.costo = 0;
		this.cantidadRetrasos = 0;
		this.tuvoRetraso = false;
	}
	
	public void asignarResponsable(Empleado empleado) {
		this.responsable = empleado;
		empleado.cambiarEstado();
	}
	
	public void cambiarResponsable(Empleado nuevoEmpleado) {
	    responsable.cambiarEstado();  // Libera al antiguo
	    nuevoEmpleado.cambiarEstado(); // Ocupa al nuevo
	    responsable = nuevoEmpleado;   // Cambia la referencia
	    costo = 0;  // AGREGAR ESTA LÍNEA - resetea el costo para que se recalcule
	}
	
	public double verCosto() {
		return costo;
	}
	
	
	public double calcularCosto() {
		costo = responsable.calcularCosto(diasNecesarios);
		return costo;
	}
	
	public boolean hayEmpleado() {
		return responsable != null;
	}
	
	public void aumentarCantidadDeRetrasos(double diasRetrasados) {
		cantidadRetrasos += diasRetrasados;
		tuvoRetraso = true;
	//	System.out.println(">> Se registraron " + diasRetrasados + " días de retraso. Total ahora: " + cantidadRetrasos);
	}
	
	//solo método de prueba para ver si registra los retrasos
	public double cantidadDeRetrasos() {
		return cantidadRetrasos;
	}
	
	public void cambiarFinalizado() {
		finalizado = true;
		if(responsable != null) {
			responsable.cambiarEstado();
			responsable = null;
		}
	}
	
	public boolean verDiasCompletos() {
		return true;
	}
	
	public void liberarResponsable() {
		 if (responsable != null) {
		       responsable.cambiarEstado();  // Cambia estado del empleado a disponible
		       responsable = null;  // Libera la referencia al empleado
		  }
	}

	public boolean verTuvoRetraso() {
		return tuvoRetraso;
	}
	
	public String verTitulo() {
		return titulo;
	}
	
	public Empleado verResponsable() {
		return responsable;
	}
	
	public boolean verFin() {
		return finalizado;
	}

	@Override
	public String toString() {
		return titulo;
	}
	
	
	
}
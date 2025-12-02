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
	    responsable.cambiarEstado();  
	    nuevoEmpleado.cambiarEstado();
	    responsable = nuevoEmpleado;
	}
	
	public void aumentarCantidadDeRetrasos(double diasRetrasados) {
		cantidadRetrasos += diasRetrasados;
		tuvoRetraso = true;
	}
	
	public void aumentarCantidadDeRetrasosDelEmpleado() {
		responsable.aumentarRetrasos();
	}
	
	public void cambiarFinalizado() {
		finalizado = true;
		if(responsable != null) {
			responsable.cambiarEstado();
			responsable = null;
		}
	}
	
	public void liberarResponsable() {
		 if (responsable != null) {
		       responsable.cambiarEstado();  
		       responsable = null;
		  }
	}
	
	public void calcularCosto() {
		costo = responsable.calcularCosto(diasNecesarios);
	}
	
	public boolean hayEmpleado() {
		return responsable != null;
	}

	public boolean verTuvoRetraso() {
		return tuvoRetraso;
	}
	
	public boolean verFin() {
		return finalizado;
	}
	
	public Empleado verResponsable() {
		return responsable;
	}
	
	public double verCosto() {
		return costo;
	}

	public String verTitulo() {
		return titulo;
	}
	
	@Override
	public String toString() {
		return titulo;
	}
	
}
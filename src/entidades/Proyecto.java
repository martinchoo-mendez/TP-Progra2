package entidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Proyecto {
	private String [] cliente;
	private String [] titulosDeTareas;
	private String [] descripcion;
	private double [] dias;
	private Map <String, Tarea> tareas; //consultar si se puede con esto o hay que hacer un conjunto
	private Integer numeroId;
	private String direccion;
	private String fechaInicio;
	private String fechaEstimada;
	private String fechaFinalizacion;
	private String estado;
	private double costoFinal;
	private double costoParcial;
	private static int ultimo = 1000;
	
	public Proyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio, String[] cliente, String inicio, String fin) {
		this.tareas = new HashMap<String, Tarea>();
		this.titulosDeTareas = titulos;
		this.descripcion = descripcion;
		this.dias = dias;
		this.direccion = domicilio;
		this.cliente = cliente;
		this.fechaInicio = inicio;
		this.fechaEstimada = fin;
		this.numeroId = ultimo;
		this.estado = Estado.pendiente;
		this.costoFinal = 0;
		this.costoParcial = 0;
		incrementarUltimo();
		this.asignarTarea(); // Agrega esto para poblar el conjunto de tareas
	}
	
	public static void resetearUltimo() {
	    ultimo = 0;
	}
	
	public void incrementarUltimo() {
		ultimo++;
	}
	
	public void asignarTarea() {
		for(int i = 0; i<titulosDeTareas.length; i++) {
			Tarea tarea = new Tarea (titulosDeTareas[i], descripcion[i], dias[i]);
			tareas.put(tarea.verTitulo(), tarea);
		}
	}
	
	public String verEstado() {
		return estado;
	}
	
	
	public void actualizarEstadoActivo() {
		estado = Estado.activo;
	}
	
	public void ActualizarEstadoFIn() {
		estado = Estado.finalizado;
	}
	
	public boolean algunaTareaConRetraso() {
		boolean algunaTarea = false;
		for(Tarea tarea: tareas.values()) {
			algunaTarea = algunaTarea || tarea.verTuvoRetraso();
		}
		return algunaTarea;
	}
	
	public void calcularCostoParcial() {
	    costoParcial = 0;
	    for (Tarea tarea: tareas.values()) {
	        if(tarea.hayEmpleado()) {
	            double costoTarea = tarea.calcularCosto();
	            // Agregar bono del 2% si es EmpleadoPlanta sin retraso
	            if (tarea.verResponsable() instanceof EmpleadoPlanta && !tarea.verTuvoRetraso()) {
	                costoTarea *= 1.02;
	            }
	            costoParcial += costoTarea;
	        }
	    }
	    
	    if(!algunaTareaConRetraso()) {
	        costoParcial *= 1.35;
	    }
	    else {
	        costoParcial *= 1.25;
	    }
	}
	
	public double verCostoParcial() {
		return costoParcial;
	}
	
	public void calcularCostoFinal() {
	    costoFinal = 0;
	    for (Tarea tarea: tareas.values()) {
	        if(tarea.hayEmpleado()) {
	            double costoTarea = tarea.calcularCosto();
	            // Agregar bono del 2% si es EmpleadoPlanta sin retraso
	            if (tarea.verResponsable() instanceof EmpleadoPlanta && !tarea.verTuvoRetraso()) {
	                costoTarea *= 1.02;
	            }
	            costoFinal += costoTarea;
	        }
	    }
	    
	    if(algunaTareaConRetraso()) {
	        costoFinal *= 1.25;
	    }
	    else {
	        costoFinal *= 1.35;
	    }
	}
	
	public double verCostoFinal() {
		return costoFinal;
	}
	
	public void asignarEmpleadoEnTarea(Empleado empleado, String tituloDeTarea) {
		for(Tarea tarea : tareas.values()) {
			if(tituloDeTarea.equals(tarea.verTitulo())) {
				tarea.asignarResponsable(empleado);
			}
		}
	}
	
	public void registrarRetrasoEnTarea(String titulo, double cantidadDias) {
		Tarea tarea = tareas.get(titulo);
		tarea.aumentarCantidadDeRetrasos(cantidadDias);
	}
	
	public String verDireccion() {
		return direccion;
	}
	
	public int verId() {
		return numeroId;
	}
	
	public boolean existeTarea(String titulo) {
		boolean existe = false;
		for(Tarea tarea : tareas.values()) {
			existe = existe || tarea.verTitulo().equals(titulo);
		}
		return existe;
	}
	
	public Tarea verTarea (String titulo) {
		Tarea tareaADevolver = null;
		for(Tarea tarea: tareas.values()) {
			if(tarea.verTitulo().equals(titulo)) {
				tareaADevolver = tarea; 
			}
		}
		return tareaADevolver;
	}
	
	public Set<Tarea> verTareas() {
		return (Set<Tarea>) tareas.values();
	}
	
	public void agregarTareasProyectoExistente(String titulo, String descripcion, double dias) {
		Tarea nuevaTarea = new Tarea (titulo, descripcion, dias);
		tareas.put(titulo, nuevaTarea);
	}
	
	public void fechaDeFin (String fin) {
		this.fechaFinalizacion = fin; 
	}
	
	public String fechaDeInicio() {
		return fechaInicio;
	}
	
	public void liberarEmpleados() {
	    for (Tarea tarea : tareas.values()) {
	        if (tarea.verResponsable() != null) {  // Asumiendo que verResponsable() devuelve el Empleado
	            //tarea.verResponsable().cambiarEstado();  // Cambia estado del empleado a disponible
	            tarea.liberarResponsable();  // Asumiendo que Tarea tiene este método para setear empleado a null
	        }
	    }
	}
	
	public List<Tupla<Integer, String>> empleadosDelProyecto(){
		List<Tupla<Integer, String>> empleados = new ArrayList<>();
		for(Tarea tarea: tareas.values()) {
			if(tarea.hayEmpleado()) {
				Empleado empleado = tarea.verResponsable();
				Tupla<Integer, String> tupla = new Tupla<Integer, String> (empleado.verLegajo(), empleado.verNombre());
				empleados.add(tupla);
			}
		}
		return empleados;
	}
	
	public Object[] tareasSinEmpleado() {
		List<Tarea> tareasSinResponsable = new ArrayList<>();
		for(Tarea tarea: tareas.values()) {
			if(!tarea.hayEmpleado()) {
				tareasSinResponsable.add(tarea);
			}
		}
		return tareasSinResponsable.toArray();
	}
	
	public String fechaDeFinEstimada() {
		return fechaEstimada;
	}

	@Override
	public String toString() {
		return "Proyecto [cliente=" + Arrays.toString(cliente) + ", titulosDeTareas=" + Arrays.toString(titulosDeTareas)
				+ ", descripcion=" + Arrays.toString(descripcion) + ", dias=" + Arrays.toString(dias) + ", tareas="
				+ tareas + ", numeroId=" + numeroId + ", direccion=" + direccion + ", fechaInicio=" + fechaInicio
				+ ", fechaEstimada=" + fechaEstimada + ", fechaFinalizacion=" + fechaFinalizacion + ", estado=" + estado
				+ ", costoFinal=" + costoFinal + "]";
	}	
}
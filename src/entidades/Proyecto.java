package entidades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
		asignarTarea(); // Agrega esto para poblar el conjunto de tareas
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
	
	public void actualizarEstadoActivo() {
		estado = Estado.activo;
	}
	
	public void ActualizarEstadoFIn() {
		estado = Estado.finalizado;
	}
	
	public void fechaDeFin (String fin) {
		this.fechaFinalizacion = fin; 
	}
	
	public void calcularCostoParcial() {
	    costoParcial = 0;
	    for (Tarea tarea: tareas.values()) {
	        if(tarea.hayEmpleado()) {
	        	tarea.calcularCosto();
	            double costoTarea = tarea.verCosto();
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
	
	public void calcularCostoFinal() {
	    costoFinal = 0;
	    for (Tarea tarea: tareas.values()) {
	        if(tarea.hayEmpleado()) {
	        	tarea.calcularCosto();
	            double costoTarea = tarea.verCosto();
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
		
	public void asignarEmpleadoEnTarea(Empleado empleado, String tituloDeTarea) {
		Tarea tarea = tareas.get(tituloDeTarea);
		tarea.asignarResponsable(empleado);
	}
	
	public void registrarRetrasoEnTarea(String titulo, double cantidadDias) {
		Tarea tarea = tareas.get(titulo);
		tarea.aumentarCantidadDeRetrasos(cantidadDias);
		tarea.aumentarCantidadDeRetrasosDelEmpleado();
	}
	
	public void agregarTareasProyectoExistente(String titulo, String descripcion, double dias) {
		Tarea nuevaTarea = new Tarea (titulo, descripcion, dias);
		tareas.put(titulo, nuevaTarea);
	}
	
	public void liberarEmpleados() {
	    for (Tarea tarea : tareas.values()) {
	        if (tarea.verResponsable() != null) { 
	           tarea.liberarResponsable(); 
	        }
	    }
	}
	
	public boolean algunaTareaConRetraso() {
		boolean algunaTarea = false;
		for(Tarea tarea: tareas.values()) {
			algunaTarea = algunaTarea || tarea.verTuvoRetraso();
		}
		return algunaTarea;
	}
	
	public boolean existeTarea(String titulo) {
		boolean existe = false;
		for(Tarea tarea : tareas.values()) {
			existe = existe || tarea.verTitulo().equals(titulo);
		}
		return existe;
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
	
	public Set<Tarea> verTareas() {
		Set <Tarea> tareaDeProyecto= new HashSet <>();
		for(Tarea tarea: tareas.values()) {
			tareaDeProyecto.add(tarea);
		}
		return tareaDeProyecto;
	}
	
	public Tarea verTarea (String titulo) {
		Tarea tarea = tareas.get(titulo);
		return tarea;
	}
	
	public String fechaDeFinEstimada() {
		return fechaEstimada;
	}
	
	public String verEstado() {
		return estado;
	}
	
	public String verDireccion() {
		return direccion;
	}
	
	public String fechaDeInicio() {
		return fechaInicio;
	}
	
	public double verCostoParcial() {
		return costoParcial;
	}
	
	public double verCostoFinal() {
		return costoFinal;
	}
	
	public int verId() {
		return numeroId;
	}


	@Override
	public String toString() {
	    StringBuilder datos = new StringBuilder();
	    datos.append("Proyecto [cliente=");
	    datos.append(Arrays.toString(cliente));
	    datos.append(", titulosDeTareas=");
	    datos.append(Arrays.toString(titulosDeTareas));
	    datos.append(", descripcion=");
	    datos.append(Arrays.toString(descripcion));
	    datos.append(", dias=");
	    datos.append(Arrays.toString(dias));
	    datos.append(", tareas=");
	    datos.append(tareas);
	    datos.append(", numeroId=");
	    datos.append(numeroId);
	    datos.append(", direccion=");
	    datos.append(direccion);
	    datos.append(", fechaInicio=");
	    datos.append(fechaInicio);
	    datos.append(", fechaEstimada=");
	    datos.append(fechaEstimada);
	    datos.append(", fechaFinalizacion=");
	    datos.append(fechaFinalizacion);
	    datos.append(", estado=");
	    datos.append(estado);
	    datos.append(", costoFinal=");
	    datos.append(costoFinal);
	    datos.append("]");
	    return datos.toString();
	}
}
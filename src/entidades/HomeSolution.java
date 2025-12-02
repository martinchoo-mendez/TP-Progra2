package entidades;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeSolution implements IHomeSolution{
	
	Map<Integer, Empleado> empleados;
	Map<Integer, Proyecto> proyectos;
	
	public HomeSolution(){
		empleados = new HashMap <>();
		proyectos = new HashMap<>();
		Empleado.resetearUltimo();
	    Proyecto.resetearUltimo();
	}

	@Override
	public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
		if (nombre == null || nombre.trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre del empleado no puede ser nulo o vacío.");
	    }
	    if (valor < 0) {
	        throw new IllegalArgumentException("El valor del empleado no puede ser negativo. Valor proporcionado: " + valor);
	    }
		EmpleadoContratado empleado = new EmpleadoContratado(nombre, valor);
		empleados.put(empleado.verLegajo(), empleado);
	}

	@Override
	public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
		if (nombre == null || nombre.trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre del empleado no puede ser nulo o vacío.");
	    }
	    if (valor < 0) {
	        throw new IllegalArgumentException("El valor del empleado no puede ser negativo. Valor proporcionado: " + valor);
	    }
	    if(!categoria.equals("INICIAL") && !categoria.equals("TECNICO") && !categoria.equals("EXPERTO")) {
	        throw new IllegalArgumentException("La categoría, tiene que ser Inicial, Técnico o Experto");
	    }
		EmpleadoPlanta empleado = new EmpleadoPlanta(nombre, valor, categoria);
		empleados.put(empleado.verLegajo(), empleado);
	}

	@Override
	public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio,
			String[] cliente, String inicio, String fin) throws IllegalArgumentException {
		// Validación 1: Títulos, descripciones y días válidos
	    for (int i = 0; i < titulos.length; i++) {
	        if (titulos[i] == null || titulos[i].trim().isEmpty()) {
	            throw new IllegalArgumentException("No hay título en la tarea " + (i + 1));
	        }
	        if (descripcion[i] == null ) {
	            throw new IllegalArgumentException("No hay descripción en la tarea " + (i + 1));
	        }
	        if (dias[i] <= 0) {
	            throw new IllegalArgumentException("Los días estimados de la tarea " + (i + 1) + " deben ser un número positivo.");
	        }
	    }
	  
	    //Validación 2 :Domicilio
	    if (domicilio == null || domicilio.trim().isEmpty()) {
	        throw new IllegalArgumentException("Faltó ingresar un domicilio");
	    }
	    
	    // Validación 3: Cliente
	    if (cliente == null || cliente.length != 3) {
	        throw new IllegalArgumentException("El cliente debe tener exactamente 3 elementos: nombre, mail, teléfono.");
	    }
	    if (cliente[0] == null || cliente[0].trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre del cliente no puede ser null o vacío.");
	    }
	    if (cliente[1] == null || !cliente[1].contains("@")) {
	        throw new IllegalArgumentException("El mail del cliente debe ser válido (contener '@').");
	    }
	    if (cliente[2] == null || cliente[2].trim().isEmpty()) {
	        throw new IllegalArgumentException("El teléfono del cliente no puede ser null o vacío.");
	    }
	    
	    // Validación 4: Fechas (AAAA-MM-DD es igual a YYYY-MM-DD)
	    try {
	        LocalDate fechaInicio = LocalDate.parse(inicio); // Formato ISO por defecto: AAAA-MM-DD
	        LocalDate fechaFin = LocalDate.parse(fin);
	        
	        if (!fechaFin.isAfter(fechaInicio)) {
	            throw new IllegalArgumentException("La fecha de finalización debe ser posterior a la fecha de inicio.");
	        }
	    } catch (DateTimeParseException e) {
	        throw new IllegalArgumentException("Las fechas deben estar en formato AAAA-MM-DD.");
	    }

		Proyecto proyecto = new Proyecto(titulos, descripcion, dias, domicilio, cliente, inicio, fin);
		proyectos.put(proyecto.verId(), proyecto);

	}

	@Override
	public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);
		
		if(proyecto.verEstado().equals("FINALIZADO")) {
			throw new Exception ("El proyecto está terminado");
		}
		Tarea tarea = proyecto.verTarea(titulo);
		if(tarea.verFin()) {
			throw new Exception ("La tarea ya está finalizada");
		}
		if(!hayEmpleadoDisponible()) {
			throw new Exception ("No hay responsable");
		}
		
		Iterator <Empleado> iteratorEmpleado = empleados.values().iterator();
		Empleado empleado = null;
		while(iteratorEmpleado.hasNext() && empleado == null) { 
			Empleado empleadoATomar = iteratorEmpleado.next();
			if(empleadoATomar.verDisponible()) {
				empleado = empleadoATomar;
			}
		}
		proyecto.asignarEmpleadoEnTarea(empleado, titulo);
		empleado.agregarTareaHistorial(tarea);
		proyecto.actualizarEstadoActivo();
	}

	//función para usar cuando haya que asignar un empleado a una tarea
	private boolean hayEmpleadoDisponible() {
		boolean algunEmpleadoDisponible = false;
		for(Empleado empleado : empleados.values()) {
			algunEmpleadoDisponible = algunEmpleadoDisponible || empleado.verDisponible(); 
		}
		return algunEmpleadoDisponible;
	}
	
	//funcion para ver cual es el empleado con menos retraso y poder asignarlo en una tarea
	private Empleado verEmpleadoConMenosRetraso() {
		Empleado empleadoConMenosRetraso = null;

	    for (Empleado empleado : empleados.values()) {
	        if (empleado.verDisponible()) {
	            if (empleadoConMenosRetraso == null || empleado.mostrarRetrasos() < empleadoConMenosRetraso.mostrarRetrasos()) {
	                empleadoConMenosRetraso = empleado;
	            }
	        }
	    }

	    return empleadoConMenosRetraso;
	}
	
	@Override
	public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
		if(!hayEmpleadoDisponible()) {
			throw new Exception ("No hay empleado disponible");
		}
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(tarea.hayEmpleado()) {
			throw new Exception ("La tarea ya fue asignada");
		}
		if(proyecto.verEstado().equals("FINALIZADO")) {
			throw new Exception ("El proyecto está terminado");
		}
		
		Empleado empleadoConMenosRetraso = verEmpleadoConMenosRetraso();
		
		proyecto.asignarEmpleadoEnTarea(empleadoConMenosRetraso, titulo);
	}

	@Override
	public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias)
			throws IllegalArgumentException {
		if (cantidadDias <= 0) {
	        throw new IllegalArgumentException("La cantidad de días de retraso debe ser positiva.");
	    }
		Proyecto proyecto = proyectos.get(numero);
		proyecto.registrarRetrasoEnTarea(titulo, cantidadDias);
	}

	@Override
	public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
			throws IllegalArgumentException {
		Proyecto proyecto = proyectos.get(numero);
		if(proyecto.verEstado().equals("FINALIZADO")) {
			throw new IllegalArgumentException ("El proyecto está finalizado");
		}
		proyecto.agregarTareasProyecto(titulo, descripcion, dias);
	}

	@Override
	public void finalizarTarea(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(tarea.verFin()) {
			throw new Exception ("La tarea ya está finalizada");
		}
		tarea.cambiarFinalizado();
		tarea.liberarResponsable();
	}

	@Override
	public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
	    try {
	        LocalDate fechaFin = LocalDate.parse(fin);
	        
	        Proyecto proyecto = proyectos.get(numero);
	        
	        LocalDate fechaInicio = LocalDate.parse(proyecto.fechaDeInicio());
	        LocalDate fechaFinEstimada = LocalDate.parse(proyecto.fechaDeFinEstimada());
	        
	        // Validación 1: Fecha fin NO puede ser anterior a fecha inicio
	        if (fechaFin.isBefore(fechaInicio)) {
	            throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha de inicio (" +fechaInicio + ").");
	        }
	        
	        // Validación 2: Fecha fin NO puede ser anterior a fecha fin estimada
	        if (fechaFin.isBefore(fechaFinEstimada)) {
	            throw new IllegalArgumentException("La fecha de finalización real (" + fechaFin + ") no puede ser anterior a la fecha de fin estimada (" +fechaFinEstimada + ").");
	        }
	        
	        if(fechaFin.isAfter(fechaFinEstimada)) {
	        	proyecto.cambiarTuvoRetraso();
	        }
	        
	        proyecto.fechaDeFin(fin);
	        proyecto.calcularCostoFinal();
	        proyecto.ActualizarEstadoFIn();
	        proyecto.liberarEmpleados();
	        
	    } catch (DateTimeParseException e) {
	        throw new IllegalArgumentException("La fecha de finalización debe estar en formato YYYY-MM-DD.");
	    }
	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		Empleado nuevoEmpleado = empleados.get(legajo);
		if(!nuevoEmpleado.verDisponible()) {
			throw new Exception ("El empleado no está disponible");
		}
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(!tarea.hayEmpleado()) {
			throw new Exception ("La tarea no tiene empleado");
		}
		tarea.cambiarResponsable(nuevoEmpleado);
		nuevoEmpleado.agregarTareaHistorial(tarea);
	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(!tarea.hayEmpleado()) {
			throw new Exception ("La tarea no tiene empleado");
		}
		Empleado empleadoConMenosRetraso = verEmpleadoConMenosRetraso();
		tarea.cambiarResponsable(empleadoConMenosRetraso);
		empleadoConMenosRetraso.agregarTareaHistorial(tarea);
	}

	@Override
	public double costoProyecto(Integer numero) {
	    double costo = 0;
	    Proyecto proyecto = proyectos.get(numero);
	   if (proyecto.verEstado().equals("ACTIVO") || proyecto.verEstado().equals("PENDIENTE")) {
	    	proyecto.calcularCostoParcial();
	    	costo = proyecto.verCostoParcial();
	    } 
	    else if (proyecto.verEstado().equals("FINALIZADO")) {
	       costo = proyecto.verCostoFinal();
	    }
	   return costo;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {
		List<Tupla<Integer, String>> finalizados = new ArrayList <>();
		for(Proyecto proyecto : proyectos.values()) {
			if(proyecto.verEstado().equals("FINALIZADO")) {
				Tupla<Integer, String> tupla = new Tupla<Integer, String> (proyecto.verId(), proyecto.verDireccion());
				finalizados.add(tupla);
			}
		}
		return finalizados;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
	    List<Tupla<Integer, String>> pendientes = new ArrayList<>();
	    for (Proyecto proyecto : proyectos.values()) {
	        if (proyecto.verEstado().equals("PENDIENTE")) {
	        	Tupla<Integer, String> tupla = new Tupla <Integer, String> (proyecto.verId(), proyecto.verDireccion());
	            pendientes.add(tupla);
	        }
	    }
	    
	    return pendientes;
	}
	
	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
		List<Tupla<Integer, String>> activos = new ArrayList <>();
		for(Proyecto proyecto : proyectos.values()) {
			if(proyecto.verEstado().equals("ACTIVO")) {
				Tupla<Integer, String> tupla = new Tupla<Integer, String> (proyecto.verId(), proyecto.verDireccion());
				activos.add(tupla);
			}
		}
		return activos;
	}

	@Override
	public Object[] empleadosNoAsignados() {
		List<Integer> noAsignados = new ArrayList<>();
		for(Empleado empleado : empleados.values()) {
			if(empleado.verDisponible()) {
				noAsignados.add(empleado.verLegajo());
			}
		}
		return noAsignados.toArray();
	}

	@Override
	public boolean estaFinalizado(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		if(proyecto.verEstado().equals("FINALIZADO")) {
			return true;
		}
		return false;
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {
		Empleado empleado = empleados.get(legajo);
		return empleado.mostrarRetrasos();
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		return proyecto.empleadosDelProyecto();
	} //consultar si esto está bien

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		if (proyecto.verEstado().equals("FINALIZADO")) {
	        throw new IllegalArgumentException("No se pueden consultar tareas no asignadas en un proyecto finalizado.");
	    }
		return proyecto.tareasSinEmpleado();
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		Set<Tarea> tareasDelProyecto = proyecto.verTareas();
		return tareasDelProyecto.toArray();
	}

	@Override
	public String consultarDomicilioProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		String direccion = proyecto.verDireccion();
		return direccion;
	}

	@Override
	public boolean tieneRestrasos(Integer legajo) {
		Empleado empleado = empleados.get(legajo);
		if(empleado.tuvoTareasConRetrasos()) {
			return true;
		}
		return false;
	}

	@Override
	public List<Tupla<Integer, String>> empleados() {
		List <Tupla<Integer, String>> empleadosDeHomeSolution = new ArrayList<>();
		for(Empleado empleado : empleados.values()) {
			Tupla<Integer, String> tupla = new Tupla<Integer, String> (empleado.verLegajo(), empleado.verNombre());
			empleadosDeHomeSolution.add(tupla);
		}
		return empleadosDeHomeSolution;
	}

	@Override
	public String consultarProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);
		String informacion = proyecto.toString();
		return informacion;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Proyecto proyecto: proyectos.values()) {
			sb.append(proyecto.toString());
		}
		sb.append("\n");
		for(Empleado empleado: empleados.values()) {
			sb.append(empleado.toString());
		}
		return sb.toString();
	}
	
}
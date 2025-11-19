package entidades;

import java.util.ArrayList;
import java.util.Comparator;
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
		// Validación 1: Nombre no nulo ni vacío
	    if (nombre == null || nombre.trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre del empleado no puede ser nulo o vacío.");
	    }
	    // Validación 2: Valor no negativo
	    if (valor < 0) {
	        throw new IllegalArgumentException("El valor del empleado no puede ser negativo. Valor proporcionado: " + valor);
	    }
		EmpleadoContratado empleado = new EmpleadoContratado(nombre, valor);
		empleados.put(empleado.verLegajo(), empleado);
	}

	@Override
	public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
		// Validación 1: Nombre no nulo ni vacío
	    if (nombre == null || nombre.trim().isEmpty()) {
	        throw new IllegalArgumentException("El nombre del empleado no puede ser nulo o vacío.");
	    }
	    // Validación 2: Valor no negativo
	    if (valor < 0) {
	        throw new IllegalArgumentException("El valor del empleado no puede ser negativo. Valor proporcionado: " + valor);
	    }
	    categoria = categoria.toUpperCase();
	    if(!categoria.equals("INICIAL") && !categoria.equals("TECNICO") && !categoria.equals("EXPERTO")) {
	        throw new IllegalArgumentException("La categoría, tiene que ser Inicial, Técnico o Experto");
	    }
		EmpleadoPlanta empleado = new EmpleadoPlanta(nombre, valor, categoria);
		empleados.put(empleado.verLegajo(), empleado);
	}

	@Override
	public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio,
			String[] cliente, String inicio, String fin) throws IllegalArgumentException {
		// Validación 1: Arrays no null y tamaños consistentes
	    if (titulos == null || descripcion == null || dias == null) {
	        throw new IllegalArgumentException("Los arrays de títulos, descripciones y días no pueden ser null.");
	    }
	    if (titulos.length != descripcion.length || titulos.length != dias.length || titulos.length == 0) {
	        throw new IllegalArgumentException("Los arrays de títulos, descripciones y días deben tener la misma longitud positiva.");
	    }
	    
	    // Validación 2: Títulos, descripciones y días válidos
	    for (int i = 0; i < titulos.length; i++) {
	        if (titulos[i] == null || titulos[i].trim().isEmpty()) {
	            throw new IllegalArgumentException("El título de la tarea " + (i + 1) + " no puede ser null o vacío.");
	        }
	        if (descripcion[i] == null ) {
	            throw new IllegalArgumentException("La descripción de la tarea " + (i + 1) + " no puede ser null o vacía.");
	        }
	        if (dias[i] <= 0) {
	            throw new IllegalArgumentException("Los días estimados de la tarea " + (i + 1) + " deben ser un número positivo.");
	        }
	    }
	      
	    // Validación 3: Domicilio
	    if (domicilio == null || domicilio.trim().isEmpty()) {
	        throw new IllegalArgumentException("El domicilio no puede ser null o vacío.");
	    }
	    
	 // Validación 4: Cliente
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
	    
	    // Validación 5: Fechas (AAAA-MM-DD es igual a YYYY-MM-DD)
	    String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
	    if (!inicio.matches(datePattern)) {
	        throw new IllegalArgumentException("La fecha de inicio debe estar en formato AAAA-MM-DD.");
	    }
	    if (!fin.matches(datePattern)) {
	        throw new IllegalArgumentException("La fecha de finalización debe estar en formato AAAA-MM-DD.");
	    }
	    if (fin.compareTo(inicio) <= 0) {
	        throw new IllegalArgumentException("La fecha de finalización debe ser posterior a la fecha de inicio.");
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

	
	public boolean hayEmpleadoDisponible() {
		boolean algunEmpleadoDisponible = false;
		for(Empleado empleado : empleados.values()) {
			algunEmpleadoDisponible = algunEmpleadoDisponible || empleado.verDisponible(); 
		}
		return algunEmpleadoDisponible;
	}
	
	public Empleado verEmpleadoConMenosRetraso() {
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
	        throw new IllegalArgumentException("La cantidad de días de retraso debe ser positiva. Valor proporcionado: " + cantidadDias);
	    }
		System.out.println("Registrando retrasos en proyecto: " + numero);
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		Empleado empleado = tarea.verResponsable();
		empleado.aumentarRetrasos(); //empleado necesita registrar cantidad de retrasos, no cantidad de días
		proyecto.registrarRetrasoEnTarea(titulo, cantidadDias);
	}

	@Override
	public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
			throws IllegalArgumentException {
		Proyecto proyecto = proyectos.get(numero);
		if(proyecto.verEstado().equals("FINALIZADO")) {
			throw new IllegalArgumentException ("El proyecto está finalizado");
		}
		proyecto.agregarTareasProyectoExistente(titulo, descripcion, dias);
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
		// Validación 1: Fecha en formato válido
	    String datePattern = "^\\d{4}-\\d{2}-\\d{2}$";
	    if (!fin.matches(datePattern)) {
	        throw new IllegalArgumentException("La fecha de finalización debe estar en formato YYYY-MM-DD.");
	    }
	    
	    // Validación 2: Fecha fin posterior a fecha inicio
	    Proyecto proyecto = proyectos.get(numero);
	    String fechaInicio = proyecto.fechaDeInicio();
	    if (fin.compareTo(fechaInicio) < 0) {
	        throw new IllegalArgumentException("La fecha de finalización no puede ser anterior a la fecha de inicio (" + fechaInicio + ").");
	    }
	    String fechaFinEstimada = proyecto.fechaDeFinEstimada(); // Asumo este getter existe en Proyecto
	    
	    if (fin.compareTo(fechaFinEstimada) < 0) {
	        throw new IllegalArgumentException("La fecha de finalización real (" + fin + 
	                                           ") no puede ser anterior a la fecha de fin estimada (" + fechaFinEstimada + ").");
	    }
	    proyecto.fechaDeFin(fin);
//	    proyecto.calcularCostoFinal();
	    proyecto.calcularCostoFinal();
	    proyecto.ActualizarEstadoFIn();
	    proyecto.liberarEmpleados();
	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		if(!hayEmpleadoDisponible()) {
			throw new Exception ("No hay empleado disponible");
		}
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(!tarea.hayEmpleado()) {
			throw new Exception ("La tarea no tiene empleado");
		}
		Empleado nuevoEmpleado = empleados.get(legajo);
		tarea.cambiarResponsable(nuevoEmpleado);
		nuevoEmpleado.agregarTareaHistorial(tarea);
	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		if(!hayEmpleadoDisponible()) {
			throw new Exception ("No hay empleado disponible");
		}
		Proyecto proyecto = proyectos.get(numero);
		Tarea tarea = proyecto.verTarea(titulo);
		if(!tarea.hayEmpleado()) {
			throw new Exception ("La tarea no tiene empleado");
		}
		Empleado empleadoConMenosRetraso = verEmpleadoConMenosRetraso();
		tarea.cambiarResponsable(empleadoConMenosRetraso);
		empleadoConMenosRetraso.agregarTareaHistorial(tarea);  // AGREGAR ESTA LÍNEA
	}

	@Override
	public double costoProyecto(Integer numero) {
	    double costo = 0;
	    Proyecto proyecto = proyectos.get(numero);
	    System.out.println("Calculando costo para proyecto: " + numero);
	   if (proyecto.verEstado().equals("ACTIVO") || proyecto.verEstado().equals("PENDIENTE")) {
	    	proyecto.calcularCostoParcial();
	    	costo = proyecto.verCostoParcial();
	    } 
	    else if (proyecto.verEstado().equals("FINALIZADO")) {
	       costo = proyecto.verCostoFinal();
	    }
	  //  System.out.println("Costo del proyecto " + "con estado " + proyecto.verEstado() + " es: " + costo);
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
	    List<Proyecto> pendientes = new ArrayList<>();
	    
	    // Filtrar proyectos pendientes
	    for (Proyecto p : proyectos.values()) {
	        if (p.verEstado().equals("PENDIENTE")) {
	            pendientes.add(p);
	        }
	    }
	    
	    // Ordenar por número de proyecto ascendente
	    pendientes.sort(Comparator.comparingInt(Proyecto::verId));
	    
	    // Convertir a lista de Tupla<Integer, String>
	    List<Tupla<Integer, String>> resultado = new ArrayList<>();
	    for (Proyecto p : pendientes) {
	        resultado.add(new Tupla<>(p.verId(), p.verDireccion()));
	    }
	    
	    return resultado;
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
		return "HomeSolution [empleados=" + empleados + ", proyectos=" + proyectos + "]";
	}
	
}
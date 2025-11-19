package entidades;

public class EmpleadoContratado extends Empleado {
	
	private double valorHora;
	
	//acá también consultar si hace falta el constructor vacio
	
	public EmpleadoContratado(String nombre, double valor) {
		super(nombre); //constructor heredado
		this.valorHora = valor;
	}
	
	public double calcularCosto(double diasNecesarios) {
		double costo = 0;
		if(diasNecesarios == .5) {
			costo = valorHora * 4;
		}
		else {
			costo = valorHora * 8 * diasNecesarios;
		}
		return costo;
	}
	
}

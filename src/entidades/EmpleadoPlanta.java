package entidades;


public class EmpleadoPlanta extends Empleado {
	private double valorDia;
	private String categoria;
	
	public EmpleadoPlanta(String nombre, double valor, String categoria) {
		super(nombre);
		this.valorDia = valor;
		this.categoria = categoria;
	}
	
	public double calcularCosto(double diasNecesarios) {
	    double costo = 0;
	    if (diasNecesarios == 0.5) {
	        costo = valorDia * 1; 
	    } else {
	        costo = valorDia * diasNecesarios;
	    }
	    return costo;
	}
	
	
}

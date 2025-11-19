package entidades;


public class EmpleadoPlanta extends Empleado {
	private double valorDia;
	private String categoria;
	
	//preguntar si hace falta el contructor vacio
	
	public EmpleadoPlanta(String nombre, double valor, String categoria) {
		super(nombre); //constructor heredado
		this.valorDia = valor;
		this.categoria = categoria;
	}
	
	public double calcularCosto(double diasNecesarios) {
	    //double bono = 1.02;
	    double costo = 0;
	    if (diasNecesarios == 0.5) {
	        costo = valorDia * 1; //* bono;  // Medio día = día completo para planta
	    } else {
	        costo = valorDia * diasNecesarios;// * bono;
	    }
	    return costo;
	}
	
	
}

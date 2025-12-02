package entidades;


public class EmpleadoPlanta extends Empleado {
	private double valorDia;
	private String categoria;
	
	public EmpleadoPlanta(String nombre, double valor, String categoria) {
		super(nombre);
		this.valorDia = valor;
		this.categoria = categoria;
	}

	@Override
	public double calcularCosto(double diasNecesarios) {
		double bono = 1.02; //agregado ahora
	    double costo = 0;
	    if (diasNecesarios == 0.5) {
	        costo = valorDia * bono;; 
	    } else {
	        costo = valorDia * diasNecesarios * 1.02;
	    }
	    return costo;
	}

	@Override
	public String toString() {
		StringBuilder datos = new StringBuilder();
		datos.append(super.toString());
		datos.append("Valor por día = ");
		datos.append(valorDia);
		datos.append("Categoría = ");
		datos.append(categoria);
		return datos.toString();
	}	
	
}

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
	    double costo = 0;
	    if (diasNecesarios == 0.5) {
	        costo = valorDia * 1; 
	    } else {
	        costo = valorDia * diasNecesarios;
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

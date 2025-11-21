package entidades;

public class EmpleadoContratado extends Empleado {
	
	private double valorHora;
	
	public EmpleadoContratado(String nombre, double valor) {
		super(nombre); 
		this.valorHora = valor;
	}

	@Override
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

	@Override
	public String toString() {
		StringBuilder datos = new StringBuilder();
		datos.append(super.toString());
		datos.append(", Valor por hora = ");
		datos.append(valorHora);
		datos.append("]");
		return datos.toString();
	}
	
	
}

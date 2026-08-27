package Dto;

public class DtoResumenDevolucion {
    
    private String totalActualRecibo;
    private String  metodoPago;
    private String totalDevolver;
    private String metodoDevolucion;

    public DtoResumenDevolucion(String totalActualRecibo, String metodoPago, String totalDevolver, String metodoDevolucion) {
        this.totalActualRecibo = totalActualRecibo;
        this.metodoPago = metodoPago;
        this.totalDevolver = totalDevolver;
        this.metodoDevolucion = metodoDevolucion;
    }

    public DtoResumenDevolucion() {
    }

    public String getTotalActualRecibo() {
        return totalActualRecibo;
    }

    public void setTotalActualRecibo(String totalActualRecibo) {
        this.totalActualRecibo = totalActualRecibo;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTotalDevolver() {
        return totalDevolver;
    }

    public void setTotalDevolver(String totalDevolver) {
        this.totalDevolver = totalDevolver;
    }

    public String getMetodoDevolucion() {
        return metodoDevolucion;
    }

    public void setMetodoDevolucion(String metodoDevolucion) {
        this.metodoDevolucion = metodoDevolucion;
    }

}

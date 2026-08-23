package Dto;

public class DtoItemHistorialabonos {
    private Long idAbono;
    private Long idRecibo;
    private String fecha;
    private String monto;
    private String formaPago;

    public DtoItemHistorialabonos(Long idAbono, Long idRecibo, String fecha, String monto, String formaPago) {
        this.idAbono = idAbono;
        this.idRecibo = idRecibo;
        this.fecha = fecha;
        this.monto = monto;
        this.formaPago = formaPago;
    }

    public DtoItemHistorialabonos() {
    }

    public Long getIdAbono() {
        return idAbono;
    }

    public void setIdAbono(Long idAbono) {
        this.idAbono = idAbono;
    }

    public Long getIdRecibo() {
        return idRecibo;
    }

    public void setIdRecibo(Long idRecibo) {
        this.idRecibo = idRecibo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    
}

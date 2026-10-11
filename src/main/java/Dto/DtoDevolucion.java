package Dto;

import java.math.BigDecimal;
import java.util.List;

public class DtoDevolucion {

    private Long idDevolucion;
    private Long idVenta;
    
    private String fecha;
    private String cliente;
    private String nit;
    private String motivo;
    
    private BigDecimal totalDevolver;
    
    private List<DtoDetallesDevolucion> detallesDevolucion;

    public DtoDevolucion(Long idDevolucion, Long idVenta, String fecha, String cliente, String nit, String motivo, BigDecimal totalDevolver, List<DtoDetallesDevolucion> detallesDevolucion) {
        this.idDevolucion = idDevolucion;
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.cliente = cliente;
        this.nit = nit;
        this.motivo = motivo;
        this.totalDevolver = totalDevolver;
        this.detallesDevolucion = detallesDevolucion;
    }

    public DtoDevolucion() {
    }

    public Long getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Long idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getTotalDevolver() {
        return totalDevolver;
    }

    public void setTotalDevolver(BigDecimal totalDevolver) {
        this.totalDevolver = totalDevolver;
    }

    public List<DtoDetallesDevolucion> getDetallesDevolucion() {
        return detallesDevolucion;
    }

    public void setDetallesDevolucion(List<DtoDetallesDevolucion> detallesDevolucion) {
        this.detallesDevolucion = detallesDevolucion;
    }
    
}

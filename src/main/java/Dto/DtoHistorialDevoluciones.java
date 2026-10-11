package Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DtoHistorialDevoluciones {
    
    private Long id;
    private Long idVenta;
    private LocalDateTime fecha;
    private String nitCliente;
    private String nombreCliente;
    private BigDecimal totalDevuelto;
    private String tipo;

    public DtoHistorialDevoluciones(Long id, Long idVenta, LocalDateTime fecha, String nitCliente, String nombreCliente, BigDecimal totalDevuelto, String tipo) {
        this.id = id;
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.nitCliente = nitCliente;
        this.nombreCliente = nombreCliente;
        this.totalDevuelto = totalDevuelto;
        this.tipo = tipo;
    }

    public DtoHistorialDevoluciones() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public BigDecimal getTotalDevuelto() {
        return totalDevuelto;
    }

    public void setTotalDevuelto(BigDecimal totalDevuelto) {
        this.totalDevuelto = totalDevuelto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    
}

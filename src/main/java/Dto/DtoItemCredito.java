package Dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DtoItemCredito {
    
    
    private Long id;
    private Long idVenta;
    private LocalDate fechaEmision;
    private LocalDate plazoPago;
    private BigDecimal montoTotal;
    private BigDecimal saldoPendiente;
    private String estado; 

    public DtoItemCredito(Long id, Long idVenta, LocalDate fechaEmision, LocalDate plazoPago, BigDecimal montoTotal, BigDecimal saldoPendiente, String estado) {
        this.id = id;
        this.idVenta = idVenta;
        this.fechaEmision = fechaEmision;
        this.plazoPago = plazoPago;
        this.montoTotal = montoTotal;
        this.saldoPendiente = saldoPendiente;
        this.estado = estado;
    }

    public DtoItemCredito() {
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

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getPlazoPago() {
        return plazoPago;
    }

    public void setPlazoPago(LocalDate plazoPago) {
        this.plazoPago = plazoPago;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}

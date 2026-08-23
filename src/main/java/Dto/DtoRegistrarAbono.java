package Dto;

import java.math.BigDecimal;

public class DtoRegistrarAbono {
    private Long idCredito;
    private Long idTurno;
    private Long idVendedor;
    private BigDecimal montoAbonado;
    private String formaPago;
    private String noDeposito;

    public DtoRegistrarAbono(Long idCredito, Long idTurno, Long idVendedor, BigDecimal montoAbonado, String formaPago, String noDeposito) {
        this.idCredito = idCredito;
        this.idTurno = idTurno;
        this.idVendedor = idVendedor;
        this.montoAbonado = montoAbonado;
        this.formaPago = formaPago;
        this.noDeposito = noDeposito;
    }

    public DtoRegistrarAbono() {
    }

    public Long getIdCredito() {
        return idCredito;
    }

    public void setIdCredito(Long idCredito) {
        this.idCredito = idCredito;
    }

    public Long getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(Long idTurno) {
        this.idTurno = idTurno;
    }

    public BigDecimal getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(BigDecimal montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public String getNoDeposito() {
        return noDeposito;
    }

    public void setNoDeposito(String noDeposito) {
        this.noDeposito = noDeposito;
    }
    
    public Long getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Long idVendedor) {
        this.idVendedor = idVendedor;
    } 
}

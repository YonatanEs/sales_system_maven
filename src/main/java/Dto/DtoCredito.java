package Dto;

import java.math.BigDecimal;

public class DtoCredito {
    
    private String nombreCliente;
    private String nitCliente;
    private BigDecimal deudaTotal;
    private String estadoGeneral;
    private RespuestaPaginada<DtoItemCredito> listCredito;

    public DtoCredito(String nombreCliente, String nitCliente, BigDecimal deudaTotal, String estadoGeneral, RespuestaPaginada<DtoItemCredito> listCredito) {
        this.nombreCliente = nombreCliente;
        this.nitCliente = nitCliente;
        this.deudaTotal = deudaTotal;
        this.estadoGeneral = estadoGeneral;
        this.listCredito = listCredito;
    }

    public DtoCredito() {
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }

    public BigDecimal getDeudaTotal() {
        return deudaTotal;
    }

    public void setDeudaTotal(BigDecimal deudaTotal) {
        this.deudaTotal = deudaTotal;
    }

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public RespuestaPaginada<DtoItemCredito> getListCredito() {
        return listCredito;
    }

    public void setListCredito(RespuestaPaginada<DtoItemCredito> listCredito) {
        this.listCredito = listCredito;
    }
    
}

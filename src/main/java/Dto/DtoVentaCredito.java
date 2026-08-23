package Dto;

import Objects.Pedido_ob;
import java.math.BigDecimal;
import java.util.List;

public class DtoVentaCredito {

    private String nombreCliente;
    private String nitCliente;
    
    private List<DtoPedido> listaPedidos;
    
    private String metodoPago;
    private BigDecimal totalApagar;
    private int idVendedor;
    private int idTurno;
    private String plazoPago;
    
    public DtoVentaCredito(String nombreCliente, String nitCliente,
            List<DtoPedido> listaPedidos, String metodoPago, BigDecimal totalApagar,
            int idVendedor, int idTurno, String plazoPago) {
        this.nombreCliente = nombreCliente;
        this.nitCliente = nitCliente;
        this.listaPedidos = listaPedidos;
        this.metodoPago = metodoPago;
        this.totalApagar = totalApagar;
        this.idVendedor = idVendedor;
        this.idTurno = idTurno;
        this.plazoPago = plazoPago;
    }

    public DtoVentaCredito(List<DtoPedido> listaPedidos, BigDecimal totalApagar) {
        this.listaPedidos = listaPedidos;
        this.totalApagar = totalApagar;
    }
    
    public DtoVentaCredito(){
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

    public List<DtoPedido> getListaPedidos() {
        return listaPedidos;
    }

    public void setListaPedidos(List<DtoPedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotalApagar() {
        return totalApagar;
    }

    public void setTotalApagar(BigDecimal totalApagar) {
        this.totalApagar = totalApagar;
    }

    public int getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(int idVendedor) {
        this.idVendedor = idVendedor;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getPlazoPago() {
        return plazoPago;
    }

    public void setPlazoPago(String plazoPago) {
        this.plazoPago = plazoPago;
    }

}

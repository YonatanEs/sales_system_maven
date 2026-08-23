package Dto;

import Objects.Usuario_Ob;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DtoRecibo {
    
    private int id;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String nitCliente;
    private String nombreCliente;
    private Usuario_Ob vendedor;
    private String metodoPago;
    private String estado;
    private List<DtoDetallesVenta> detallesVenta;

    public DtoRecibo(int id, LocalDateTime fecha, BigDecimal total, String nitCliente, String nombreCliente, Usuario_Ob vendedor, String metodoPago, String estado, List<DtoDetallesVenta> detallesVenta) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.nitCliente = nitCliente;
        this.nombreCliente = nombreCliente;
        this.vendedor = vendedor;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.detallesVenta = detallesVenta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public Usuario_Ob getVendedor() {
        return vendedor;
    }

    public void setVendedor(Usuario_Ob vendedor) {
        this.vendedor = vendedor;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<DtoDetallesVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DtoDetallesVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }
    
}

package Dto;

import java.math.BigDecimal;

public class DtoPedido {

    private int id_producto;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal descuentos;
    private BigDecimal preciofinal;
    private BigDecimal subtotal;

    public DtoPedido(int id_producto, String codigo, String descripcion, BigDecimal cantidad, BigDecimal precio, BigDecimal descuentos, BigDecimal preciofinal, BigDecimal subtotal) {
        this.id_producto = id_producto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuentos = descuentos;
        this.preciofinal = preciofinal;
        this.subtotal = subtotal;
    }
    
    public DtoPedido(){
    }

    public int getId_producto() {
        return id_producto;
    }

    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
    }

    public BigDecimal getPreciofinal() {
        return preciofinal;
    }

    public void setPreciofinal(BigDecimal preciofinal) {
        this.preciofinal = preciofinal;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
}

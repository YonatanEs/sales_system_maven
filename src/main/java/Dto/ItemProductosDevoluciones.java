package Dto;

import java.math.BigDecimal;

public class ItemProductosDevoluciones {
    
    private Long idProducto;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal cantidadadevolver;

    public ItemProductosDevoluciones(Long idProducto, String codigo, String descripcion, BigDecimal cantidad, BigDecimal precio, BigDecimal cantidadadevolver) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.cantidadadevolver = cantidadadevolver;
    }

    public ItemProductosDevoluciones() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
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

    public BigDecimal getCantidadadevolver() {
        return cantidadadevolver;
    }

    public void setCantidadadevolver(BigDecimal cantidadadevolver) {
        this.cantidadadevolver = cantidadadevolver;
    }
    
}

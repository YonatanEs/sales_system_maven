package Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DtoAddStock {
    private int id;
    private LocalDateTime fechaEntrada;
    private BigDecimal stock;
    private BigDecimal precioCompra;
    private String concepto;
   

    public DtoAddStock(int id, LocalDateTime fechaEntrada, BigDecimal stock, BigDecimal precioCompra, String concepto) {
        this.id = id;
        this.fechaEntrada = fechaEntrada;
        this.stock = stock;
        this.precioCompra = precioCompra;
        this.concepto = concepto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(LocalDateTime fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

}

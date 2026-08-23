package Dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DtoRemoveStock {

    private int idProducto;
    private LocalDateTime fechaSalida;
    private BigDecimal stock;
    private String concepto;

    public DtoRemoveStock(int idProducto, LocalDateTime fechaSalida, BigDecimal stock, String concepto) {
        this.idProducto = idProducto;
        this.fechaSalida = fechaSalida;
        this.stock = stock;
        this.concepto = concepto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
    
}

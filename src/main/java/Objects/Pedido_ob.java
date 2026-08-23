package Objects;

import Dto.DtoPedido;
import java.math.BigDecimal;

public class Pedido_ob {

    private int id_producto;
    private String codigo;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal descuentos = new BigDecimal("0.00");
    private BigDecimal preciofinal;
    private BigDecimal subtotal;

    public Pedido_ob(int id_producto, String codigo, String descripcion, BigDecimal cantidad, BigDecimal precio) {
        this.id_producto = id_producto;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        recalcularSubtotal();
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
        recalcularSubtotal();
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
        recalcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public BigDecimal getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(BigDecimal descuentos) {
        this.descuentos = descuentos;
        recalcularSubtotal();
    }

    public BigDecimal getPreciofinal() {
        return preciofinal;
    }

    private void recalcularSubtotal() {
        this.preciofinal = precio.subtract(descuentos);
        if (this.preciofinal.compareTo(BigDecimal.ZERO) < 0) {
            this.preciofinal = new BigDecimal(0.00);
        }

        this.subtotal = cantidad.multiply(preciofinal);
    }

    public boolean isDescuentoAplicado(){
        return this.descuentos.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public DtoPedido toDTO(){
        DtoPedido dto = new DtoPedido( 
                id_producto,
                codigo,
                descripcion,
                cantidad,
                precio,
                descuentos,
                preciofinal,
                subtotal);
        return dto;
    }
    
}

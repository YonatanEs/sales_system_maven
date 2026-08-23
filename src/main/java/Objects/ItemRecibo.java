package Objects;

import Dto.DtoDetallesVenta;
import java.math.RoundingMode;

public class ItemRecibo {

    private String codigo;
    private String descripcion;
    private String cantidad;
    private String precio;
    private String subtotal;

    public ItemRecibo(DtoDetallesVenta dt){
        this.codigo=dt.getCodigo();
        this.descripcion=dt.getDescripcion();
        this.cantidad=dt.getCantidad().stripTrailingZeros().toPlainString();
        this.precio="Q "+dt.getPrecio().setScale(2, RoundingMode.HALF_UP).toPlainString();
        this.subtotal ="Q "+dt.getPrecio().multiply(dt.getCantidad()).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
    
    public ItemRecibo(String codigo, String descripcion, String cantidad, String precio, String subtotal) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
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

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }
    
}

package Objects;

import java.math.BigDecimal;

public class ProductoModificar {
    
    private int id;
    private String codigo;
    private String descripcion;
    private BigDecimal precio_venta;
    private int id_proveedor;
    private int id_medida;
    private int id_categoria;

    public ProductoModificar(int id, String codigo, String descripcion, BigDecimal precio_venta, int id_proveedor, int id_medida, int id_categoria) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio_venta = precio_venta;
        this.id_proveedor = id_proveedor;
        this.id_medida = id_medida;
        this.id_categoria = id_categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public BigDecimal getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(BigDecimal precio_venta) {
        this.precio_venta = precio_venta;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public int getId_medida() {
        return id_medida;
    }

    public void setId_medida(int id_medida) {
        this.id_medida = id_medida;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }
    
    
}

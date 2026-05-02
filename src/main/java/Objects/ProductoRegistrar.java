package Objects;

public class ProductoRegistrar {

    private String codigo;
    private String descripcion;
    private double precio_compra;
    private double precio_venta;
    private int id_proveedor;
    private int id_medida;
    private int id_categoria;

    public ProductoRegistrar(String codigo, String descripcion, double precio_compra, double precio_venta, int id_proveedor, int id_medida, int id_categoria) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio_compra = precio_compra;
        this.precio_venta = precio_venta;
        this.id_proveedor = id_proveedor;
        this.id_medida = id_medida;
        this.id_categoria = id_categoria;
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

    public double getPrecio_compra() {
        return precio_compra;
    }

    public void setPrecio_compra(double precio_compra) {
        this.precio_compra = precio_compra;
    }

    public double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(double precio_venta) {
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

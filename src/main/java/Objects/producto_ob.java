package Objects;

public class Producto_ob {
    
    
    private int id;
    private String codigo;
    private String descripcion;
    private double stock;
    private double precio_venta;
    private double precio_compra;
    private Categoria_ob categoria_ob;
    private Medida_ob medida_ob;
    private Proveedor_ob proveedor_ob;
    private String estado;

    public Producto_ob(int id, String codigo, String descripcion, double stock, double precio_venta, double precio_compra, Categoria_ob categoria_ob, Medida_ob medida_ob, Proveedor_ob proveedor_ob, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio_venta = precio_venta;
        this.precio_compra = precio_compra;
        this.categoria_ob = categoria_ob;
        this.medida_ob = medida_ob;
        this.proveedor_ob = proveedor_ob;
        this.estado = estado;
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

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(double precio_venta) {
        this.precio_venta = precio_venta;
    }

    public double getPrecio_compra() {
        return precio_compra;
    }

    public void setPrecio_compra(double precio_compra) {
        this.precio_compra = precio_compra;
    }

    public Categoria_ob getCategoria_ob() {
        return categoria_ob;
    }

    public void setCategoria_ob(Categoria_ob categoria_ob) {
        this.categoria_ob = categoria_ob;
    }

    public Medida_ob getMedida_ob() {
        return medida_ob;
    }

    public void setMedida_ob(Medida_ob medida_ob) {
        this.medida_ob = medida_ob;
    }

    public Proveedor_ob getProveedor_ob() {
        return proveedor_ob;
    }

    public void setProveedor_ob(Proveedor_ob proveedor_ob) {
        this.proveedor_ob = proveedor_ob;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
}

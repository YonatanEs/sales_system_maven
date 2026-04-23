package Objects;

public class Producto_ob {
    
    
    private int id;
    private String codigo;
    private String descripcion;
    private double stock;
    private double precio;
    private double precio_por_mayor;
    private int id_categoria;
    private String categoria;
    private int id_medida;
    private String medida;
    private int id_proveedor;
    private String proveedor;
    private String estado;

    public Producto_ob(int id, String codigo, String descripcion, double stock, double precio, double precio_por_mayor, int id_categoria, String categoria, int id_medida, String medida, int id_proveedor, String proveedor, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.precio = precio;
        this.precio_por_mayor = precio_por_mayor;
        this.id_categoria = id_categoria;
        this.categoria = categoria;
        this.id_medida = id_medida;
        this.medida = medida;
        this.id_proveedor = id_proveedor;
        this.proveedor = proveedor;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getPrecio_por_mayor() {
        return precio_por_mayor;
    }

    public void setPrecio_por_mayor(double precio_por_mayor) {
        this.precio_por_mayor = precio_por_mayor;
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getId_medida() {
        return id_medida;
    }

    public void setId_medida(int id_medida) {
        this.id_medida = id_medida;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public int getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(int id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

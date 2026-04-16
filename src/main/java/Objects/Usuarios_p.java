package Objects;

public class Usuarios_p {
    
    private long id;
    private String nombre;
    private String telefono;
    private String username;
    private String permisos;
    private String estado;
    
    public Usuarios_p(long id,String nombre, String telefono, String username, String permisos, String estado){
        this.id=id;
        this.nombre=nombre;
        this.telefono=telefono;
        this.username=username;
        this.permisos=permisos;
        this.estado=estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}

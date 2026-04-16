package Objects;

public class UsuarioModificar {

    
    private long id;
    private String nombre;
    private String telefono;
    private String username;
    private String password;
    private String permisos;
    
    private boolean actualizarPassword;

    public UsuarioModificar(long id, String nombre, String telefono, String username, String password, String permisos, boolean actualizarPassword) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.username = username;
        this.password = password;
        this.permisos = permisos;
        this.actualizarPassword = actualizarPassword;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }

    public boolean isActualizarPassword() {
        return actualizarPassword;
    }

    public void setActualizarPassword(boolean actualizarPassword) {
        this.actualizarPassword = actualizarPassword;
    }
    
}

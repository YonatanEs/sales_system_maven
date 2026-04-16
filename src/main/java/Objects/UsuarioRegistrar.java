package Objects;

public class UsuarioRegistrar {

    private String nombre;
    private String telefono;
    private String username;
    private String password;
    private String permisos;

    public UsuarioRegistrar(String nombre, String telefono, String username, String password, String permisos) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.username = username;
        this.password = password;
        this.permisos = permisos;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
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
    
}

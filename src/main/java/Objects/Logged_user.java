package Objects;

public class Logged_user {

    private int id;
    private String nombre;
    private String username;
    private String permisos;

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }


    public Logged_user(int id, String nombre, String username,String permisos) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.permisos = permisos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }    
}

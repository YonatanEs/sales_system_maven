package Objects;

import javax.swing.ImageIcon;

public class DatosEmpresaOb {

    private String nombre;
    private String nit;
    private String telefono;
    private String direccion;
    private String Slogan;
    private ImageIcon logo;

    public DatosEmpresaOb(String nombre, String nit, String telefono, String direccion, String Slogan, ImageIcon logo) {
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
        this.Slogan = Slogan;
        this.logo = logo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSlogan() {
        return Slogan;
    }

    public void setSlogan(String Slogan) {
        this.Slogan = Slogan;
    }

    public ImageIcon getLogo() {
        return logo;
    }

    public void setLogo(ImageIcon logo) {
        this.logo = logo;
    }
    
}

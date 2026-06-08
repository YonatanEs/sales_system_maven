package Dto;

public class Dto_cambiarcontraseña {
    
    private int idUserAuth;
    private String passwordActual;
    private String passwordNuevo;

    public Dto_cambiarcontraseña(int idUserAuth, String passwordActual, String passwordNuevo) {
        this.idUserAuth = idUserAuth;
        this.passwordActual = passwordActual;
        this.passwordNuevo = passwordNuevo;
    }

    public int getIdUserAuth() {
        return idUserAuth;
    }

    public void setIdUserAuth(int idUserAuth) {
        this.idUserAuth = idUserAuth;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNuevo() {
        return passwordNuevo;
    }

    public void setPasswordNuevo(String passwordNuevo) {
        this.passwordNuevo = passwordNuevo;
    }
    
}

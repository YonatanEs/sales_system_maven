package Dto;

public class Dto_cajaSelected_abierta {

    private boolean abierta;
    private String message;
    private int idTurno;

    public Dto_cajaSelected_abierta(boolean abierta, String message, int idTurno) {
        this.abierta = abierta;
        this.message = message;
        this.idTurno = idTurno;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isAbierta() {
        return abierta;
    }

    public void setAbierta(boolean abierta) {
        this.abierta = abierta;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }
    
}

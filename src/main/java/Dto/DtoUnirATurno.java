package Dto;

public class DtoUnirATurno {

    private int idTurno;
    private int idUsuario;

    public DtoUnirATurno(int idTurno, int idUsuario) {
        this.idTurno = idTurno;
        this.idUsuario = idUsuario;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
}

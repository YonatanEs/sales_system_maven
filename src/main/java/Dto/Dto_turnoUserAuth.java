package Dto;

public class Dto_turnoUserAuth {

    private int idTurnoActivo;
    private boolean masterCaja;

    public Dto_turnoUserAuth(int idTurnoActivo, boolean masterCaja) {
        this.idTurnoActivo = idTurnoActivo;
        this.masterCaja = masterCaja;
    }

    public int getIdTurnoActivo() {
        return idTurnoActivo;
    }

    public void setIdTurnoActivo(int idTurnoActivo) {
        this.idTurnoActivo = idTurnoActivo;
    }

    public boolean isMasterCaja() {
        return masterCaja;
    }

    public void setMasterCaja(boolean masterCaja) {
        this.masterCaja = masterCaja;
    }
    
}

package Dto;

import Objects.Usuario_Ob;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DtoCerrarTurno {

    private int idTurno;
    private Usuario_Ob userCierre;
    private BigDecimal arqueo;

    public DtoCerrarTurno(int idTurno, Usuario_Ob userCierre, BigDecimal arqueo) {
        this.idTurno = idTurno;
        this.userCierre = userCierre;
        this.arqueo = arqueo;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public Usuario_Ob getUserCierre() {
        return userCierre;
    }

    public void setUserCierre(Usuario_Ob userCierre) {
        this.userCierre = userCierre;
    }

    public BigDecimal getArqueo() {
        return arqueo;
    }

    public void setArqueo(BigDecimal arqueo) {
        this.arqueo = arqueo;
    }
    
    
    
}

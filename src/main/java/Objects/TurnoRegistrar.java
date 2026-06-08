package Objects;

import java.math.BigDecimal;

public class TurnoRegistrar {

    private Cajas_ob caja;
    private Usuario_Ob userMaster;
    private BigDecimal saldoInicial; 

    public TurnoRegistrar(Cajas_ob caja, Usuario_Ob userMaster, BigDecimal saldoInicial) {
        this.caja = caja;
        this.userMaster = userMaster;
        this.saldoInicial = saldoInicial;
    }

    public Cajas_ob getCaja() {
        return caja;
    }

    public void setCaja(Cajas_ob caja) {
        this.caja = caja;
    }

    public Usuario_Ob getUserMaster() {
        return userMaster;
    }

    public void setUserMaster(Usuario_Ob userMaster) {
        this.userMaster = userMaster;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

}
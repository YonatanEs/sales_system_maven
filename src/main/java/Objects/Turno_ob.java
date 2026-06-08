package Objects;

import Secciones.Usuarios;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Turno_ob {

    private int id;
    private Cajas_ob caja;
    private Usuario_Ob userMaster;
    private Usuario_Ob userCierre;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;
    private BigDecimal saldoInicial; 
    private BigDecimal ingresos;  
    private BigDecimal salidas;  
    private BigDecimal ventas;
    private BigDecimal ventaDepositos; 
    private BigDecimal cobroCredito;
    private BigDecimal saldoFinal;
    private BigDecimal saldoFaltante;
    private BigDecimal saldoSobrante;
    private BigDecimal arqueo;
    private String estado;

    public Turno_ob(int id, Cajas_ob caja, Usuario_Ob userMaster, Usuario_Ob userCierre, LocalDateTime fechaApertura, LocalDateTime fechaCierre, BigDecimal saldoInicial, BigDecimal ingresos, BigDecimal salidas, BigDecimal ventas, BigDecimal ventaDepositos, BigDecimal cobroCredito, BigDecimal saldoFinal, BigDecimal saldoFaltante, BigDecimal saldoSobrante, BigDecimal arqueo, String estado) {
        this.id = id;
        this.caja = caja;
        this.userMaster = userMaster;
        this.userCierre = userCierre;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.saldoInicial = saldoInicial;
        this.ingresos = ingresos;
        this.salidas = salidas;
        this.ventas = ventas;
        this.ventaDepositos = ventaDepositos;
        this.cobroCredito = cobroCredito;
        this.saldoFinal = saldoFinal;
        this.saldoFaltante = saldoFaltante;
        this.saldoSobrante = saldoSobrante;
        this.arqueo = arqueo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Usuario_Ob getUserCierre() {
        return userCierre;
    }

    public void setUserCierre(Usuario_Ob userCierre) {
        this.userCierre = userCierre;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(BigDecimal saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getSalidas() {
        return salidas;
    }

    public void setSalidas(BigDecimal salidas) {
        this.salidas = salidas;
    }

    public BigDecimal getVentas() {
        return ventas;
    }

    public void setVentas(BigDecimal ventas) {
        this.ventas = ventas;
    }

    public BigDecimal getVentaDepositos() {
        return ventaDepositos;
    }

    public void setVentasDepositos(BigDecimal ventasDepositos) {
        this.ventaDepositos = ventasDepositos;
    }

    public BigDecimal getCobroCredito() {
        return cobroCredito;
    }

    public void setCobroCredito(BigDecimal cobroCredito) {
        this.cobroCredito = cobroCredito;
    }
    
    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public BigDecimal getSaldoFaltante() {
        return saldoFaltante;
    }

    public void setSaldoFaltante(BigDecimal saldoFaltante) {
        this.saldoFaltante = saldoFaltante;
    }

    public BigDecimal getSaldoSobrante() {
        return saldoSobrante;
    }

    public void setSaldoSobrante(BigDecimal saldoSobrante) {
        this.saldoSobrante = saldoSobrante;
    }

    public BigDecimal getArqueo() {
        return arqueo;
    }

    public void setArqueo(BigDecimal arqueo) {
        this.arqueo = arqueo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}

package Dto;

import java.math.BigDecimal;

public class DtoEfectivoCaja {

    private int idTurno;
    private String tipoMovimiento;
    private BigDecimal importe;
    private String concepto;

    public DtoEfectivoCaja(int idTurno, String tipoMovimiento, BigDecimal importe, String concepto) {
        this.idTurno = idTurno;
        this.tipoMovimiento = tipoMovimiento;
        this.importe = importe;
        this.concepto = concepto;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

}

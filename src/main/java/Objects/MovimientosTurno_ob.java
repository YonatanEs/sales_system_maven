package Objects;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientosTurno_ob {
    
    private int id;
    private int idTurno;
    private LocalDateTime fecha;
    private String tipo;
    private String concepto;
    private BigDecimal importe;

    public MovimientosTurno_ob(int id, int idTurno, LocalDateTime fecha, String tipo, String concepto, BigDecimal importe) {
        this.id = id;
        this.idTurno = idTurno;
        this.fecha = fecha;
        this.tipo = tipo;
        this.concepto = concepto;
        this.importe = importe;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(int idTurno) {
        this.idTurno = idTurno;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

}

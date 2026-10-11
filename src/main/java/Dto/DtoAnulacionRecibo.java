package Dto;


public class DtoAnulacionRecibo {
    
    private Long idVenta;
    private Long idTurno;

    public DtoAnulacionRecibo(Long idVenta, Long idTurno) {
        this.idVenta = idVenta;
        this.idTurno = idTurno;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public Long getIdTurno() {
        return idTurno;
    }

    public void setIdTurno(Long idTurno) {
        this.idTurno = idTurno;
    }
    
}

package Dto;

import java.util.List;

public class DtoRegistrarDevolucion {
    
    private Long idVenta;
    private Long idVendedor;
    private Long idTurno;
    private String motivo;
    private List<ItemProductosDevoluciones> productosDevueltos;

    public DtoRegistrarDevolucion(Long idVenta, Long idVendedor, Long idTurno, String motivo,List<ItemProductosDevoluciones> productosDevueltos) {
        this.idVenta = idVenta;
        this.idVendedor = idVendedor;
        this.idTurno = idTurno;
        this.motivo = motivo;
        this.productosDevueltos = productosDevueltos;
    }

    public DtoRegistrarDevolucion() {
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public void setIdVendedor(Long idVendedor) {
        this.idVendedor = idVendedor;
    }

    public void setIdTurno(Long idTurno) {
        this.idTurno = idTurno;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setProductosDevueltos(List<ItemProductosDevoluciones> productosDevueltos) {
        this.productosDevueltos = productosDevueltos;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public Long getIdTurno() {
        return idTurno;
    }

    public String getMotivo() {
        return motivo;
    }

    public List<ItemProductosDevoluciones> getProductosDevueltos() {
        return productosDevueltos;
    }

}

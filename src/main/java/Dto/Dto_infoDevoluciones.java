package Dto;

import java.util.List;

public class Dto_infoDevoluciones {
    
    private Long idVenta;
    private String detalleRecibo1;
    private String detalleRecibo2;
    private String estado;
    private List<ItemProductosDevoluciones> listaProductos;

    public Dto_infoDevoluciones(Long idVenta, String detalleRecibo1, String detalleRecibo2, String estado, List<ItemProductosDevoluciones> listaProductos) {
        this.idVenta = idVenta;
        this.detalleRecibo1 = detalleRecibo1;
        this.detalleRecibo2 = detalleRecibo2;
        this.estado = estado;
        this.listaProductos = listaProductos;
    }

    public Dto_infoDevoluciones() {
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getDetalleRecibo1() {
        return detalleRecibo1;
    }

    public void setDetalleRecibo1(String detalleRecibo1) {
        this.detalleRecibo1 = detalleRecibo1;
    }

    public String getDetalleRecibo2() {
        return detalleRecibo2;
    }

    public void setDetalleRecibo2(String detalleRecibo2) {
        this.detalleRecibo2 = detalleRecibo2;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ItemProductosDevoluciones> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<ItemProductosDevoluciones> listaProductos) {
        this.listaProductos = listaProductos;
    }
    
}

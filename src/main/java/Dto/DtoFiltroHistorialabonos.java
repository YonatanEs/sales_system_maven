package Dto;

public class DtoFiltroHistorialabonos {
    
    private Long idCliente;
    private String contextoBusqueda;
    private String contentBusqueda;
    private int page;
    private int size;

    public DtoFiltroHistorialabonos(Long idCliente, String contextoBusqueda, String contentBusqueda, int page, int size) {
        this.idCliente = idCliente;
        this.contextoBusqueda = contextoBusqueda;
        this.contentBusqueda = contentBusqueda;
        this.page = page;
        this.size = size;
    }

    public DtoFiltroHistorialabonos() {
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getContextoBusqueda() {
        return contextoBusqueda;
    }

    public void setContextoBusqueda(String contextoBusqueda) {
        this.contextoBusqueda = contextoBusqueda;
    }

    public String getContentBusqueda() {
        return contentBusqueda;
    }

    public void setContentBusqueda(String contentBusqueda) {
        this.contentBusqueda = contentBusqueda;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    
}

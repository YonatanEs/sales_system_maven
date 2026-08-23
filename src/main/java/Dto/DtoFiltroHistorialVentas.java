package Dto;

public class DtoFiltroHistorialVentas {

    public enum ContextoOrigen {
        GENERAL,
        CLIENTE,
        TURNO
    }

    public enum ContextoBusqueda{
        buscadornit,
        buscadornorecibo,
        buscadorfecha
    }
    
    private ContextoOrigen contexto;
    private ContextoBusqueda contextoBusqueda;
    private String busqueda;
    private String winParametro;

    private String fecha;
    private int pageSize;
    private int page;

    public DtoFiltroHistorialVentas(ContextoOrigen contexto, ContextoBusqueda contextoBusqueda,
            String busqueda, String winParametro, String fecha, int pageSize, int page) {
        this.contexto=contexto;
        this.contextoBusqueda=contextoBusqueda;
        this.busqueda = busqueda;
        this.winParametro = winParametro;
        this.fecha = fecha;
        this.pageSize = pageSize;
        this.page = page;
    }

    public DtoFiltroHistorialVentas() {
    }
    
    public ContextoOrigen getContexto() {
        return contexto;
    }

    public void setContexto(ContextoOrigen contexto) {
        this.contexto = contexto;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public String getWinParametro() {
        return winParametro;
    }

    public void setWinParametro(String winParametro) {
        this.winParametro = winParametro;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ContextoBusqueda getContextoBusqueda() {
        return contextoBusqueda;
    }

    public void setContextoBusqueda(ContextoBusqueda contextoBusqueda) {
        this.contextoBusqueda = contextoBusqueda;
    }

}

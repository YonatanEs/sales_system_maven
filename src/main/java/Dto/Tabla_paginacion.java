package Dto;

public class Tabla_paginacion {
    
    private int page;
    private int size;
    private String busqueda;

    public Tabla_paginacion(int page, int size, String busqueda) {
        this.page = page;
        this.size = size;
        this.busqueda = busqueda;
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

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }
}

package Dto;

public class ValorRequestPag {
    
    private String busqueda;
    private int page;
    private int size;

    public ValorRequestPag(String busqueda, int page, int size) {
        this.busqueda = busqueda;
        this.page = page;
        this.size = size;
    }

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
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
    
    
}

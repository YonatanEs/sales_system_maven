package Dto;

public class DtoFiltroHistorialDevolucion {
    
    private String window;
    private String variableWindow;
    
    private String buscador;
    private String busqueda;

    private int page;
    private int size;

    public DtoFiltroHistorialDevolucion(String window, String variableWindow, String buscador, String busqueda, int page, int size) {
        this.window = window;
        this.variableWindow = variableWindow;
        this.buscador = buscador;
        this.busqueda = busqueda;
        this.page = page;
        this.size = size;
    }

    public DtoFiltroHistorialDevolucion() {
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }

    public String getVariableWindow() {
        return variableWindow;
    }

    public void setVariableWindow(String variableWindow) {
        this.variableWindow = variableWindow;
    }

    public String getBuscador() {
        return buscador;
    }

    public void setBuscador(String buscador) {
        this.buscador = buscador;
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

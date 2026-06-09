package Dto;

public class DtoItemSugerenciaProductos {

    private int id;
    private String sugerencia;

    public DtoItemSugerenciaProductos(int id, String sugerencia) {
        this.id = id;
        this.sugerencia = sugerencia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSugerencia() {
        return sugerencia;
    }

    public void setSugerencia(String sugerencia) {
        this.sugerencia = sugerencia;
    }

    @Override
    public String toString() {
        return sugerencia;
    }

}

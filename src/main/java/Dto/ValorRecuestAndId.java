package Dto;

public class ValorRecuestAndId {

    private int id;
    private String valor;

    public ValorRecuestAndId(int id, String valor) {
        this.id = id;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String Valor) {
        this.valor = Valor;
    }
    
}

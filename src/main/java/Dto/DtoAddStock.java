package Dto;

public class DtoAddStock {
    private int id;
    private double stock;

    public DtoAddStock(int id, double stock) {
        this.id = id;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
    
}

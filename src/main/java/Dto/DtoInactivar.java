package Dto;

public class DtoInactivar {

    private int id;
    private boolean inactivar;

    public DtoInactivar(int id, boolean inactivar) {
        this.id = id;
        this.inactivar = inactivar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isInactivar() {
        return inactivar;
    }

    public void setInactivar(boolean inactivar) {
        this.inactivar = inactivar;
    }
   
    
}

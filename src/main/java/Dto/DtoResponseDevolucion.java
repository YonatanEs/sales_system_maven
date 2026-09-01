package Dto;

public class DtoResponseDevolucion<T> {

    private boolean success;
    private String message;
    private boolean devolucionTotal;

    public DtoResponseDevolucion(boolean success, String message, boolean devolucionTotal) {
        this.success = success;
        this.message = message;
        this.devolucionTotal = devolucionTotal;
    }

    public DtoResponseDevolucion() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isDevolucionTotal() {
        return devolucionTotal;
    }

    public void setDevolucionTotal(boolean devolucionTotal) {
        this.devolucionTotal = devolucionTotal;
    }
    
}

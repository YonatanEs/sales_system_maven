package Dto;

public class RequestMessage {
    
    private boolean success;
    private String message;

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

    public RequestMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    
}

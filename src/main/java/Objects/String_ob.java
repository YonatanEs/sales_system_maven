package Objects;

public class String_ob {

    private String string;

    public String_ob(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
    
}

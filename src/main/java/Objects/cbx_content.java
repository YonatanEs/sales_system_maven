package Objects;

import javax.swing.JOptionPane;

public class cbx_content {

    private int id;
    private String name;

    public cbx_content(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /*@Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        cbx_content other = (cbx_content) obj;
        JOptionPane.showMessageDialog(null, "Se esta ejecutando el equals");
        // Si tu ID es int o Long, se comparan así:
        return this.id == other.id;
    }*/
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        cbx_content other = (cbx_content) obj;

        // Convertimos ambos a String para asegurarnos de que compare el número puro
        // sin importar si uno es Integer, int, o Long por detrás.
        return String.valueOf(this.id).equals(String.valueOf(other.id));
    }

}

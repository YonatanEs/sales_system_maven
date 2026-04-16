package Clases;

import Secciones.Inventario;
import Secciones.Venta;
import Vistas.UtilPanels;
import Vistas.Home;
import com.mxrck.autocompleter.AutoCompleterCallback;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.sql.*;
import javax.swing.JOptionPane;

public class Autocompleter {

    private static Home v;
    private UtilPanels p;
    
    
    public Autocompleter(Home v, UtilPanels p){
        this.v = v;
        this.p = p;
        iniautocompleter();
        llenarbusc();
    }
    
    public void iniautocompleter(){
        /*v.buscadorcod = new TextAutoCompleter(p.txt_buscadorproductos1, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {
                //Inventario.infonl(o.toString());
            }
        });*/
        v.sugerenciasventas = new TextAutoCompleter(v.txt_buscadorproductos, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {
                if (o instanceof name) {
                    name n = (name) o;
                    Venta.detalleproductos(n.getId());
                }
                if (o instanceof code) {
                    code c = (code) o;
                    Venta.detalleproductos(c.getId());
                }
            }
        });
    }
    
    public static void llenarbusc() {
        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(
                    "SELECT code FROM products WHERE Estado='Activo'");
            ResultSet r = p.executeQuery();
            while(r.next()){
                v.buscadorcod.addItem(r.getString(1));
            }
            c.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
        
        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(
                    "SELECT id, code, description FROM products");
            ResultSet r = p.executeQuery();
            v.sugerenciasventas.removeAllItems();
            while (r.next()) {
                int id = r.getInt(1);
                String code = r.getString(2), name = r.getString(3);
                v.sugerenciasventas.addItem(new name(id, name));
                v.sugerenciasventas.addItem(new code(id, code));
            }
            c.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error "+ e);
        }
    }
    
}

class name{
    private int id;
    private String name;

    public name(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
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
}

class code{
    private int id;
    private String code;

    public code(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
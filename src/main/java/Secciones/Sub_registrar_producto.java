package Secciones;

import Clases.Conexion;
import Clases.Tools;
import Objects.Categoria_ob;
import Objects.Medida_ob;
import Objects.Proveedor_ob;
import Vistas.UtilPanels;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//funciones del panel de regitrar nuevo producto
public class Sub_registrar_producto {

    private Inventario inv;
    
    /*Variables de la ventana medida*/
    JTextField txt_nombre_med, txt_abrev_med;
    JButton btn_cancel_med, btn_reg_act_med;
    JTable tab_med;

    boolean actualizando_med = false;

    int id_med_select = 0;
    
    /*variables de la ventana proveedor */
    JTextField txt_nombre_prov;
    JButton btn_cancel_prov, btn_reg_act_prov;
    JTable tab_prov;
    
    boolean actualizando_prov = false;
    int id_prov_select = 0;
    
    
    /*  variables de la ventana categoria */
    JTextField txt_nombre_cat;
    JButton btn_cancel_cat, btn_reg_act_cat;
    JTable tab_cat;
    
    boolean actualizando_cat = false;
    int id_cat_select = 0;
    
    
    public Sub_registrar_producto(Inventario inv, UtilPanels panels) {
        this.inv = inv;
       
        init(panels);
        diseño();
        
        eventosVentanaMedida();
        eventosVentanaProveedores();
        eventosVentanaCategoria();
    }

    private void diseño(){
        Tools.diseñotabla1(tab_med);
        Tools.diseñotabla1(tab_prov);
        Tools.diseñotabla1(tab_cat);
    }
    
    public void init(UtilPanels panels) {
        /*Componenetes de la ventana addMedida*/
        this.txt_nombre_med = panels.txt_nombreMed_inv;
        this.txt_abrev_med = panels.txt_abrevMed_inv;
        this.btn_cancel_med = panels.btn_cancelarMed_inv;
        this.btn_reg_act_med = panels.btn_guardarMed_inv_;
        this.tab_med = panels.tabla_medidas_inv;

        /* Componentes de la ventana addProveedor */
        this.txt_nombre_prov = panels.txt_nombreProv_inv;
        this.btn_cancel_prov = panels.btn_cancelarProv_inv;
        this.btn_reg_act_prov = panels.btn_guardarProv_inv;
        this.tab_prov = panels.tabla_proveedor_inv;
        
        /* Componentes de la ventana addCategoria */
        this.txt_nombre_cat = panels.txt_nombreCat_inv;
        this.btn_cancel_cat = panels.btn_cancelarCat_inv;
        this.btn_reg_act_cat = panels.btn_guardarCat_inv;
        this.tab_cat = panels.tabla_categoria_inv;
        
        Tools.clickwhite(Arrays.asList(txt_nombre_med, txt_abrev_med));
        Tools.clickwhite(Arrays.asList(txt_nombre_prov));
        Tools.clickwhite(Arrays.asList(txt_nombre_cat));
    }

    public void eventosVentanaMedida() {
        tab_med.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tab_med.getSelectedRow();
                id_med_select = (int) tab_med.getValueAt(row, 0);

                if (id_med_select > 0) {
                    actualizando_med = true;
                    btn_reg_act_med.setText("Actualizar");
                    txt_nombre_med.setText(tab_med.getValueAt(row, 1).toString());
                    txt_abrev_med.setText(tab_med.getValueAt(row, 2).toString());
                } else {
                    actualizando_med = false;
                    btn_reg_act_med.setText("Registrar");
                }
            }
        });

        btn_cancel_med.addActionListener((e) -> {
            cleanWinMedidas();
            tab_med.clearSelection();
        });

        btn_reg_act_med.addActionListener((e) -> {
            int val = 0;
            String nombre = txt_nombre_med.getText();
            String abrev = txt_abrev_med.getText();

            if (nombre.trim().isEmpty()) {
                txt_nombre_med.setBackground(Color.red);
                val++;
            }

            if (abrev.trim().isEmpty()) {
                txt_abrev_med.setBackground(Color.red);
                val++;
            }

            if (val == 0) {
                if (actualizando_med && id_med_select > 0) {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM medidas WHERE (nombre = ? OR abreviatura = ?) AND id_medida != ?");
                        p.setString(1, nombre);
                        p.setString(2, abrev);
                        p.setInt(3, id_med_select);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                            String nom = r.getString("nombre"), abr = r.getString("abreviatura");

                            if (nom.equalsIgnoreCase(nombre) && abr.equalsIgnoreCase(abrev)) {
                                Tools.showMessage("¡El nombre y la abreviatura ya existen!", "Message", "/Images/warning-1.png");
                            } else if (nom.equalsIgnoreCase(nombre)) {
                                Tools.showMessage("¡El nombre ya existe!", "Message", "/Images/warning-1.png");
                            } else if (abr.equalsIgnoreCase(abrev)) {
                                Tools.showMessage("¡La abreviatura ya existe!", "Message", "/Images/warning-1.png");
                            }

                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "UPDATE medidas SET nombre=?, abreviatura=? WHERE id_medida=?");
                            p2.setString(1, nombre);
                            p2.setString(2, abrev);
                            p2.setInt(3, id_med_select);
                            p2.executeUpdate();

                            cleanWinMedidas();
                            cargar_datos_tab_med();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Medida registrada correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                } else {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM medidas WHERE nombre = ? OR abreviatura = ?");
                        p.setString(1, nombre);
                        p.setString(2, abrev);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                            String nom = r.getString("nombre"), abr = r.getString("abreviatura");

                            if (nom.equalsIgnoreCase(nombre) && abr.equalsIgnoreCase(abrev)) {
                                Tools.showMessage("¡El nombre y la abreviatura ya existen!", "Message", "/Images/warning-1.png");
                            } else if (nom.equalsIgnoreCase(nombre)) {
                                Tools.showMessage("¡El nombre ya existe!", "Message", "/Images/warning-1.png");
                            } else if (abr.equalsIgnoreCase(abrev)) {
                                Tools.showMessage("¡La abreviatura ya existe!", "Message", "/Images/warning-1.png");
                            }

                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "INSERT INTO medidas(nombre, abreviatura) VALUES(?,?)");
                            p2.setString(1, nombre);
                            p2.setString(2, abrev);
                            p2.executeUpdate();

                            cleanWinMedidas();
                            cargar_datos_tab_med();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Medida registrada correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
            }

        });
    }


    public void eventosVentanaProveedores() {
        tab_prov.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tab_prov.getSelectedRow();
                id_prov_select = (int) tab_prov.getValueAt(row, 0);

                if (id_prov_select > 0) {
                    actualizando_prov = true;
                    btn_reg_act_prov.setText("Actualizar");
                    txt_nombre_prov.setText(tab_prov.getValueAt(row, 1).toString());
                } else {
                    actualizando_prov = false;
                    btn_reg_act_prov.setText("Registrar");
                }
            }
        });

        btn_cancel_prov.addActionListener((e) -> {
            cleanWinProveedores();
            tab_prov.clearSelection();
        });

        btn_reg_act_prov.addActionListener((e) -> {
            int val = 0;
            String nombre = txt_nombre_prov.getText();

            if (nombre.trim().isEmpty()) {
                txt_nombre_prov.setBackground(Color.red);
                val++;
            }

            if (val == 0) {
                if (actualizando_prov && id_prov_select > 0) {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM proveedores WHERE nombre = ? AND id_proveedor != ?");
                        p.setString(1, nombre);
                        p.setInt(2, id_prov_select);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                                Tools.showMessage("¡El proveedor ya existe!", "Message", "/Images/warning-1.png");
                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "UPDATE proveedores SET nombre=? WHERE id_proveedor=? ");
                            p2.setString(1, nombre);
                            p2.setInt(2, id_prov_select);
                            p2.executeUpdate();

                            cleanWinProveedores();
                            cargar_datos_tab_prov();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Proveedor registrado correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                } else {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM proveedores WHERE nombre = ? ");
                        p.setString(1, nombre);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                                Tools.showMessage("¡El proveedor ya existe!", "Message", "/Images/warning-1.png");
                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "INSERT INTO proveedores(nombre) VALUES(?)");
                            p2.setString(1, nombre);
                            p2.executeUpdate();

                            cleanWinProveedores();
                            cargar_datos_tab_prov();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Proveedor registrado correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
            }

        });
    }

    
    public void eventosVentanaCategoria() {
        tab_cat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tab_cat.getSelectedRow();
                id_cat_select = (int) tab_cat.getValueAt(row, 0);

                if (id_cat_select > 0) {
                    actualizando_cat = true;
                    btn_reg_act_cat.setText("Actualizar");
                    txt_nombre_cat.setText(tab_cat.getValueAt(row, 1).toString());
                } else {
                    actualizando_cat = false;
                    btn_reg_act_cat.setText("Registrar");
                }
            }
        });

        btn_cancel_cat.addActionListener((e) -> {
            cleanWinCategoria();
            tab_cat.clearSelection();
        });

        btn_reg_act_cat.addActionListener((e) -> {
            int val = 0;
            String nombre = txt_nombre_cat.getText();

            if (nombre.trim().isEmpty()) {
                txt_nombre_cat.setBackground(Color.red);
                val++;
            }

            if (val == 0) {
                if (actualizando_cat && id_cat_select > 0) {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM categorias WHERE nombre = ? AND id_categoria != ?");
                        p.setString(1, nombre);
                        p.setInt(2, id_cat_select);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                                Tools.showMessage("¡La categoria ya existe!", "Message", "/Images/warning-1.png");
                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "UPDATE categorias SET nombre=? WHERE id_categoria=? ");
                            p2.setString(1, nombre);
                            p2.setInt(2, id_cat_select);
                            p2.executeUpdate();

                            cleanWinCategoria();
                            cargar_datos_tab_cat();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Categoria actualizada correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                } else {
                    try {
                        Connection c = Conexion.conectar();
                        PreparedStatement p = c.prepareStatement(
                                "SELECT * FROM categorias WHERE nombre = ? ");
                        p.setString(1, nombre);
                        ResultSet r = p.executeQuery();
                        if (r.next()) {
                                Tools.showMessage("¡La categoria ya existe!", "Message", "/Images/warning-1.png");
                        } else {
                            try{
                            PreparedStatement p2 = c.prepareStatement(
                                    "INSERT INTO categorias(nombre) VALUES(?)");
                            p2.setString(1, nombre);
                            p2.executeUpdate();

                            cleanWinCategoria();
                            cargar_datos_tab_cat();
                            inv.cargarDatos_cbx_winRegProd();

                            Tools.showAprovationMessage("Categoria registrada correctamente", "Message");
                            }catch(Exception ex){
                                JOptionPane.showMessageDialog(null, "Error al registrar");
                            }
                        }
                        c.close();

                    } catch (Exception ef) {
                        JOptionPane.showMessageDialog(null, "Error " + e);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
            }

        });
    }

    /**
     * *** metodos de win medidas
     */
    public void initWinMedidas() {

        cleanWinMedidas();
        cargar_datos_tab_med();
    }

    public void cleanWinMedidas() {
        txt_nombre_med.setText("");
        txt_abrev_med.setText("");
        txt_nombre_med.setBackground(Color.WHITE);
        txt_abrev_med.setBackground(Color.WHITE);
        id_med_select = 0;
        actualizando_med = false;
        btn_reg_act_med.setText("Registrar");
    }
    
    //datos de la tabla medidas de la ventana addMedidas
    public void cargar_datos_tab_med() {

        DefaultTableModel model_med = (DefaultTableModel) tab_med.getModel();
        Tools.vaciartabla(model_med);

        List<Medida_ob> listMedidas = new ArrayList<>();
        String sql = "SELECT * FROM medidas";

        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {
                listMedidas.add(new Medida_ob(rs.getInt("id_medida"),
                        rs.getString("nombre"), rs.getString("abreviatura")));
            }

            for (Medida_ob medida : listMedidas) {
                Object[] fila = new Object[]{medida.getId(), medida.getNombre(),
                    medida.getAbreviatura()};
                model_med.addRow(fila);
            }

            tab_med.setModel(model_med);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "a ocurrido un error al cargar datos de tabla medidas");
        }

    }
    
    /**
     *** metodos de win proveedores
     **/
    public void initWinProveedores(){
        cleanWinProveedores();
        cargar_datos_tab_prov();
    }
    
    public void cleanWinProveedores(){
        txt_nombre_prov.setText("");
        txt_nombre_prov.setBackground(Color.WHITE);
        id_prov_select = 0;
        actualizando_prov = false;
        btn_reg_act_prov.setText("Registrar");
    }
    
    //datos de la tabla proveedor de la ventana addProveedor
    public void cargar_datos_tab_prov() {

        DefaultTableModel model_prov = (DefaultTableModel) tab_prov.getModel();
        Tools.vaciartabla(model_prov);

        List<Proveedor_ob> listProveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";

        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {
                listProveedores.add(new Proveedor_ob(rs.getInt("id_proveedor"), 
                        rs.getString("nombre")));
            }

            for (Proveedor_ob prov : listProveedores) {
                Object[] fila = new Object[]{prov.getId(), prov.getNombre()};
                model_prov.addRow(fila);
            }

            tab_prov.setModel(model_prov);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "a ocurrido un error al cargar datos de tabla proveedor");
        }

    }
    
    /**   cargar datos de ventana categoria   **/
    public void initWinCategoria(){
        cleanWinCategoria();
        cargar_datos_tab_cat();
    }

    private void cleanWinCategoria() {
        txt_nombre_cat.setText("");
        txt_nombre_cat.setBackground(Color.WHITE);
        id_cat_select = 0;
        actualizando_cat = false;
        btn_reg_act_cat.setText("Registrar");
    }
    
    public void cargar_datos_tab_cat() {

        DefaultTableModel model_cat = (DefaultTableModel) tab_cat.getModel();
        Tools.vaciartabla(model_cat);

        List<Categoria_ob> listCategoria = new ArrayList<>();
        String sql = "SELECT * FROM categorias";

        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);
            ResultSet rs = p.executeQuery();

            while (rs.next()) {
                listCategoria.add(new Categoria_ob(rs.getInt("id_categoria"), 
                        rs.getString("nombre")));
            }

            for (Categoria_ob cat : listCategoria) {
                Object[] fila = new Object[]{cat.getId(), cat.getNombre()};
                model_cat.addRow(fila);
            }

            tab_cat.setModel(model_cat);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "a ocurrido un error al cargar datos de tabla proveedor");
        }

    }
    
    
}

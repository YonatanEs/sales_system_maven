package Secciones;

import Vistas.Home;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.sql.*;
import Clases.Conexion;
import Vistas.UtilPanels;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.accessibility.AccessibleRole;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class Venta {

    private static Home v;
    private static UtilPanels p;
    private static JDialog detalleproducto;
    private static int idpro;
    //private static int rowselectid;
    
    

    public Venta(Home v, UtilPanels p) {
        this.v = v;
        this.p = p;
        customtable();
        //clickevent();
    }

    private static void clickevent() {
        v.tableNventa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = v.tableNventa.rowAtPoint(e.getPoint());
            }
        });
    }

    //modifica el diseño de la tabla venta
    private static void customtable() {
        v.tableNventa.getTableHeader().setReorderingAllowed(false);
        v.tableNventa.setRowHeight(30);
    }

    //muestra el producto que escribes en el buscador
    public static void detalleproductos(int id) {
        try {
            Connection c = Conexion.conectar();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM products WHERE id='" + id + "'");
            ResultSet r = ps.executeQuery();
            if (r.next()) {
                p.jL_namepr.setText(r.getString("description"));
                p.jL_codepr.setText(r.getString("code"));
                p.jL_catepro.setText(r.getString("categoria"));
                byte b[] = r.getBytes("foto");
                ImageIcon foto = new ImageIcon(b);
                p.jL_fotopr.setIcon(new ImageIcon(
                        foto.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH)));
                int stock = Integer.parseInt(stockpr(id));
                p.jL_stockpr.setText("" + stock);
                ChangeListener[] cl = p.cant.getChangeListeners();

                if (cl.length > 1) {
                    int lis = 0;
                    for (ChangeListener cls : cl) {
                        if (lis != 1) {
                            p.cant.removeChangeListener(cls);
                        }
                        lis++;
                    }
                }
                p.cant.setValue(0);
                p.cant.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        int valor = Integer.parseInt(p.cant.getValue().toString());
                        // Limita el valor a 0 si es menor
                        if (valor < 1) {
                            p.cant.setValue(0);
                        }

                        // Limita el valor a 30 si es mayor
                        if (valor > stock) {
                            p.cant.setValue(stock);
                        }

                        precio();
                    }
                });
                idpro = id;
                precio();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
        detalleproducto = new JDialog();
        detalleproducto.getContentPane().add(p.jProduct);
        detalleproducto.pack();
        detalleproducto.setLocationRelativeTo(null);
        detalleproducto.setResizable(false);
        detalleproducto.setModal(true);
        detalleproducto.setVisible(true);
    }

    private static String stockpr(int id) {
        int st = 0;
        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(
                    "SELECT SUM(stock) FROM batch WHERE id_product='" + id + "'");
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                st = rs.getInt(1);
            } else {
                st = 0;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }

        for (int i = 0; i < v.tableNventa.getRowCount(); i++) {
            int idp = Integer.parseInt(v.tableNventa.getValueAt(i, 0).toString());
            if (idp == id) {
                int stock = Integer.parseInt(v.tableNventa.getValueAt(i, 4).toString());

                st = st - stock;
            }
        }

        return st + "";
    }

    public static void precio() {
        int cant = Integer.parseInt(p.cant.getValue().toString());
        int cant2 = cant;
        Double preciofinal = 0.0;
        try {
            Connection c = Conexion.conectar();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM batch WHERE id_product='" + idpro + "' AND Stock>0");
            ResultSet r = ps.executeQuery();
            //int stock = 0;
            ArrayList<Producto> pro = new ArrayList<>();
            if (cant != 0) {
                while (r.next()) {
                    int stock = r.getInt("stock");
                    if (cant2 > stock) {
                        pro.add(new Producto(r.getString("lot_number"), stock, r.getDouble("sale_price")));
                        cant2 = cant2 - stock;
                    } else if (cant2 == stock) {
                        pro.add(new Producto(r.getString("lot_number"), stock, r.getDouble("sale_price")));
                        break;
                    } else {
                        pro.add(new Producto(r.getString("lot_number"), cant2, r.getDouble("sale_price")));
                        break;
                    }
                }
                preciofinal = PrecioPromedio(pro);
            } else {
                preciofinal = 0.0;
            }
            c.close();
            p.jL_preciopr.setText("Q. " + preciofinal);
            p.jL_preciototal.setText("Q. " + preciofinal * cant);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }

    private static double PrecioPromedio(ArrayList<Producto> productos) {

        double precioTotal = 0.0;
        double precioPromedio = 0.0;

        for (Producto producto : productos) {
            precioTotal += producto.getPrecio() * producto.getCantidad();
        }

        precioPromedio = precioTotal / productos.stream().mapToInt(Producto::getCantidad).sum();

        return precioPromedio;
    }

    public static void añadirproduct() {
        int stk = Integer.parseInt(p.cant.getValue().toString());
        if (stk > 0) {
            if (v.tableNventa.getRowCount() == 0) {
                DefaultTableModel mod = (DefaultTableModel) v.tableNventa.getModel();
                mod.addRow(new Object[]{idpro, p.jL_codepr.getText(), p.jL_namepr.getText(), 0,
                    p.cant.getValue().toString(), 0});
                v.tableNventa.setModel(mod);
                preciotab(idpro);
                detalleproducto.dispose();
                Totalapagar();
            } else {
                int nf = 0;
                boolean yes = false;

                for (int i = 0; i < v.tableNventa.getRowCount(); i++) {
                    int id = Integer.parseInt(v.tableNventa.getValueAt(i, 0).toString());
                    if (id == idpro) {
                        yes = true;
                        nf = i;
                        break;
                    }
                }
                if (yes) {
                    int stock = Integer.parseInt(v.tableNventa.getValueAt(nf, 4).toString());
                    int nstock = Integer.parseInt(p.cant.getValue().toString());

                    v.tableNventa.setValueAt(stock + nstock, nf, 4);
                    preciotab(idpro);
                    detalleproducto.dispose();
                    Totalapagar();
                } else {
                    DefaultTableModel mod = (DefaultTableModel) v.tableNventa.getModel();
                    mod.addRow(new Object[]{idpro, p.jL_codepr.getText(), p.jL_namepr.getText(), 0,
                        p.cant.getValue().toString(), 0});
                    v.tableNventa.setModel(mod);
                    preciotab(idpro);
                    detalleproducto.dispose();
                    Totalapagar();
                }

            }
        }
    }

    private static void preciotab(int id) {
        int numfila = 0;
        for (int i = 0; i < v.tableNventa.getRowCount(); i++) {
            int numf = (int) v.tableNventa.getValueAt(i, 0);
            if (numf == id) {
                numfila = i;
                break;
            }
        }

        int cant = Integer.parseInt(v.tableNventa.getValueAt(numfila, 4).toString());
        int cant2 = cant;
        Double preciofinal = 0.0;
        try {
            Connection c = Conexion.conectar();
            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM batch WHERE id_product='" + id + "' AND Stock>0");
            ResultSet r = ps.executeQuery();
            //int stock = 0;
            ArrayList<Producto> pro = new ArrayList<>();
            if (cant != 0) {
                while (r.next()) {
                    int stock = r.getInt("stock");
                    if (cant2 > stock) {
                        pro.add(new Producto(r.getString("lot_number"), stock, r.getDouble("sale_price")));
                        cant2 = cant2 - stock;
                    } else if (cant2 == stock) {
                        pro.add(new Producto(r.getString("lot_number"), stock, r.getDouble("sale_price")));
                        break;
                    } else {
                        pro.add(new Producto(r.getString("lot_number"), cant2, r.getDouble("sale_price")));
                        break;
                    }
                }
                preciofinal = PrecioPromedio(pro);
            } else {
                preciofinal = 0.0;
            }
            c.close();
            v.tableNventa.setValueAt(preciofinal, numfila, 3);
            v.tableNventa.setValueAt(preciofinal * cant, numfila, 5);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }

    public static void Totalapagar() {
        Double Total = 0.00;
        for (int i = 0; i < v.tableNventa.getRowCount(); i++) {
            Double sub = (Double) v.tableNventa.getValueAt(i, 5);
            Total = Total + sub;
        }
        v.jL_totalapagar.setText(Total + "");
    }

    public static void eliminarpro() {
        int[] rows = v.tableNventa.getSelectedRows();
        int resp = JOptionPane.showConfirmDialog(null, "¿Eliminar productos seleccionados?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            DefaultTableModel mod = (DefaultTableModel) v.tableNventa.getModel();
            for (int i = rows.length - 1; i >= 0; i--) {
                mod.removeRow(rows[i]);
            }
            v.tableNventa.setModel(mod);
            Totalapagar();
        }
    }

    public static void eliminarproall() {
        int resp = JOptionPane.showConfirmDialog(null, "¿Eliminar todos los productos?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(resp==JOptionPane.YES_OPTION){
            DefaultTableModel mod = (DefaultTableModel) v.tableNventa.getModel();
            mod.setRowCount(0);
            v.tableNventa.setModel(mod);
            Totalapagar();
        }
    }
}

class Producto {

    private String nombre;
    private int cantidad;
    private double precio;

    public Producto(String nombre, int cantidad, double precio) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    
    
}

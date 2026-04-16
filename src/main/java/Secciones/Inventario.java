package Secciones;

import Clases.Conexion;
import Clases.Tools;
import Objects.Precio_ob;
import Objects.cbx_content;
import Objects.productos_inv;
import Vistas.UtilPanels;
import Vistas.Home;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Inventario {

    private JLabel btn_nuevoproducto, btn_ingresarInventario,
            btn_anteriorpag, btn_totalpag, btn_siguientepag;

    private JComboBox<String> cbx_buscpor;
    private JComboBox<cbx_content> cbx_itembuspor;

    private JTable tabla_inventario;

    private JTextField Buscador;

    private int tamañoPag = 25;
    private int pagActual = 1;
    private int totalPag;

    private int pagGuardada = 0;

    //componentes de la ventana registrar/actualizar producto
    private JDialog windowRegistrar;

    private boolean winregistrando;

    private Precio_ob precioventa;
    private Precio_ob precioMayor;

    private JComboBox cbx_categoria_win;
    private JComboBox cbx_medida_win;
    private JComboBox cbx_proveedor_win;

    private ArrayList<JTextField> list_txt_rp;
    private JTextField txt_codigo_pr;
    private JTextField txt_descripcion_pr;

    private JButton btn_guardar_pr;
    private JButton btn_addMedida;
    private JButton btn_addCategoria;
    private JButton btn_addProveedor;

    //componenetes de la ventana add medidas
    Sub_registrar_producto subwin_reg_producto;

    private JDialog windowAddMedida;

    private JTextField txt_nombre_winMed;
    private JTextField txt_abrev_winMed;


    //componentes de la ventana add categoria
    private JDialog windowAddCategoria;
    
    //componentes de la ventana add proveedores
    private JDialog windowAddProveedor;
    
    //componentes de la ventana registrar stock inicial
    private JDialog win_reg_stock_ini;
    
    private JLabel jL_hynt_medida_rsi;
    private JSpinner jS_stockinicial_rsi;
    
    private Precio_ob precio_compra_rsi;

    private JButton btn_omitir_rsi;
    private JButton btn_continuar_rsi;
    
   
    
    
    
    private boolean actualizando_pr = false;

    public Inventario(Home home, UtilPanels panel) {
        //componentes de la ventana mayor
        btn_nuevoproducto = home.btn_nvprod_inv;
        btn_ingresarInventario = home.btn_addinv_inv;
        btn_anteriorpag = home.btn_anteriorpag_inv;
        btn_anteriorpag.setName("btn_anteriopag_inv");
        btn_totalpag = home.btn_pags_inv;
        btn_siguientepag = home.btn_sigpag_inv;
        btn_siguientepag.setName("btn_siguientepag_inv");

        cbx_buscpor = home.cbx_buspor_inv;
        cbx_itembuspor = home.cbx_itembuspor_inv;

        tabla_inventario = home.tabla_inventario;

        Buscador = home.txt_busprod_inv;

        //componentes de la ventana registrar/actualizar
        windowRegistrar = Tools.newWindow(panel.jP_reg_producto_inv);

        txt_codigo_pr = panel.txt_codigo_rp_inv;
        txt_descripcion_pr = panel.txt_descripcion_rp_inv;

        precioventa = new Precio_ob(panel.txt_precioVenta_rp_inv);
        precioMayor = new Precio_ob(panel.txt_preciomayor_rp_inv);


        cbx_categoria_win = panel.cbx_categoria_rp_inv;
        cbx_medida_win = panel.cbx_medida_rp_inv;
        cbx_proveedor_win = panel.cbx_proveedor_rp_inv;

        btn_guardar_pr = panel.btn_guardar_rp_inv;
        btn_addMedida = panel.btn_addmedida_inv;
        btn_addCategoria = panel.btn_addcategoria_inv;
        btn_addProveedor = panel.btn_addproveedor_inv;

        //componentes de la ventana addMedidas
        subwin_reg_producto = new Sub_registrar_producto(this, panel);
        windowAddMedida = Tools.newWindow(panel.jP_reg_medida_inv);

        txt_nombre_winMed = panel.txt_nombreMed_inv;
        txt_abrev_winMed = panel.txt_abrevMed_inv;

        
        //componentes de ventana addProveedor
        windowAddProveedor = Tools.newWindow(panel.jP_reg_proveedor_inv);

        //componentes de ventana addCategoria
        windowAddCategoria = Tools.newWindow(panel.jP_reg_categoria_inv);
        
        //componentes de ventana registrar stock inicial
        win_reg_stock_ini = Tools.newWindow(panel.jP_reg_stockinicial);
        
        jL_hynt_medida_rsi = panel.jL_stock_hint_rsi;
        jS_stockinicial_rsi = panel.jS_stock_inicial_rsi;
        precio_compra_rsi = new Precio_ob(panel.txt_preciocompra_rsi);
        btn_omitir_rsi = panel.btn_omitir_rsi;
        btn_continuar_rsi = panel.btn_continuar_rsi;
        
        diseño();
        eventosVentanaMayor();
        eventosVentanaReg();
        eventosVentanaStockIni();

        //inicializamos datos
        cargarDatos_cbx_buscarporitem();
        cargarDatosTabla(0, "");
    }

    //añadimos diseño a los componenetes de nuestra ventana inventario
    private void diseño() {
        Tools.btn_animacion_size(btn_nuevoproducto);
        Tools.btn_animacion_size(btn_ingresarInventario);
        Tools.btn_animacion_size(btn_anteriorpag);
        Tools.btn_animacion_size(btn_siguientepag);

        Tools.diseñotabla1(tabla_inventario);

        Tools.buscador(Buscador, "Buscar producto...");

        Tools.clickwhite_comp(Arrays.asList(txt_codigo_pr, txt_descripcion_pr));
        Tools.clickwhite_comp(Arrays.asList(cbx_medida_win, cbx_categoria_win, cbx_proveedor_win));
    }

    //añadimos funcion a nuestros componenetes de inventario
    private void eventosVentanaMayor() {
        //al cambiar de seleccion en buscar por se actualizaran los datos
        cbx_buscpor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatos_cbx_buscarporitem();
            }
        });

        //al seleccionar un elemento se actualizan los datos de la tabla
        cbx_itembuspor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                cbx_content cb = (cbx_content) cbx_itembuspor.getSelectedItem();
                if (cb == null || cb.getId() == 0) {
                    cargarDatosTabla(0, "");
                } else {
                    cargarDatosTabla(2, "");
                }
            }
        });

        //boton de pagina atras
        btn_anteriorpag.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pagActual = pagActual--;
                cargarDatosTabla(0, "");
            }
        });

        //boton de pagina siguiente
        btn_siguientepag.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pagActual = pagActual++;
                cargarDatosTabla(0, "");
            }
        });

        //funcion de buscador
        Buscador.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String busqueda = Buscador.getText();
                cbx_itembuspor.setSelectedIndex(0);
                if (busqueda.trim().isEmpty()) {
                    cargarDatosTabla(1, "");
                } else {
                    cargarDatosTabla(1, busqueda);
                }

            }
        });

        //funcion de boton nuevo producto
        btn_nuevoproducto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //cargardatos de los cbx de ventana registrar producto
                cargarDatos_cbx_winRegProd();
                
                //Limpiar los campos de la ventana nuevo producto
                cleanWinRegistrar();
                precioventa.resert();
                precioMayor.resert();
                Tools.resetOriginalColor(Arrays.asList(txt_codigo_pr, txt_descripcion_pr,
                        cbx_medida_win, cbx_categoria_win, cbx_proveedor_win));

                actualizando_pr = false;
                windowRegistrar.setVisible(true);
            }
        });

    }

    private void eventosVentanaReg() {
        
        //funcion de guardar o actualizar producto al presionar el boton guardar
        btn_guardar_pr.addActionListener(e -> {
            if (actualizando_pr) {

            } else {
                RegistrarNuevoProducto();
            }
        });

        //boton addMedida
        btn_addMedida.addActionListener(e -> {
            //cargar datos 
            subwin_reg_producto.initWinMedidas();
            windowAddMedida.setVisible(true);
        });

        /**
         * ****Funciones de las subventanas medida, categoria, y proveedor****
         */
        //funcion de añadir abreviatura a txt abreviatura
        txt_nombre_winMed.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String nombre = txt_nombre_winMed.getText();
                String abreviatura = generarAbreviatura(nombre);
                txt_abrev_winMed.setText(abreviatura);

            }
        });
        
        /*****  Funciones de ventanas addProveedor *********/
        //boton de addproveedor
        btn_addProveedor.addActionListener(e ->{
            subwin_reg_producto.initWinProveedores();
            windowAddProveedor.setVisible(true);
        });
        
        
        /*******  Funciones de ventana addCategoria ******/
        //boton de addCategoria
        btn_addCategoria.addActionListener(e ->{
            subwin_reg_producto.initWinCategoria();
            windowAddCategoria.setVisible(true);
        });
    }

    private void eventosVentanaStockIni(){
        btn_omitir_rsi.addActionListener(e->{
            win_reg_stock_ini.dispose();
        });
        
        btn_continuar_rsi.addActionListener(e->{
            JOptionPane.showMessageDialog(null, "Guardando..");
        });
    }
    
    public void cargarDatos_cbx_winRegProd() {
        //cargar los datos de de los jcomboBox de la ventana registrar producto
        List<String[]> cbx = new ArrayList<>();
        List<JComboBox> listCBX = new ArrayList<>();

        listCBX.add(cbx_categoria_win);
        listCBX.add(cbx_medida_win);
        listCBX.add(cbx_proveedor_win);

        cbx.add(new String[]{"categorias", "id_categoria", "nombre"});
        cbx.add(new String[]{"medidas", "id_medida", "nombre"});
        cbx.add(new String[]{"proveedores", "id_proveedor", "nombre"});

        for (int i = 0; i < cbx.size(); i++) {
            List<cbx_content> cont = itemsBusqueda(cbx.get(i)[0], cbx.get(i)[1], cbx.get(i)[2]);
            listCBX.get(i).removeAllItems();
            for (cbx_content c1 : cont) {
                listCBX.get(i).addItem(c1);
            }
        }
    }

    private void cargarDatos_cbx_buscarporitem() {
        ArrayList<cbx_content> Items;
        if (cbx_buscpor.getSelectedIndex() == 0) {
            Items = itemsBusqueda("categorias", "id_categoria", "nombre");
        } else {
            Items = itemsBusqueda("proveedores", "id_proveedor", "nombre");
        }
        cbx_itembuspor.removeAllItems();
        for (cbx_content cont : Items) {
            cbx_itembuspor.addItem(cont);
        }
    }

    private ArrayList<cbx_content> itemsBusqueda(String table, String column_id, String column_nombre) {
        try {
            ArrayList<cbx_content> listItems = new ArrayList<>();
            listItems.add(new cbx_content(0, "Seleccione una opción"));

            String sql = "SELECT " + column_id + ", " + column_nombre + " FROM " + table;

            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);

            ResultSet r = p.executeQuery();

            while (r.next()) {
                listItems.add(new cbx_content(r.getInt(1), r.getString(2)));
            }

            c.close();
            return listItems;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en itemsBusqueda " + e);
        }
        return null;
    }

    private int totalPag(int caso, String busqueda) {
        try {
            String sql = "SELECT COUNT(*) FROM productos";
            if (caso == 1) {
                sql = "SELECT COUNT(*) FROM productos WHERE descripcion LIKE '%" + busqueda + "%' OR codigo LIKE '%" + busqueda + "%'";
            } else if (caso == 2) {
                String column = "id_categoria";
                if (cbx_buscpor.getSelectedIndex() == 1) {
                    column = "id_proveedor";
                }

                cbx_content cb = (cbx_content) cbx_itembuspor.getSelectedItem();
                int id = (cb == null) ? 0 : cb.getId();

                sql = "SELECT COUNT(*) FROM productos WHERE " + column + " ='" + id + "'";
            }

            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);

            ResultSet r = p.executeQuery();

            if (r.next()) {
                int totalregistros = r.getInt(1);
                c.close();

                int totalpaginas = (int) Math.ceil((double) totalregistros / tamañoPag);

                //JOptionPane.showMessageDialog(null, totalregistros);
                //JOptionPane.showMessageDialog(null, totalpaginas);
                return totalpaginas;
            }
            c.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en totalPag " + e);
        }
        return 0;
    }

    private void cargarDatosTabla(int caso, String busqueda) {
        try {
            totalPag = totalPag(caso, busqueda);

            if (totalPag == 0) {
                pagActual = 0;
            } else {
                pagActual = 1;
            }

            btn_totalpag.setText(pagActual + " de " + totalPag);

            if (pagActual <= 1 || totalPag <= 1) {
                Tools.btn_anim_enabled(btn_anteriorpag, false);
            } else {
                Tools.btn_anim_enabled(btn_anteriorpag, true);
            }

            if (pagActual == totalPag || totalPag <= 1) {
                Tools.btn_anim_enabled(btn_siguientepag, false);
            } else {
                Tools.btn_anim_enabled(btn_siguientepag, true);
            }

            ArrayList<productos_inv> listProducts = new ArrayList<>();

            DefaultTableModel model = (DefaultTableModel) tabla_inventario.getModel();
            Tools.vaciartabla(model);

            if (totalPag > 0) {
                String casoDeBusqueda = " ";
                if (caso == 1) {
                    casoDeBusqueda = "WHERE p.descripcion LIKE '%" + busqueda + "%' OR p.codigo LIKE '%" + busqueda + "%' ";
                } else if (caso == 2) {
                    String column = "p.id_categoria";
                    if (cbx_buscpor.getSelectedIndex() == 1) {
                        column = "p.id_proveedor";
                    }

                    cbx_content cb = (cbx_content) cbx_itembuspor.getSelectedItem();
                    casoDeBusqueda = " WHERE " + column + " ='" + cb.getId() + "' ";
                }

                String sql = "SELECT p.id_productos, p.codigo, p.descripcion, COALESCE(SUM(i.stock_actual),0) AS total_stock, p.precio, "
                        + " p.precio_por_mayor, p.id_categoria,  c.nombre AS nombre_categoria, "
                        + " p.id_medida, m.abreviatura AS nombre_medida,  p.id_proveedor, "
                        + " pr.nombre  AS nombre_proveedor, p.estado "
                        + "FROM productos p "
                        + "INNER JOIN categorias c ON p.id_categoria = c.id_categoria "
                        + "INNER JOIN medidas m ON p.id_medida = m.id_medida "
                        + "INNER JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor "
                        + "LEFT JOIN ingreso_inventario i ON p.id_productos = i.id_producto GROUP BY p.id_productos "
                        + casoDeBusqueda
                        + " ORDER BY p.descripcion ASC "
                        + "LIMIT " + tamañoPag + " OFFSET " + ((pagActual - 1) * tamañoPag) + "";

                Connection c = Conexion.conectar();
                PreparedStatement p = c.prepareStatement(sql);

                ResultSet r = p.executeQuery();

                while (r.next()) {
                    listProducts.add(new productos_inv(
                            r.getInt("id_productos"),
                            r.getString("codigo"),
                            r.getString("descripcion"),
                            r.getDouble("total_stock"),
                            r.getDouble("precio"),
                            r.getDouble("precio_por_mayor"),
                            r.getInt("id_categoria"),
                            r.getString("nombre_categoria"),
                            r.getInt("id_medida"),
                            r.getString("nombre_medida"),
                            r.getInt("id_proveedor"),
                            r.getString("nombre_proveedor"),
                            r.getString("estado")
                    ));
                }

                for (productos_inv prod : listProducts) {
                    Object[] fila = new Object[]{prod.getId_producto(), prod.getCodigo(), prod.getDescripcion(), prod.getStock() + " " + prod.getMedida(),
                        "Q. " + prod.getPrecio(), "Q. " + prod.getPrecio_por_mayor(), prod.getCategoria(), prod.getProveedor(), prod.getEstado()};
                    model.addRow(fila);
                }

            }
            tabla_inventario.setModel(model);

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en cargar datos de tabla " + e);
        }
    }

    private String getAbrMedida(int id_selected) {
        try {
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement("SELECT abreviatura FROM medidas WHERE id_medida=?");
            p.setInt(1, id_selected);

            ResultSet r = p.executeQuery();
            if (r.next()) {
                return r.getString(1);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void RegistrarNuevoProducto() {
        String codigo = txt_codigo_pr.getText(),
                descripcion = txt_descripcion_pr.getText();
        double precio_v = precioventa.getPrecio(),
                precioMayor_v = precioMayor.getPrecio();
        int id_medida = 0, id_categoria = 0, id_proveedor = 0;
        boolean val = true;

        if (codigo.trim().isEmpty()) {
            val = false;
            txt_codigo_pr.setBackground(Color.red);
        }
        if (descripcion.trim().isEmpty()) {
            val = false;
            txt_descripcion_pr.setBackground(Color.red);
        }
        
        if (precio_v <= 0) {
            val = false;
            precioventa.setError();
        }
        if (precioMayor_v <= 0) {
            val = false;
            precioMayor.setError();
        }
        if (cbx_categoria_win.getSelectedItem() instanceof cbx_content) {
            id_categoria = ((cbx_content) cbx_categoria_win.getSelectedItem()).getId();
            if (id_categoria < 1) {
                val = false;
                cbx_categoria_win.setBackground(Color.red);
            }
        }
        if (cbx_medida_win.getSelectedItem() instanceof cbx_content) {
            id_medida = ((cbx_content) cbx_medida_win.getSelectedItem()).getId();
            if (id_medida < 1) {
                val = false;
                cbx_medida_win.setBackground(Color.red);
            }
        }
        if (cbx_proveedor_win.getSelectedItem() instanceof cbx_content) {
            id_proveedor = ((cbx_content) cbx_proveedor_win.getSelectedItem()).getId();
            if (id_proveedor < 1) {
                val = false;
                cbx_proveedor_win.setBackground(Color.red);
            }
        }

        if (val) {
            try {
                Connection c = Conexion.conectar();
                PreparedStatement p = c.prepareStatement("SELECT * FROM productos WHERE codigo=?");
                p.setString(1, codigo);
                ResultSet r = p.executeQuery();

                if (r.next()) {
                    Tools.showMessage("¡El código ya esta siendo usado!", "Message", "/Images/warning-1.png");
                } else {
                    try {
                        
                        PreparedStatement p2 = c.prepareStatement(
                                "INSERT INTO productos(codigo, descripcion, precio, precio_por_mayor, id_categoria, id_medida, id_proveedor, estado) "
                                + " VALUES (?,?,?,?,?,?,?,'Activo')", Statement.RETURN_GENERATED_KEYS);
                        p2.setString(1, codigo);
                        p2.setString(2, descripcion);
                        p2.setDouble(3, precio_v);
                        p2.setDouble(4, precioMayor_v);
                        p2.setInt(5, id_categoria);
                        p2.setInt(6, id_medida);
                        p2.setInt(7, id_proveedor);
                        
                        p2.executeUpdate();

                        ResultSet generatedKeys = p2.getGeneratedKeys();
                        int id_productogenerado = 0;
                        if(generatedKeys.next()){
                            id_productogenerado = generatedKeys.getInt(1);
                        }
                        show_reg_stock_inicial(id_productogenerado);
                        
                        cleanWinRegistrar();
                        Tools.paintgreen(Arrays.asList(txt_codigo_pr, txt_descripcion_pr,
                                cbx_medida_win, cbx_categoria_win, cbx_proveedor_win));
                        precioventa.setAprobation();
                        precioMayor.setAprobation();

                        cargarDatosTabla(0, "");

                        Tools.showAprovationMessage("¡Producto registrado correctamente!", "Message");
                        Tools.resetOriginalColor(Arrays.asList(txt_codigo_pr, txt_descripcion_pr, cbx_medida_win,
                                cbx_categoria_win, cbx_proveedor_win));
                        precioventa.resert();
                        precioMayor.resert();

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error en segunda conexion " + ex);
                    }
                }
                c.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error en la conexion " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }
    
    private void show_reg_stock_inicial(int id_productogenerado){
        try{
            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement("SELECT m.abreviatura FROM productos p "
                    + "INNER JOIN medidas m ON p.id_medida = m.id_medida WHERE id_productos = ?");
            p.setInt(1, id_productogenerado);
            ResultSet r = p.executeQuery();
            if(r.next()){
                jL_hynt_medida_rsi.setText(r.getString(1));
            }else{
                jL_hynt_medida_rsi.setText("");
            }
            jS_stockinicial_rsi.setValue(0);
            
        }catch(Exception ef){
            JOptionPane.showMessageDialog(null, "Error "+ ef);
        }
        win_reg_stock_ini.setVisible(true);
    }
    
    

    private String generarAbreviatura(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return "";
        }

        String[] palabras = nombre.split("\\s+"); // Dividir el nombre en palabras
        StringBuilder abreviatura = new StringBuilder();

        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                abreviatura.append(palabra.charAt(0)); // Tomar la primera letra

                if (palabra.length() >= 3) {
                    abreviatura.append(palabra.charAt(2)); // Tomar la tercera letra (si existe)
                }
            }
        }

        return abreviatura.toString();//.toUpperCase(); // Convertir a mayúsculas
    }

    private void cleanWinRegistrar() {
        Tools.clean(Arrays.asList(txt_codigo_pr, txt_descripcion_pr));
        cbx_medida_win.setSelectedIndex(0);
        cbx_categoria_win.setSelectedIndex(0);
        cbx_proveedor_win.setSelectedIndex(0);
    }

}

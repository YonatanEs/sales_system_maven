package Secciones;

import Clases.OrdenPersonalizado;
import Clases.Tools;
import Clases.Txt_buscador;
import Dto.DtoAddStock;
import Dto.DtoResponseOb;
import Dto.RequestMessage;
import Dto.RespuestaPaginada;
import Dto.ValorRecuest;
import Dto.ValorRequestPag;
import General.UtilMessage;
import General.userAuth;
import Objects.CategoriaRegistrar;
import Objects.Categoria_ob;
import Objects.Cliente_ob;
import Objects.MedidaRegistrar;
import Objects.Medida_ob;
import Objects.ProductoRegistrar;
import Objects.Producto_ob;
import Objects.ProveedorRegistrar;
import Objects.Proveedor_ob;
import Objects.String_ob;
import Vistas.Home;
import Vistas.UtilPanels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mxrck.autocompleter.AutoCompleter;
import com.mxrck.autocompleter.AutoCompleterCallback;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Inventario {

    private Home home;
    private UtilPanels panel;

    private static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private int pagina_actual = 0;

    private JDialog d_registrar;
    private JDialog d_proveedor;
    private JDialog d_categoria;
    private JDialog d_medida;
    private JDialog d_addStock;

    private Producto_ob productoSelected;
    private boolean actualizandoProducto;

    private Proveedor_ob proveedorSelected;
    private boolean actualizandoProveedor;

    private Categoria_ob categoriaSelected;
    private boolean actualizandoCategoria;

    private Medida_ob medidaSelected;
    private boolean actualizandoMedida;

    public Txt_buscador txt_buscador_producto;

    private TextAutoCompleter autoCompleterInventario;

    public Inventario(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;

        Tools.btn_animacion_size(home.btn_nvprod_inv);
        Tools.btn_animacion_size(home.btn_anteriorpag_inv);
        Tools.btn_animacion_size(home.btn_sigpag_inv);
        txt_buscador_producto = new Txt_buscador("Buscar producto..", home.txt_buscador_inventario);
        d_registrar = Tools.newWindow(panel.jP_reg_producto_inv);
        // Definimos el orden exacto que queremos
        List<Component> miOrden = Arrays.asList(
                panel.txt_codigo_rp_inv,
                panel.txt_descripcion_rp_inv,
                panel.txt_precioVenta_rp_inv,
                panel.txt_preciocompra_rp_inv,
                panel.cbx_medida_rp_inv,
                panel.cbx_proveedor_rp_inv,
                panel.cbx_categoria_rp_inv,
                panel.btn_guardar_rp_inv
        );
        Tools.configurarNavegacionFlechas(d_registrar);
        Tools.txt_cantidad(panel.txt_addstock_addstock);
        Tools.focusWhite_comp(Arrays.asList(panel.txt_addstock_addstock));

        d_registrar.setFocusTraversalPolicy(new OrdenPersonalizado(miOrden));

        d_addStock = Tools.newWindow(panel.jP_addStock);
        configurarAtajo();

        customTable();

        Listeners();
        ListenerToWindowProveedor();
        ListenerToWindowCategoria();
        ListenerToWindowMedida();
        ListenerToWindowAddStock();

    }

    /*---------- --- Encargados de darle funcion a los objetos con listener ----*/
    public void Listeners() {
        //obtener el producto seleccionado de tabla inventario
        home.tableInventario.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                handlePopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handlePopup(e);
            }

            public void handlePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = home.tableInventario.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < home.tableInventario.getRowCount()) {

                        home.tableInventario.setRowSelectionInterval(row, row);
                        int id_producto = Integer.parseInt(home.tableInventario.getValueAt(row, 0).toString());

                        productoSelected = productoSelected(id_producto);

                        home.jPopupInventario.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        //boton siguiente de tabla producto
        home.btn_sigpag_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean isEnabled = home.btn_sigpag_inv.isEnabled();
                if (isEnabled) {
                    pagina_actual++;
                    tabledates(txt_buscador_producto.getTextoReal(), pagina_actual);
                }
            }
        });

        //btn anterior de tabla producto
        home.btn_anteriorpag_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual > 0) {
                    boolean isEnabled = home.btn_anteriorpag_inv.isEnabled();
                    if (isEnabled) {
                        pagina_actual--;
                        tabledates(txt_buscador_producto.getTextoReal(), pagina_actual);
                    }
                }
            }
        });

        //para validar de que la
        Tools.buscadorTablaValidate(home.txt_buscador_inventario, "Buscar producto..", (consulta) -> {
            pagina_actual = 0;
            tabledates(consulta, pagina_actual);
        });

        home.btn_nvprod_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                llenarCbxProveedor();
                llenarCbxCategoria();
                llenarCbxMedidas();
                cleanRegProductos();
                resetColorRegProductos();
                d_registrar.setVisible(true);
            }
        });

        panel.btn_guardar_rp_inv.addActionListener((e) -> {
            registrarProducto();
        });

        home.item_addStock_inv.addActionListener((e) -> {
            if (productoSelected != null) {
                if (productoSelected.getId() > 0) {
                    agregarStockProducto(productoSelected);
                } else {
                    JOptionPane.showMessageDialog(null, "Seleccione un producto");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione un producto");
            }
        });

        autoCompleterInventario = new TextAutoCompleter(home.txt_buscador_inventario, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {

            }
        });

        sugerenciasBuscadorInventario();

        Tools.focusWhite_comp(Arrays.asList(panel.txt_codigo_rp_inv, panel.txt_descripcion_rp_inv,
                panel.txt_precioVenta_rp_inv, panel.txt_preciocompra_rp_inv, panel.cbx_categoria_rp_inv,
                panel.cbx_medida_rp_inv, panel.cbx_proveedor_rp_inv));
    }

    /* --------------subseccion proveedores-------------------*/
    public void ListenerToWindowProveedor() {
        panel.btn_addproveedor_inv.addActionListener((e) -> {
            d_proveedor = Tools.newWindow(panel.jP_reg_proveedor_inv);
            actualizandoProveedor = false;
            panel.txt_nombreProv_inv.setBackground(Color.WHITE);
            panel.txt_nombreProv_inv.setText("");
            panel.btn_guardarProv_inv.setText("Guardar");
            dateTableProveedor();
            d_proveedor.setVisible(true);
        });

        panel.tabla_proveedor_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = panel.tabla_proveedor_inv.getSelectedRow();
                int id = Integer.parseInt(panel.tabla_proveedor_inv.getValueAt(row, 0).toString());

                proveedorSelected = proveedorSelected(id);
                actualizandoProveedor = true;

                datosProveedor();
            }
        });

        panel.btn_cancelarProv_inv.addActionListener((e) -> {
            proveedorSelected = new Proveedor_ob(0, "");
            actualizandoProveedor = false;

            panel.txt_nombreProv_inv.setBackground(Color.WHITE);
            panel.txt_nombreProv_inv.setText("");
            panel.btn_guardarProv_inv.setText("Guardar");

        });

        panel.btn_guardarProv_inv.addActionListener((e) -> {
            if (actualizandoProveedor) {
                modificarProveedor();
            } else {
                registrarProveedor();
            }
        });
    }

    /* --------------subseccion categorias-------------------*/
    public void ListenerToWindowCategoria() {
        panel.btn_addcategoria_inv.addActionListener((e) -> {
            d_categoria = Tools.newWindow(panel.jP_reg_categoria_inv);
            actualizandoCategoria = false;
            panel.txt_nombreCat_inv.setBackground(Color.WHITE);
            panel.txt_nombreCat_inv.setText("");
            panel.btn_guardarCat_inv.setText("Guardar");
            dateTableCategoria();
            d_categoria.setVisible(true);
        });

        panel.tabla_categoria_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = panel.tabla_categoria_inv.getSelectedRow();
                int id = Integer.parseInt(panel.tabla_categoria_inv.getValueAt(row, 0).toString());

                categoriaSelected = categoriaSelected(id);
                actualizandoCategoria = true;

                datosCategoria();
            }
        });

        panel.btn_cancelarCat_inv.addActionListener((e) -> {
            categoriaSelected = new Categoria_ob(0, "");
            actualizandoCategoria = false;

            panel.txt_nombreCat_inv.setBackground(Color.WHITE);
            panel.txt_nombreCat_inv.setText("");
            panel.btn_guardarCat_inv.setText("Guardar");

        });

        panel.btn_guardarCat_inv.addActionListener((e) -> {
            if (actualizandoCategoria) {
                modificarCategoria();
            } else {
                registrarCategoria();
            }
        });
    }

    /* --------------subseccion categorias-------------------*/
    public void ListenerToWindowMedida() {
        panel.btn_addmedida_inv.addActionListener((e) -> {
            d_medida = Tools.newWindow(panel.jP_reg_medida_inv);
            actualizandoMedida = false;
            panel.txt_nombreMed_inv.setBackground(Color.WHITE);
            panel.txt_nombreMed_inv.setText("");
            panel.txt_abrevMed_inv.setBackground(Color.WHITE);
            panel.txt_abrevMed_inv.setText("");
            panel.btn_guardarMed_inv_.setText("Guardar");
            dateTableMedida();
            d_medida.setVisible(true);
        });

        panel.tabla_medidas_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int row = panel.tabla_medidas_inv.getSelectedRow();
                int id = Integer.parseInt(panel.tabla_medidas_inv.getValueAt(row, 0).toString());

                medidaSelected = medidaSelected(id);
                actualizandoMedida = true;

                datosMedida();
            }
        });

        panel.btn_cancelarMed_inv.addActionListener((e) -> {
            medidaSelected = new Medida_ob(0, "", "");
            actualizandoMedida = false;

            panel.txt_nombreMed_inv.setBackground(Color.WHITE);
            panel.txt_nombreMed_inv.setText("");
            panel.txt_abrevMed_inv.setBackground(Color.WHITE);
            panel.txt_abrevMed_inv.setText("");
            panel.btn_guardarMed_inv_.setText("Guardar");

        });

        panel.btn_guardarMed_inv_.addActionListener((e) -> {
            if (actualizandoMedida) {
                modificarMedida();
            } else {
                registrarMedida();
            }
        });

        Tools.txt_sugerir(panel.txt_nombreMed_inv, panel.txt_abrevMed_inv);

        Tools.txt_precio(panel.txt_precioVenta_rp_inv);
        Tools.txt_precio(panel.txt_preciocompra_rp_inv);

    }

    /*---------------Listen to Win addStock------------------*/
    public void ListenerToWindowAddStock() {
        panel.btn_añadir_addstock.addActionListener((e) -> {
            añadirStock();
        });
    }

    /*------Metodos para controlar objetos visuales y no visuales en la interfaz grafica */
    public void customTable() {
        home.tableInventario.getTableHeader().setReorderingAllowed(false);
        home.tableInventario.setRowHeight(25);
        Tools.headers(home.tableInventario);

        panel.tabla_proveedor_inv.getTableHeader().setResizingAllowed(false);
        panel.tabla_proveedor_inv.setRowHeight(25);
        Tools.headers(panel.tabla_proveedor_inv);

        panel.tabla_categoria_inv.getTableHeader().setResizingAllowed(false);
        panel.tabla_categoria_inv.setRowHeight(25);
        Tools.headers(panel.tabla_categoria_inv);

        panel.tabla_medidas_inv.getTableHeader().setResizingAllowed(false);
        panel.tabla_medidas_inv.setRowHeight(25);
        Tools.headers(panel.tabla_medidas_inv);
    }

    public void cleanRegProductos() {
        Tools.clean(Arrays.asList(panel.txt_codigo_rp_inv, panel.txt_descripcion_rp_inv,
                panel.txt_precioVenta_rp_inv, panel.txt_preciocompra_rp_inv));
        panel.txt_precioVenta_rp_inv.setText("Q ");
        panel.txt_preciocompra_rp_inv.setText("Q ");
        panel.cbx_proveedor_rp_inv.setSelectedIndex(0);
        panel.cbx_medida_rp_inv.setSelectedIndex(0);
        panel.cbx_categoria_rp_inv.setSelectedIndex(0);
    }

    public void painGreenRegProductos() {
        Tools.paintgreen(Arrays.asList(panel.txt_codigo_rp_inv, panel.txt_descripcion_rp_inv,
                panel.txt_precioVenta_rp_inv, panel.txt_preciocompra_rp_inv, panel.cbx_categoria_rp_inv,
                panel.cbx_medida_rp_inv, panel.cbx_proveedor_rp_inv));

    }

    public void resetColorRegProductos() {
        Tools.resetOriginalColor(Arrays.asList(panel.txt_codigo_rp_inv, panel.txt_descripcion_rp_inv,
                panel.txt_precioVenta_rp_inv, panel.txt_preciocompra_rp_inv, panel.cbx_categoria_rp_inv,
                panel.cbx_medida_rp_inv, panel.cbx_proveedor_rp_inv));
    }

    public void agregarStockProducto(Producto_ob producto) {
        panel.jL_descripcion_addstock.setText(producto.getDescripcion());
        panel.jL_stock_addstock.setText(Tools.formatearStock(producto.getStock()));
        panel.jL_medida_addstock.setText(producto.getMedida());
        panel.txt_addstock_addstock.setText("0");

        d_addStock.setVisible(true);
    }

    public void editarProducto() {

    }

    /*---------- validacion de datos y conexion a la api de productos---------*/
    public void tabledates(String busqueda, int pagina) {
        pagina_actual = pagina;
        try {
            DefaultTableModel modelo = (DefaultTableModel) home.tableInventario.getModel();

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRequestPag valorRecuest = new ValorRequestPag(busqueda, pagina, 20);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/productos/listarProductos")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    Type type = new TypeToken<RespuestaPaginada<Producto_ob>>() {
                    }.getType();
                    RespuestaPaginada<Producto_ob> rs = gson.fromJson(json, type);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);

                        if (rs != null && rs.getContent() != null) {
                            for (Producto_ob producto : rs.getContent()) {
                                modelo.addRow(new Object[]{
                                    producto.getId(),
                                    producto.getCodigo(),
                                    producto.getDescripcion(),
                                    Tools.formatearStock(producto.getStock()),
                                    producto.getPrecioVentas(),
                                    producto.getPrecioCompra(),
                                    producto.getCategoria(),
                                    producto.getProveedor(),
                                    producto.getEstado()
                                });

                                home.btn_sigpag_inv.setEnabled(!rs.isLast());
                                home.btn_anteriorpag_inv.setEnabled(pagina > 0);
                                home.jLabel_pags_inv.setText("Pagina     " + (pagina + 1) + "     de    " + rs.getTotalPages());
                                sugerenciasBuscadorInventario();
                            }

                        }

                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }

    public void registrarProducto() {
        boolean val = true;
        String codigo = panel.txt_codigo_rp_inv.getText().trim();
        String descripcion = panel.txt_descripcion_rp_inv.getText();
        double precio_venta = Tools.getPrecioLimpio(panel.txt_precioVenta_rp_inv);
        double precio_compra = Tools.getPrecioLimpio(panel.txt_preciocompra_rp_inv);
        int id_proveedor = ((Proveedor_ob) panel.cbx_proveedor_rp_inv.getSelectedItem()).getId();
        int id_medida = ((Medida_ob) panel.cbx_medida_rp_inv.getSelectedItem()).getId();
        int id_categoria = ((Categoria_ob) panel.cbx_categoria_rp_inv.getSelectedItem()).getId();

        if (codigo.trim().isEmpty()) {
            panel.txt_codigo_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (descripcion.trim().isEmpty()) {
            panel.txt_descripcion_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (id_proveedor == 0) {
            panel.cbx_proveedor_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (id_medida == 0) {
            panel.cbx_medida_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (id_categoria == 0) {
            panel.cbx_categoria_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (precio_venta == 0) {
            panel.txt_precioVenta_rp_inv.setBackground(Color.red);
            val = false;
        }
        if (precio_compra == 0) {
            panel.txt_preciocompra_rp_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            if (precio_compra > precio_venta) {
                panel.txt_preciocompra_rp_inv.setBackground(Color.red);
                JOptionPane.showMessageDialog(null, "¡El precio de compra no puede ser mayor al de venta!");
            } else {
                try {
                    //conexion a la api
                    ProductoRegistrar productoRegistrar = new ProductoRegistrar(
                            codigo, descripcion, precio_compra, precio_venta,
                            id_proveedor, id_medida, id_categoria);
                    String json = gson.toJson(productoRegistrar);

                    RequestBody body = RequestBody.create(json, JSON);

                    Request request = new Request.Builder()
                            .url(General.properties.getUrl() + "/api/productos/registrar")
                            .post(body)
                            .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException ioe) {
                            UtilMessage.messageError("Error en la conexion " + ioe);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String json = response.body().string();
                            int code = response.code();

                            if (response.isSuccessful()) {

                                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<Producto_ob>>() {
                                }.getType();

                                DtoResponseOb<Producto_ob> dto = gson.fromJson(json, type);
                                if (dto.isSuccess()) {
                                    SwingUtilities.invokeLater(() -> {

                                        agregarStockProducto(dto.getData());

                                        painGreenRegProductos();
                                        cleanRegProductos();

                                        tabledates(txt_buscador_producto.getTextoReal(), pagina_actual);

                                        UtilMessage.messageAprobation(dto.getMessage());

                                    });

                                } else {
                                    UtilMessage.message(dto.getMessage());
                                }
                            } else {
                                if (code == 400) {
                                    DtoResponseOb<Producto> dto = gson.fromJson(json, DtoResponseOb.class);
                                    UtilMessage.messageWarning(dto.getMessage());
                                } else {
                                    String error = General.Error.parseJsonError(json);
                                    UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error " + e);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }

    public Producto_ob productoSelected(int id_producto) {
        try {
            //Conexion a la api
            RequestBody body = RequestBody.create(String.valueOf(id_producto), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/productos/productoSelected")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Producto_ob producto = gson.fromJson(json, Producto_ob.class);
                return producto;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        JOptionPane.showMessageDialog(null, "Algo está saliedo mal");
        return null;
    }

    public void sugerenciasBuscadorInventario() {
        try {
            //conexion a api
            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/productos/listarSugerencias")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> sugerencias = gson.fromJson(json, type);

                    SwingUtilities.invokeLater(() -> {
                        autoCompleterInventario.removeAllItems();

                        for (String string : sugerencias) {
                            autoCompleterInventario.addItem(string);
                        }
                    });

                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }

    public void añadirStock() {
        boolean val = true;
        double stock = Double.parseDouble(panel.txt_addstock_addstock.getText().trim());

        if (stock <= 0) {
            val = false;
            panel.txt_addstock_addstock.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "El stock a añadir debe ser mayor a 0 ");
        }
        if (val) {
            try {
                //conexion a la api
                DtoAddStock addstock = new DtoAddStock(productoSelected.getId(), stock);
                String json = gson.toJson(addstock);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/productos/añadirstock")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ioe) {
                        UtilMessage.messageError("Error en la conexion " + ioe);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        int code = response.code();

                        if (response.isSuccessful()) {

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<Producto_ob>>() {
                            }.getType();

                            DtoResponseOb<Producto_ob> dto = gson.fromJson(json, type);
                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {

                                    d_addStock.dispose();

                                    tabledates(txt_buscador_producto.getTextoReal(), pagina_actual);

                                    UtilMessage.messageAprobation(dto.getMessage());

                                });

                            } else {
                                UtilMessage.message(dto.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                DtoResponseOb<Producto> dto = gson.fromJson(json, DtoResponseOb.class);
                                UtilMessage.messageWarning(dto.getMessage());
                            } else {
                                String error = General.Error.parseJsonError(json);
                                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                            }
                        }
                    }
                });
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error " + e);
            }
        }
    }

    /*------------------------Validacion de datos y conexion a la api de proveedor--------*/
    //llenar cbx_proveedor de ventana registrar
    private void llenarCbxProveedor() {
        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/proveedores/listarProveedor")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Proveedor_ob>>() {
                    }.getType();
                    List<Proveedor_ob> proveedores = gson.fromJson(json, listType);
                    SwingUtilities.invokeLater(() -> {
                        panel.cbx_proveedor_rp_inv.removeAllItems();
                        panel.cbx_proveedor_rp_inv.addItem(new Proveedor_ob(0, "Seleccione un opción"));
                        for (Proveedor_ob proveedor : proveedores) {
                            panel.cbx_proveedor_rp_inv.addItem(proveedor);
                        }
                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //llenar tablaproveedor en inventario
    private void dateTableProveedor() {
        DefaultTableModel modelo = (DefaultTableModel) panel.tabla_proveedor_inv.getModel();

        List<Object[]> datos = new ArrayList<Object[]>();

        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/proveedores/listarProveedor")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Proveedor_ob>>() {
                    }.getType();
                    List<Proveedor_ob> proveedores = gson.fromJson(json, listType);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Proveedor_ob proveedor : proveedores) {
                            modelo.addRow(new Object[]{
                                proveedor.getId(),
                                proveedor.getNombre()
                            });
                            panel.tabla_proveedor_inv.setModel(modelo);
                        }

                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //retorna un objeto proveedor desde la api
    private Proveedor_ob proveedorSelected(long id) {
        try {
            //Conexion a la api

            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/proveedores/proveedorSelected")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Proveedor_ob proveedor = gson.fromJson(json, Proveedor_ob.class);
                return proveedor;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }

    //Llenar datos a actualizar en ventana de proveedor
    public void datosProveedor() {
        proveedorSelected = proveedorSelected(proveedorSelected.getId());

        panel.txt_nombreProv_inv.setBackground(Color.WHITE);
        panel.txt_nombreProv_inv.setText(proveedorSelected.getNombre());
        panel.btn_guardarProv_inv.setText("Actualizar");

    }

    //conexion para registrar un nuevo proveedor
    public void registrarProveedor() {
        boolean val = true;
        String nombre = panel.txt_nombreProv_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreProv_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            //conexion a la api

            ProveedorRegistrar proveedorRegistrar = new ProveedorRegistrar(nombre);
            String json = gson.toJson(proveedorRegistrar);

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/proveedores/registrar")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    int code = response.code();

                    if (response.isSuccessful()) {

                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        if (requestMessage.isSuccess()) {
                            SwingUtilities.invokeLater(() -> {

                                panel.txt_nombreProv_inv.setBackground(Color.GREEN);
                                panel.txt_nombreProv_inv.setText("");
                                actualizandoProveedor = false;

                                dateTableProveedor();
                                llenarCbxProveedor();
                                UtilMessage.messageAprobation(requestMessage.getMessage());
                            });

                        } else {
                            UtilMessage.message(requestMessage.getMessage());
                        }
                    } else {
                        if (code == 400) {
                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            UtilMessage.messageWarning(requestMessage.getMessage());
                        } else {
                            String error = General.Error.parseJsonError(json);
                            UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                        }
                    }
                }
            });

        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }

    //conexion para modificar un proveedor
    public void modificarProveedor() {
        boolean val = true;
        String nombre = panel.txt_nombreProv_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreProv_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            if (proveedorSelected.getId() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un fila");
            } else {
                //conexion a la api

                Proveedor_ob proveedorModificar = new Proveedor_ob(proveedorSelected.getId(), nombre);
                String json = gson.toJson(proveedorModificar);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/proveedores/modificar")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ioe) {
                        UtilMessage.messageError("Error en la conexion " + ioe);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        int code = response.code();

                        if (response.isSuccessful()) {

                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            if (requestMessage.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {

                                    panel.txt_nombreProv_inv.setBackground(Color.GREEN);
                                    panel.txt_nombreProv_inv.setText("");
                                    actualizandoProveedor = false;
                                    panel.btn_guardarProv_inv.setText("Guardar");

                                    dateTableProveedor();
                                    llenarCbxProveedor();
                                    UtilMessage.messageAprobation(requestMessage.getMessage());
                                });

                            } else {
                                UtilMessage.message(requestMessage.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                                UtilMessage.messageWarning(requestMessage.getMessage());
                            } else {
                                String error = General.Error.parseJsonError(json);
                                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                            }
                        }
                    }
                });
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }

    }

    /*------------------------Validacion de datos y conexion a la api de categorias---------*/
    //llenar cbx_categoria de ventana registrar
    private void llenarCbxCategoria() {
        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/categorias/listarCategoria")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Categoria_ob>>() {
                    }.getType();
                    List<Categoria_ob> categorias = gson.fromJson(json, listType);
                    SwingUtilities.invokeLater(() -> {
                        panel.cbx_categoria_rp_inv.removeAllItems();
                        panel.cbx_categoria_rp_inv.addItem(new Categoria_ob(0, "Seleccione un opción"));
                        for (Categoria_ob categoria : categorias) {
                            panel.cbx_categoria_rp_inv.addItem(categoria);
                        }
                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //llenar tablacategoria en inventario
    private void dateTableCategoria() {
        DefaultTableModel modelo = (DefaultTableModel) panel.tabla_categoria_inv.getModel();

        List<Object[]> datos = new ArrayList<Object[]>();

        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/categorias/listarCategoria")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Categoria_ob>>() {
                    }.getType();
                    List<Categoria_ob> categorias = gson.fromJson(json, listType);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Categoria_ob categoria : categorias) {
                            modelo.addRow(new Object[]{
                                categoria.getId(),
                                categoria.getNombre()
                            });
                            panel.tabla_categoria_inv.setModel(modelo);
                        }

                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //retorna un objeto categoria desde la api
    private Categoria_ob categoriaSelected(long id) {
        try {
            //Conexion a la api

            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/categorias/categoriaSelected")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Categoria_ob categoria = gson.fromJson(json, Categoria_ob.class);
                return categoria;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }

    //Llenar datos a actualizar en ventana de categoria
    public void datosCategoria() {
        categoriaSelected = categoriaSelected(categoriaSelected.getId());

        panel.txt_nombreCat_inv.setBackground(Color.WHITE);
        panel.txt_nombreCat_inv.setText(categoriaSelected.getNombre());
        panel.btn_guardarCat_inv.setText("Actualizar");

    }

    //conexion para registrar una nueva categoria
    public void registrarCategoria() {
        boolean val = true;
        String nombre = panel.txt_nombreCat_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreCat_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            //conexion a la api

            CategoriaRegistrar categoriaRegistrar = new CategoriaRegistrar(nombre);
            String json = gson.toJson(categoriaRegistrar);

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/categorias/registrar")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    int code = response.code();

                    if (response.isSuccessful()) {

                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        if (requestMessage.isSuccess()) {
                            SwingUtilities.invokeLater(() -> {

                                panel.txt_nombreCat_inv.setBackground(Color.GREEN);
                                panel.txt_nombreCat_inv.setText("");
                                actualizandoCategoria = false;

                                dateTableCategoria();
                                llenarCbxCategoria();
                                UtilMessage.messageAprobation(requestMessage.getMessage());
                            });

                        } else {
                            UtilMessage.message(requestMessage.getMessage());
                        }
                    } else {
                        if (code == 400) {
                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            UtilMessage.messageWarning(requestMessage.getMessage());
                        } else {
                            String error = General.Error.parseJsonError(json);
                            UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                        }
                    }
                }
            });

        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }

    //conexion para modificar una categoria
    public void modificarCategoria() {
        boolean val = true;
        String nombre = panel.txt_nombreCat_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreCat_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            if (categoriaSelected.getId() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un fila");
            } else {
                //conexion a la api

                Categoria_ob categoriaModificar = new Categoria_ob(categoriaSelected.getId(), nombre);
                String json = gson.toJson(categoriaModificar);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/categorias/modificar")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ioe) {
                        UtilMessage.messageError("Error en la conexion " + ioe);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        int code = response.code();

                        if (response.isSuccessful()) {

                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            if (requestMessage.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {

                                    panel.txt_nombreCat_inv.setBackground(Color.GREEN);
                                    panel.txt_nombreCat_inv.setText("");
                                    actualizandoCategoria = false;
                                    panel.btn_guardarCat_inv.setText("Guardar");

                                    dateTableCategoria();
                                    llenarCbxCategoria();
                                    UtilMessage.messageAprobation(requestMessage.getMessage());
                                });

                            } else {
                                UtilMessage.message(requestMessage.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                                UtilMessage.messageWarning(requestMessage.getMessage());
                            } else {
                                String error = General.Error.parseJsonError(json);
                                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                            }
                        }
                    }
                });
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }

    }

    /*------------------------Validacion de datos y conexion a la api de medidas---------*/
    //llenar cbx_medidas de ventana registrar
    private void llenarCbxMedidas() {
        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/medidas/listarMedida")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Medida_ob>>() {
                    }.getType();
                    List<Medida_ob> medidas = gson.fromJson(json, listType);
                    SwingUtilities.invokeLater(() -> {
                        panel.cbx_medida_rp_inv.removeAllItems();
                        panel.cbx_medida_rp_inv.addItem(new Medida_ob(0, "Seleccione un opción", ""));
                        for (Medida_ob medida : medidas) {
                            panel.cbx_medida_rp_inv.addItem(medida);
                        }
                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //llenar tablamedida en inventario
    private void dateTableMedida() {
        DefaultTableModel modelo = (DefaultTableModel) panel.tabla_medidas_inv.getModel();

        List<Object[]> datos = new ArrayList<Object[]>();

        //Conexion a la api
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/medidas/listarMedida")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                UtilMessage.messageError("Error en la conexion " + ioe);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Medida_ob>>() {
                    }.getType();
                    List<Medida_ob> medidas = gson.fromJson(json, listType);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for (Medida_ob medida : medidas) {
                            modelo.addRow(new Object[]{
                                medida.getId(),
                                medida.getNombre(),
                                medida.getAbreviatura()
                            });
                            panel.tabla_medidas_inv.setModel(modelo);
                        }

                    });
                } else {
                    if (code == 400) {
                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        UtilMessage.messageWarning(requestMessage.getMessage());
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            }
        });
    }

    //retorna un objeto medida desde la api
    private Medida_ob medidaSelected(long id) {
        try {
            //Conexion a la api

            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/medidas/medidaSelected")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Medida_ob medida = gson.fromJson(json, Medida_ob.class);
                return medida;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }

    //Llenar datos a actualizar en ventana de medida
    public void datosMedida() {
        medidaSelected = medidaSelected(medidaSelected.getId());

        panel.txt_nombreMed_inv.setBackground(Color.WHITE);
        panel.txt_nombreMed_inv.setText(medidaSelected.getNombre());
        panel.txt_abrevMed_inv.setBackground(Color.WHITE);
        panel.txt_abrevMed_inv.setText(medidaSelected.getAbreviatura());
        panel.btn_guardarMed_inv_.setText("Actualizar");

    }

    //conexion para registrar una nueva medida
    public void registrarMedida() {
        boolean val = true;
        String nombre = panel.txt_nombreMed_inv.getText();
        String abreviatura = panel.txt_abrevMed_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreCat_inv.setBackground(Color.red);
            val = false;
        }
        if (abreviatura.trim().isEmpty()) {
            panel.txt_abrevMed_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            //conexion a la api

            MedidaRegistrar medidaRegistrar = new MedidaRegistrar(nombre, abreviatura);
            String json = gson.toJson(medidaRegistrar);

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/medidas/registrar")
                    .post(body)
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    int code = response.code();

                    if (response.isSuccessful()) {

                        RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                        if (requestMessage.isSuccess()) {
                            SwingUtilities.invokeLater(() -> {

                                panel.txt_nombreMed_inv.setBackground(Color.GREEN);
                                panel.txt_nombreMed_inv.setText("");
                                panel.txt_abrevMed_inv.setBackground(Color.GREEN);
                                panel.txt_abrevMed_inv.setText("");
                                actualizandoMedida = false;

                                dateTableMedida();
                                llenarCbxMedidas();
                                UtilMessage.messageAprobation(requestMessage.getMessage());
                            });

                        } else {
                            UtilMessage.message(requestMessage.getMessage());
                        }
                    } else {
                        if (code == 400) {
                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            UtilMessage.messageWarning(requestMessage.getMessage());
                        } else {
                            String error = General.Error.parseJsonError(json);
                            UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                        }
                    }
                }
            });

        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }

    //conexion para modificar una medida
    public void modificarMedida() {
        boolean val = true;
        String nombre = panel.txt_nombreMed_inv.getText();
        String abreviatura = panel.txt_abrevMed_inv.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombreMed_inv.setBackground(Color.red);
            val = false;
        }
        if (abreviatura.trim().isEmpty()) {
            panel.txt_abrevMed_inv.setBackground(Color.red);
            val = false;
        }

        if (val) {
            if (medidaSelected.getId() == 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un fila");
            } else {
                //conexion a la api

                Medida_ob medidaModificar = new Medida_ob(medidaSelected.getId(), nombre, abreviatura);
                String json = gson.toJson(medidaModificar);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/medidas/modificar")
                        .post(body)
                        .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException ioe) {
                        UtilMessage.messageError("Error en la conexion " + ioe);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String json = response.body().string();
                        int code = response.code();

                        if (response.isSuccessful()) {

                            RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                            if (requestMessage.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {

                                    panel.txt_nombreMed_inv.setBackground(Color.GREEN);
                                    panel.txt_nombreMed_inv.setText("");
                                    panel.txt_abrevMed_inv.setBackground(Color.GREEN);
                                    panel.txt_abrevMed_inv.setText("");
                                    actualizandoMedida = false;
                                    panel.btn_guardarMed_inv_.setText("Guardar");

                                    dateTableMedida();
                                    llenarCbxMedidas();
                                    UtilMessage.messageAprobation(requestMessage.getMessage());
                                });

                            } else {
                                UtilMessage.message(requestMessage.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                RequestMessage requestMessage = gson.fromJson(json, RequestMessage.class);
                                UtilMessage.messageWarning(requestMessage.getMessage());
                            } else {
                                String error = General.Error.parseJsonError(json);
                                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                            }
                        }
                    }
                });
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }

    }

    /*--------clases extras -----------*/
    public void configurarAtajo() {
        // 1. Definir el nombre de la acción
        String nombreAccion = "abrirOtraPantalla";

        // 2. Obtener el InputMap para cuando el diálogo tenga el foco
        InputMap im = d_addStock.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = d_addStock.getRootPane().getActionMap();

        // 3. Registrar la combinación de teclas (Ejemplo: CTRL + F)
        
        // 4. Definir qué hará esa acción
        am.put(nombreAccion, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Pantalla editar");
            }
        });
    }

}

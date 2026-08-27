package Secciones;

import Clases.Cargando;
import Clases.ListaClientesRep;
import Clases.Tools;
import Clases.Txt_buscador;
import Clases.X;
import Dto.DtoIdVenta;
import Dto.DtoItemSugerenciaProductos;
import Dto.DtoPedido;
import Dto.DtoVentaContado;
import Dto.DtoResponseOb;
import Dto.DtoVentaCredito;
import Dto.DtoVentaDeposito;
import General.UtilMessage;
import General.userAuth;
import Objects.Cliente_ob;
import Objects.Pedido_ob;
import Objects.ProductoRegistrar;
import Objects.Producto_ob;
import Objects.Turno_ob;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mxrck.autocompleter.AutoCompleterCallback;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jfree.data.jdbc.JDBCCategoryDataset;

public class sub_nuevaventa {

    private Ventas v;

    private Txt_buscador buscadorProductosVentas;
    private List<Pedido_ob> preVenta = new ArrayList<>();
    private TextAutoCompleter autoCompleterNuevaventa;

    private JDialog d_addProducto;
    private JDialog d_finalizarventa;
    private JDialog d_descuentos;

    private Producto_ob productoSelected = null;
    private Producto_ob productoDescuento = null;

    private boolean existProductPedido = false;

    private List<DtoItemSugerenciaProductos> sugerenciasWin;
    private List<Cliente_ob> Clientes;

    private Timer timer = new Timer(0, null);

    private boolean bloqueadoPorAutoCompleter = false;
    private boolean isClienteSelected = false;

    private int idPedidoSelected = 0;

    private DtoVentaContado dtoRegistrar;

    private TextAutoCompleter autocompleterClientes;
    private BigDecimal descuento = BigDecimal.ZERO;
    private BigDecimal precioFinal = BigDecimal.ZERO;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //variables para la opcion de ventas con deposito
    private File fileComprobante = null;
    private boolean isAddComprobante = false;
    
    //variables para la opcion de ventas a credito
     private String plazoDePago = "";
    
    public sub_nuevaventa(Ventas ventas) {
        this.v = ventas;

        Tools.focusWhite_comp(Arrays.asList(v.panel.txt_nit_finalizarventa, v.panel.txt_nombre_finalizarventa, v.panel.txt_noDeposito_finalizarventa));

        v.home.turnos.ValidarTurnoUserAuth();
        if (userAuth.getIdTurnoActivo() > 0) {
            Turno_ob turno = v.home.turnos.turnoSelected(userAuth.getIdTurnoActivo());
            v.home.jL_info_nuevaventa.setText("Usuario: " + userAuth.getUsuario().getNombre() + " | "
                    + "Caja: " + turno.getCaja().getNombre() + " | "
                    + "Turno nro: " + turno.getId());
        } else {
            v.home.jTabbedPane1.setSelectedComponent(v.home.jP_turno);
            Cargando.doSomething(() -> {
                v.home.turnos.ValidarTurnoUserAuth();
                v.home.turnos.llenarInfoTurno();
                v.home.turnos.tabledates("", 0);
            });
        }

        Tools.diseñotabla1(v.home.tableNuevaVenta);
        Tools.txt_cantidad(v.panel.txt_addstock_nuevaventa);
        v.panel.txt_addstock_nuevaventa.setText("0");

        buscadorProductosVentas = new Txt_buscador("Buscar por codigo o descripcion..",
                v.home.txt_buscador_historialventa);

        d_addProducto = Tools.newWindow(v.panel.jP_infoproducto_nuevaventa);
        d_addProducto.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        d_finalizarventa = Tools.newWindow(v.panel.jP_finalizarVenta_nuevaventa);
        d_descuentos = Tools.newWindow(v.panel.jP_descuento_nuevaventa);

        Tools.txt_precio(v.panel.txt_efectivo_finalizarventa);

        sugerenciasBuscadorInventario();

        autoCompleterNuevaventa = new TextAutoCompleter(v.home.txt_buscador_historialventa, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {
                bloqueadoPorAutoCompleter = true;

                DtoItemSugerenciaProductos dto = (DtoItemSugerenciaProductos) o;

                productoSelected = v.home.inventario.productoSelected(dto.getId());

                SwingUtilities.invokeLater(() -> {
                    winAddProducto();
                });

                bloqueadoPorAutoCompleter = false;
            }
        });

        autocompleterClientes = new TextAutoCompleter(v.panel.txt_nombre_finalizarventa, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {
                Cliente_ob cliente = (Cliente_ob) o;

                v.panel.txt_nit_finalizarventa.setText(cliente.getNit());
                validarnit();
                v.panel.txt_nit_finalizarventa.requestFocusInWindow();
            }
        });

        Listeners();
    }

    private void Listeners() {
        v.home.btn_generar_nuevaventa.addActionListener(e -> {
            validarVenta();
        });
        v.panel.btn_añadir_nuevaventa.addActionListener(e -> {
            Cargando.doSomething(() -> {
                añadirProductoManual();
            });
        });
        v.home.txt_buscador_historialventa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String texto = v.home.txt_buscador_historialventa.getText().trim();

                    procesarEntrada(texto);
                } else {

                }
            }
        });
        d_addProducto.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                productoSelected = null;
            }
        });
        v.home.tableNuevaVenta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = v.home.tableNuevaVenta.getSelectedRow();
                if (row >= 0) {
                    idPedidoSelected = Integer.parseInt(v.home.tableNuevaVenta
                            .getValueAt(row, 0).toString());
                }
            }
        });
        v.home.jM_eliminar_nuevaventa.addActionListener(e -> {
            if (idPedidoSelected < 1) {
                JOptionPane.showMessageDialog(v.home, "Seleccione una fila");
                return;
            }

            int res = JOptionPane.showConfirmDialog(null,
                    "¿Deseas eliminar este producto?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE); // Cambiamos el icono a advertencia

            // 3. Ejecución
            if (res == JOptionPane.YES_OPTION) {
                preVenta.removeIf(p -> p.getId_producto() == idPedidoSelected);
                activarTable();
            }
        });
        v.home.jM_eliminartodo_nuevaventa.addActionListener(e -> {
            if (preVenta.isEmpty()) {
                JOptionPane.showMessageDialog(v.home, "La lista ya está vacía.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2. Mensaje de confirmación mejorado
            int res = JOptionPane.showConfirmDialog(null,
                    "¿Deseas eliminar todos los productos de la lista actual?\nEsta acción no se puede deshacer.",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE); // Cambiamos el icono a advertencia

            // 3. Ejecución
            if (res == JOptionPane.YES_OPTION) {
                EliminarTodoTableNventa();
            }
        });

        v.panel.cbx_metodopago_finalizarventa.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                validarmetodoPago();
            }
        });

        v.panel.txt_nit_finalizarventa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                validarnit();
            }
        });

        v.panel.txt_efectivo_finalizarventa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                BigDecimal efectivo = new BigDecimal(Tools
                        .getPrecioLimpio(v.panel.txt_efectivo_finalizarventa));
                BigDecimal totalapagar = dtoRegistrar.getTotalApagar();
                BigDecimal cambio = efectivo.subtract(totalapagar);

                v.panel.txt_cambio_finalizarventa.setText("Q. " + cambio.toPlainString());
            }
        });

        v.home.jM_descuento_nuevaventa.addActionListener(e -> {
            WinDescuentos();
        });

        v.panel.cbx_tipodescuento_descuento.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (v.panel.cbx_tipodescuento_descuento.getSelectedIndex() == 0) {
                    removeAllKeyListener(v.panel.txt_valordescontar_descuento);
                    Tools.txt_precio(v.panel.txt_valordescontar_descuento);
                    txt_descuento(v.panel.txt_valordescontar_descuento);
                    v.panel.txt_valordescontar_descuento.setText("Q 0.00");
                } else {
                    removeAllKeyListener(v.panel.txt_valordescontar_descuento);
                    Tools.txt_porcentaje(v.panel.txt_valordescontar_descuento);
                    txt_descuento(v.panel.txt_valordescontar_descuento);
                    v.panel.txt_valordescontar_descuento.setText("% 0");
                }
            }
        });

        v.panel.btn_aplicardescuento_descuento.addActionListener(e -> {
            AplicarDescuento();
        });

        v.panel.btn_addcombrobante_finalizarventa.addActionListener(e -> {
            selectComprobanteDeposito();
        });

        v.panel.btn_confirmarventa_finalizarventa.addActionListener(e -> {
            Cargando.doSomething(() -> {
                String metodoPago = v.panel.cbx_metodopago_finalizarventa.getSelectedItem().toString();

                if (metodoPago.equals("Contado")) {
                    RegistrarVentaContado();
                } else if (metodoPago.equals("Credito")) {
                    RegistrarVentaCredito();
                } else {
                    RegistrarVentaDeposito();
                }
            });
        });

        v.panel.jD_plazoDePago_finalizarventa.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Date plazo = v.panel.jD_plazoDePago_finalizarventa.getDate();
                if (plazo != null) {
                    LocalDate plazoLD = plazo.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate hoy = LocalDate.now();

                    if (!plazoLD.isBefore(hoy)) {
                        
                        String fecha = new SimpleDateFormat("yyyy-MM-dd")
                                .format(plazo);

                        String fechaview = new SimpleDateFormat("dd/MM/yyyy")
                                .format(plazo);
                        
                        plazoDePago = fecha;
                        v.panel.txt_plazoDePago_finalizarventa.setText(fechaview);
                        v.panel.jD_plazoDePago_finalizarventa.requestFocus();
                    }else{
                        JOptionPane.showMessageDialog(null, "¡La fecha seleccionada no puede ser anterior a hoy!");
                    }
                }
            }
        });

    }

    private void validarmetodoPago() {
        String metodo = v.panel.cbx_metodopago_finalizarventa.getSelectedItem().toString();

        CardLayout cl = (CardLayout) (v.panel.jP_detallespago_finalizarventa.getLayout());
        cl.show(v.panel.jP_detallespago_finalizarventa, metodo);

    }

    private void validarnit() {
        String nit = v.panel.txt_nit_finalizarventa.getText().trim();

        Cliente_ob cli = Clientes.stream()
                .filter(c -> c.getNit().equals(nit))
                .findFirst()
                .orElse(null);

        if (cli != null) {
            v.panel.txt_nombre_finalizarventa.setText(cli.getNombre());
            v.panel.txt_nombre_finalizarventa.setEditable(false);
            v.panel.jL_registrado_finalizarventa.setVisible(true);
            isClienteSelected = true;
        } else {
            v.panel.txt_nombre_finalizarventa.setText("");
            v.panel.txt_nombre_finalizarventa.setEditable(true);
            v.panel.jL_registrado_finalizarventa.setVisible(false);
            isClienteSelected = false;
        }
    }

    private void EliminarTodoTableNventa() {
        preVenta.clear();
        activarTable();
    }

    private void buscarEnSugerencias(String input) {
        if (productoSelected != null) {
            SwingUtilities.invokeLater(() -> {
                winAddProducto();
            });
            return;
        }

        DtoItemSugerenciaProductos sugerencia = sugerenciasWin.stream()
                .filter(d -> d.getSugerencia().equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);

        if (sugerencia != null) {
            productoSelected = v.home.inventario.productoSelected(sugerencia.getId());
            SwingUtilities.invokeLater(() -> {
                winAddProducto();
            });
        } else {
            mostrarErrorEscaner("Producto no encontrado");
        }
    }

    private void procesarEntrada(String input) {
        final String inputFinal = input;
        if (input.trim().isEmpty()) {
            return;
        }

        // 1. Si el escáner detecta un código de barras, tiene prioridad absoluta.
        // Los códigos de barras son rápidos y no necesitan autocompletar.
        if (input.matches("\\d{8,14}")) {
            v.home.txt_buscador_historialventa.setText("");
            añadirProductoAutomatico(input.trim());
            return;
        }

        // 2. Si no es código de barras, es una búsqueda manual.
        // Usamos una bandera para que el callback del AutoCompleter 
        // nos avise si ya procesó la selección.
        // Pequeño retardo (50ms) para dejar que el AutoCompleter reaccione al Enter
        javax.swing.Timer delay = new javax.swing.Timer(50, e -> {
            if (!bloqueadoPorAutoCompleter) {
                buscarEnSugerencias(inputFinal);
            }
        });

        delay.setRepeats(false);
        delay.start();
    }

    public boolean initNuevaVenta() {
        v.home.turnos.ValidarTurnoUserAuth();
        if (userAuth.getIdTurnoActivo() > 0) {
            Turno_ob turno = v.home.turnos.turnoSelected(userAuth.getIdTurnoActivo());
            v.home.jL_info_nuevaventa.setText("Usuario: " + userAuth.getUsuario().getNombre() + " | "
                    + "Caja: " + turno.getCaja().getNombre() + " | "
                    + "Turno nro: " + turno.getId());
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Es necesario Abrir un turno");
            v.home.jTabbedPane1.setSelectedComponent(v.home.jP_turno);
            Cargando.doSomething(() -> {
                v.home.turnos.ValidarTurnoUserAuth();
                v.home.turnos.llenarInfoTurno();
                v.home.turnos.tabledates("", 0);
            });
            return false;
        }
    }

    private void activarTable() {
        DefaultTableModel model = (DefaultTableModel) v.home.tableNuevaVenta.getModel();
        model.setRowCount(0);

        if (preVenta.size() < 1) {
            v.home.tableNuevaVenta.setModel(model);
            v.home.jL_totalapagar_nuevaventa.setText("Total a pagar Q. 0.00");
            return;
        }

        for (Pedido_ob pedido : preVenta) {
            model.addRow(new Object[]{
                pedido.getId_producto(),
                pedido.getCodigo(),
                pedido.getDescripcion(),
                pedido.getCantidad().toPlainString(),
                "Q. " + pedido.getPrecio().toPlainString(),
                "Q. " + pedido.getDescuentos().toPlainString(),
                "Q. " + pedido.getPreciofinal().toPlainString(),
                "Q. " + pedido.getSubtotal().toPlainString()});
        }

        boolean isDescuentoAplicado = preVenta.stream()
                .anyMatch(Pedido_ob::isDescuentoAplicado);

        var columnDescuentos = v.home.tableNuevaVenta.getColumnModel().getColumn(5);
        var columnPrecioFinal = v.home.tableNuevaVenta.getColumnModel().getColumn(6);

        if (isDescuentoAplicado) {
            columnDescuentos.setMinWidth(50);
            columnDescuentos.setMaxWidth(200);
            columnDescuentos.setPreferredWidth(80);

            columnPrecioFinal.setMinWidth(50);
            columnPrecioFinal.setMaxWidth(200);
            columnPrecioFinal.setPreferredWidth(80);
        } else {
            columnDescuentos.setMinWidth(0);
            columnDescuentos.setMaxWidth(0);
            columnDescuentos.setPreferredWidth(0);

            columnPrecioFinal.setMinWidth(0);
            columnPrecioFinal.setMaxWidth(0);
            columnPrecioFinal.setPreferredWidth(0);
        }

        v.home.tableNuevaVenta.setModel(model);

        BigDecimal Total = BigDecimal.ZERO;
        Total = Total.setScale(2, RoundingMode.HALF_UP);
        for (Pedido_ob p : preVenta) {
            Total = Total.add(p.getSubtotal());
        }

        v.home.jL_totalapagar_nuevaventa.setText("Total a pagar Q. " + Total.toPlainString());
    }

    private void mostrarErrorEscaner(String error) {
        v.home.jL_error_nuevaventa.setText(error);

        timer = new Timer(5000, e -> {
            v.home.jL_error_nuevaventa.setText("");
        });

        timer.setRepeats(false);
        timer.start();
    }

    private void añadirProductoAutomatico(String codigo) {
        if (timer.isRunning()) {
            v.home.jL_error_nuevaventa.setText("");
            timer.stop();
        }
        DtoItemSugerenciaProductos resultado = sugerenciasWin.stream()
                .filter(d -> d.getSugerencia().equals(codigo))
                .findFirst()
                .orElse(null);

        if (resultado == null) {
            mostrarErrorEscaner("¡Producto no encontrado!");
            return;
        }

        Producto_ob producto = v.home.inventario.productoSelected(resultado.getId());

        if (producto == null) {
            mostrarErrorEscaner("¡Producto no encontrado!");
            return;
        }

        BigDecimal cantidad = BigDecimal.ONE;
        BigDecimal precio = new BigDecimal(producto.getPrecio_venta());

        addProducto(new Pedido_ob(producto.getId(),
                producto.getCodigo(),
                producto.getDescripcion(),
                cantidad,
                precio));

    }

    private void añadirProductoManual() {
        boolean val = true;
        BigDecimal stockAñadir = new BigDecimal(v.panel.txt_addstock_nuevaventa.getText().trim());
        BigDecimal stockActual = new BigDecimal(productoSelected.getStock());
        BigDecimal precio = new BigDecimal(productoSelected.getPrecio_venta());
        if (stockAñadir.compareTo(BigDecimal.ZERO) <= 0) {
            val = false;
            JOptionPane.showMessageDialog(null, "¡La cantidad debe ser mayor a 0!");
            return;
        }
        if (stockAñadir.compareTo(stockActual) > 0) {
            val = false;
            JOptionPane.showMessageDialog(null, "¡La cantidad ingresada supera al stock disponible!");
            return;
        }
        if (val) {
            addProducto(new Pedido_ob(productoSelected.getId(),
                    productoSelected.getCodigo(),
                    productoSelected.getDescripcion(),
                    stockAñadir,
                    precio));
            d_addProducto.dispose();
        }
    }

    private void addProducto(Pedido_ob pedido) {
        if (preVenta.size() >= 30) {
            JOptionPane.showMessageDialog(null, "Has alcanzado el límite recomendado de ítems por factura. "
                    + "Considera generar una nueva factura para mayor claridad.");
            return;
        }
        Pedido_ob existente = preVenta.stream()
                .filter(p -> p.getId_producto() == pedido.getId_producto())
                .findFirst()
                .orElse(null);

        if (existente != null) {
            BigDecimal nuevoStock = existente.getCantidad().add(pedido.getCantidad());
            existente.setCantidad(nuevoStock);
            existente.setPrecio(pedido.getPrecio());
        } else {
            preVenta.add(pedido);
        }
        activarTable();
    }

    public void sugerenciasBuscadorInventario() {
        try {
            //conexion a api
            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/productos/listarSugerenciasOb")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .get()
                    .build();

            Ventas.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();

                    Type type = new TypeToken<List<DtoItemSugerenciaProductos>>() {
                    }.getType();
                    ArrayList<DtoItemSugerenciaProductos> sugerencias = Ventas.gson.fromJson(json, type);

                    sugerenciasWin = sugerencias;

                    SwingUtilities.invokeLater(() -> {
                        autoCompleterNuevaventa.removeAllItems();

                        for (DtoItemSugerenciaProductos dto : sugerencias) {
                            autoCompleterNuevaventa.addItem(dto);
                        }
                    });

                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }

    public void winAddProducto() {
        if (productoSelected != null) {
            v.panel.txt_addstock_nuevaventa.setText("0");
            v.panel.jL_descripcion_nuevaventa.setText(productoSelected.getDescripcion());
            v.panel.jL_precio_nuevaventa.setText("Q. " + productoSelected.getPrecio_venta());
            v.panel.jL_stock_nuevaventa.setText(productoSelected.getMedida_ob().getAbreviatura()
                    + " " + productoSelected.getStock());
            v.panel.jL_medida_nuevaventa.setText(productoSelected.getMedida_ob().getAbreviatura());

            //verifico que haya o no el mismo producto en la tabla de pedidos
            Map<Integer, Pedido_ob> pedidos = preVenta.stream()
                    .collect(Collectors.toMap(Pedido_ob::getId_producto, Pedido_ob -> Pedido_ob));

            Pedido_ob pedido = pedidos.get(productoSelected.getId());

            BigDecimal Stocktotal = new BigDecimal(productoSelected.getStock());
            existProductPedido = false;

            //si existe un objeto ya existente en la tabla le quitamos el stock aqui
            if (pedido != null) {
                existProductPedido = true;
                Stocktotal = Stocktotal.subtract(pedido.getCantidad());
                productoSelected.setStock(Stocktotal.doubleValue());
                //JOptionPane.showMessageDialog(null, "" + stock);
            }

            v.panel.jL_stock_nuevaventa.setText(Stocktotal.toPlainString());
            v.panel.jL_medida_nuevaventa.setText(productoSelected.getMedida_ob().getAbreviatura());

            d_addProducto.setVisible(true);
        }
    }

    private void validarVenta() {
        boolean val = true;
        if (preVenta.size() <= 0) {
            val = false;
            JOptionPane.showMessageDialog(v.home, "No se puede completar la venta. Por favor, agregue al menos un producto a la lista antes de proceder");
            return;
        }
        if (val) {
            WinFinalizarVenta();
        }
    }

    private void WinFinalizarVenta() {
        v.home.clientes.ListarClientes(new ListaClientesRep() {
            @Override
            public void onSuccess(List<Cliente_ob> listaClientes) {
                SwingUtilities.invokeLater(() -> {
                    Clientes = listaClientes;

                    autocompleterClientes.removeAllItems();
                    for (Cliente_ob cli : listaClientes) {
                        autocompleterClientes.addItem(cli);
                    }
                });
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

        BigDecimal total = BigDecimal.ZERO;

        for (Pedido_ob p : preVenta) {
            total = total.add(p.getSubtotal());
        }

        total = total.setScale(2, RoundingMode.HALF_UP);

        List<DtoPedido> pedidos = preVenta.stream()
                .map(Pedido_ob::toDTO)
                .collect(Collectors.toList());

        dtoRegistrar = new DtoVentaContado(pedidos, total);

        v.panel.jL_totalapagar_finalizarventa.setText("Q. " + total);

        validarmetodoPago();
        resetWinFinalizarVenta();

        d_finalizarventa.setVisible(true);
    }

    private void WinDescuentos() {
        productoDescuento = v.home.inventario.productoSelected(idPedidoSelected);
        if (productoDescuento != null) {
            v.panel.jL_descripcionproducto_descuento
                    .setText(productoDescuento.getDescripcion());
            v.panel.jL_preciooriginal_descuento
                    .setText("Q. " + productoDescuento.getPrecio_venta());
            v.panel.jL_preciofinal_descuento
                    .setText("Q. " + productoDescuento.getPrecio_venta());
            v.panel.cbx_tipodescuento_descuento.setSelectedIndex(0);

            v.panel.txt_valordescontar_descuento.setText("Q 0.00");

            removeAllKeyListener(v.panel.txt_valordescontar_descuento);
            Tools.txt_precio(v.panel.txt_valordescontar_descuento);
            txt_descuento(v.panel.txt_valordescontar_descuento);

            d_descuentos.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Seleccione una fila");
        }
    }

    private void removeAllKeyListener(JTextField txt) {
        var listeners = txt.getKeyListeners();
        for (var listener : listeners) {
            txt.removeKeyListener(listener);
        }
    }

    private void txt_descuento(JTextField txt) {
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                descuentoLabel();
            }
        });
    }

    private void descuentoLabel() {
        int tipodescuento = v.panel.cbx_tipodescuento_descuento.getSelectedIndex();
        BigDecimal precioVenta = BigDecimal.valueOf(productoDescuento.getPrecio_venta());
        BigDecimal precioCompra = BigDecimal.valueOf(productoDescuento.getPrecio_compra());
        BigDecimal margenPermitido = precioVenta.subtract(precioCompra);

        BigDecimal precioFinal;
        BigDecimal montoDescuento; // Variable para almacenar el descuento final

        if (tipodescuento == 0) { // MONTO FIJO
            montoDescuento = BigDecimal.valueOf(Tools.getPrecioLimpio(v.panel.txt_valordescontar_descuento));

            if (montoDescuento.compareTo(margenPermitido) > 0) {
                montoDescuento = margenPermitido;
                v.panel.txt_valordescontar_descuento.setText("Q " + montoDescuento.toPlainString());
            }
            this.descuento = montoDescuento;
            precioFinal = precioVenta.subtract(montoDescuento);

        } else { // PORCENTAJE
            BigDecimal porcentajeIngresado = BigDecimal.valueOf(Tools.getPorcentajeLimpio(v.panel.txt_valordescontar_descuento));
            BigDecimal porcentajeMaximo = margenPermitido.divide(precioVenta, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

            if (porcentajeIngresado.compareTo(porcentajeMaximo) > 0) {
                porcentajeIngresado = porcentajeMaximo.setScale(2, RoundingMode.HALF_UP);
                v.panel.txt_valordescontar_descuento.setText("% " + porcentajeIngresado.toPlainString());
            }

            // RECALCULAMOS el monto de descuento con el porcentaje (ya sea el original o el ajustado)
            montoDescuento = calcularDescuentoPorcentaje(precioVenta, porcentajeIngresado);
            this.descuento = montoDescuento;
            precioFinal = precioVenta.subtract(montoDescuento);
        }

        this.precioFinal = precioFinal.setScale(2, RoundingMode.HALF_UP);
        v.panel.jL_preciofinal_descuento.setText("Q " + precioFinal.setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    private BigDecimal calcularDescuentoPorcentaje(BigDecimal precio, BigDecimal porcentaje) {
        BigDecimal factor = porcentaje.divide(new BigDecimal("100"));
        BigDecimal descuento = precio.multiply(factor);

        return descuento.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularPorcentajeEquivalente(BigDecimal precioVenta, BigDecimal precioCompra) {
        BigDecimal margen = precioVenta.subtract(precioCompra);

        BigDecimal factor = margen.divide(precioVenta, 4, RoundingMode.HALF_UP);

        return factor.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
    }

    private void AplicarDescuento() {
        if (descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal precioCompra = new BigDecimal(productoDescuento.getPrecio_compra());
            if (precioFinal.compareTo(precioCompra) > 0) {
                Pedido_ob resultado = preVenta.stream()
                        .filter(d -> d.getId_producto() == idPedidoSelected)
                        .findFirst()
                        .orElse(null);

                resultado.setDescuentos(descuento);
                activarTable();
                d_descuentos.dispose();
            } else {
                JOptionPane.showMessageDialog(null, "El descuento ingresado es demasiado alto. El precio final no puede ser menor al costo de compra");
            }
        } else {
            JOptionPane.showMessageDialog(null, "El descuento debe ser mayor a 0");
        }
    }

    ///Registra una venta al contado
    private void RegistrarVentaContado() {
        String metodopago = v.panel.cbx_metodopago_finalizarventa.getSelectedItem().toString();
        String nitcliente = v.panel.txt_nit_finalizarventa.getText().trim();
        String nombrecliente = v.panel.txt_nombre_finalizarventa.getText();
        BigDecimal total = dtoRegistrar.getTotalApagar();

        boolean val = true;

        if (nitcliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nit_finalizarventa.setBackground(Color.red);
        }

        if (nombrecliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nombre_finalizarventa.setBackground(Color.red);
        }

        if (val) {

            if (metodopago.equals("Credito")) {
                if (!isClienteSelected) {
                    JOptionPane.showMessageDialog(null, "El metodo de pago a crédito solo está disponible para clientes registrados");
                    return;
                }
            }
            if (total.compareTo(new BigDecimal("2500")) >= 0 && (nitcliente.equalsIgnoreCase("cf")
                    || nitcliente.equalsIgnoreCase("c/f"))) {
                v.panel.txt_nit_finalizarventa.setBackground(Color.red);
                JOptionPane.showMessageDialog(null, "Para realizar una venta mayor a Q 2,500 es necesario un NIT");
                return;
            }

            try {
                //conexion a la api
                dtoRegistrar.setMetodoPago(metodopago);
                dtoRegistrar.setNitCliente(nitcliente);
                dtoRegistrar.setNombreCliente(nombrecliente);
                dtoRegistrar.setIdVendedor(userAuth.getUsuario().getId());
                dtoRegistrar.setIdTurno(userAuth.getIdTurnoActivo());

                String json = gson.toJson(dtoRegistrar);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/ventas/registarVentaContado")
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

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<DtoIdVenta>>() {
                            }.getType();
                            DtoResponseOb<DtoIdVenta> dto = gson.fromJson(json, type);

                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {
                                    d_finalizarventa.dispose();
                                    EliminarTodoTableNventa();
                                    Cargando.doAsync((onFinish) -> {
                                        v.home.reportes.generarRecibo(dto.getData(), onFinish);
                                    });
                                });
                            } else {
                                UtilMessage.message(dto.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                DtoResponseOb<Producto_ob> dto = gson.fromJson(json, DtoResponseOb.class);
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
        } else {
            JOptionPane.showMessageDialog(null, "Debes llenar todos los campos");
        }
    }

    ///Funcion de Registrar venta con deposito
    private void selectComprobanteDeposito() {
        FileDialog fileDialog = new FileDialog(v.home, "Seleccionar comprobante de pago", FileDialog.LOAD);

        fileDialog.setFilenameFilter((dir, name) -> {
            String nombreMinúscula = name.toLowerCase();
            return nombreMinúscula.endsWith(".jpg")
                    || nombreMinúscula.endsWith(".jpeg")
                    || nombreMinúscula.endsWith(".png");
        });

        fileDialog.setVisible(true);

        String directorio = fileDialog.getDirectory();
        String nombreArchivo = fileDialog.getFile();

        if (directorio != null && nombreArchivo != null) {
            fileComprobante = new File(directorio + nombreArchivo);
            isAddComprobante = true;

            v.panel.btn_addcombrobante_finalizarventa.setText(nombreArchivo);

        } else {
            JOptionPane.showMessageDialog(null, "ha pasado un error al cargar el comprobante");
        }
    }

    private void RegistrarVentaDeposito() {
        String metodopago = v.panel.cbx_metodopago_finalizarventa.getSelectedItem().toString();
        String nitcliente = v.panel.txt_nit_finalizarventa.getText().trim();
        String nombrecliente = v.panel.txt_nombre_finalizarventa.getText();
        BigDecimal total = dtoRegistrar.getTotalApagar();
        String noDeposito = v.panel.txt_noDeposito_finalizarventa.getText();
        boolean val = true;

        if (nitcliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nit_finalizarventa.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "Debes ingresar un NIT");
            return;
        }

        if (nombrecliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nombre_finalizarventa.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "Debes ingresar un nombre");
            return;
        }

        if (noDeposito.trim().isEmpty()) {
            val = false;
            v.panel.txt_noDeposito_finalizarventa.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "Es necesario el número de Deposito");
            return;
        }

        if (!isAddComprobante) {
            val = false;
            JOptionPane.showMessageDialog(null, "Es necesario adjuntar el comprobante del deposito");
        }

        if (val) {
            if (total.compareTo(new BigDecimal("2500")) >= 0 && (nitcliente.equalsIgnoreCase("cf")
                    || nitcliente.equalsIgnoreCase("c/f"))) {
                JOptionPane.showMessageDialog(null, "Para realizar una venta mayor a Q 2,500 es necesario un NIT");
                return;
            }

            try {
                //conexion a la api
                DtoVentaDeposito ventaDeposito = new DtoVentaDeposito(nombrecliente, nitcliente,
                        dtoRegistrar.getListaPedidos(), metodopago, dtoRegistrar.getTotalApagar(), noDeposito,
                        userAuth.getUsuario().getId(), userAuth.getIdTurnoActivo());

                String json = gson.toJson(ventaDeposito);

                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("ventaJson", json);

                if (fileComprobante != null && fileComprobante.exists()) {
                    String contentType = "application/octet-stream";
                    try {
                        String detectado = java.nio.file.Files.probeContentType(fileComprobante.toPath());
                        if (detectado != null) {
                            contentType = detectado;
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "No se pudo determinar el Content-Type de la imagen, usando default. " + e.getMessage());
                    }

                    RequestBody fileBody = RequestBody.create(
                            fileComprobante,
                            MediaType.parse(contentType)
                    );

                    multipartBuilder.addFormDataPart("comprobante", fileComprobante.getName(), fileBody);
                }
                RequestBody body = multipartBuilder.build();

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/ventas/registrarVentaDeposito")
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

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<DtoIdVenta>>() {
                            }.getType();
                            DtoResponseOb<DtoIdVenta> dto = gson.fromJson(json, type);

                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {
                                    d_finalizarventa.dispose();
                                    EliminarTodoTableNventa();
                                    Cargando.doAsync((onFinish) -> {
                                        v.home.reportes.generarRecibo(dto.getData(), onFinish);
                                    });
                                });
                            } else {
                                UtilMessage.message(dto.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                DtoResponseOb<Producto_ob> dto = gson.fromJson(json, DtoResponseOb.class);
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
    
    ///Registra una venta al credito
    private void RegistrarVentaCredito() {
        String metodopago = v.panel.cbx_metodopago_finalizarventa.getSelectedItem().toString();
        String nitcliente = v.panel.txt_nit_finalizarventa.getText().trim();
        String nombrecliente = v.panel.txt_nombre_finalizarventa.getText();
        BigDecimal total = dtoRegistrar.getTotalApagar();

        boolean val = true;

        if (nitcliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nit_finalizarventa.setBackground(Color.red);
        }

        if (nombrecliente.trim().isEmpty()) {
            val = false;
            v.panel.txt_nombre_finalizarventa.setBackground(Color.red);
        }

        if (val) {

            if (metodopago.equals("Credito")) {
                if (!isClienteSelected) {
                    JOptionPane.showMessageDialog(null, "El metodo de pago a crédito solo está disponible para clientes registrados");
                    return;
                }
            }
            if (total.compareTo(new BigDecimal("2500")) >= 0 && (nitcliente.equalsIgnoreCase("cf")
                    || nitcliente.equalsIgnoreCase("c/f"))) {
                v.panel.txt_nit_finalizarventa.setBackground(Color.red);
                JOptionPane.showMessageDialog(null, "Para realizar una venta mayor a Q 2,500 es necesario un NIT");
                return;
            }

            try {
                //conexion a la api
                DtoVentaCredito ventaCredito = new DtoVentaCredito(nombrecliente, nitcliente,
                        dtoRegistrar.getListaPedidos(), metodopago, dtoRegistrar.getTotalApagar(),
                        userAuth.getUsuario().getId(), userAuth.getIdTurnoActivo(), plazoDePago);

                String json = gson.toJson(ventaCredito);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/ventas/registarVentaCredito")
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

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<DtoIdVenta>>() {
                            }.getType();
                            DtoResponseOb<DtoIdVenta> dto = gson.fromJson(json, type);

                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {
                                    d_finalizarventa.dispose();
                                    EliminarTodoTableNventa();
                                    Cargando.doAsync((onFinish) -> {
                                        v.home.reportes.generarRecibo(dto.getData(), onFinish);
                                    });
                                });
                            } else {
                                UtilMessage.message(dto.getMessage());
                            }
                        } else {
                            if (code == 400) {
                                DtoResponseOb<Producto_ob> dto = gson.fromJson(json, DtoResponseOb.class);
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
        } else {
            JOptionPane.showMessageDialog(null, "Debes llenar todos los campos");
        }
    }

    private void resetWinFinalizarVenta() {
        v.panel.txt_nit_finalizarventa.setText("");
        v.panel.txt_nombre_finalizarventa.setText("");
        v.panel.txt_noDeposito_finalizarventa.setText("");
        v.panel.btn_addcombrobante_finalizarventa.setText("Adjuntar archivo");
        v.panel.jL_registrado_finalizarventa.setVisible(false);

        Date fechahoy = new Date();
        v.panel.jD_plazoDePago_finalizarventa.setDate(fechahoy);
        plazoDePago = new SimpleDateFormat("yyyy-MM-dd")
                                .format(fechahoy);
        v.panel.txt_fechafactura_historialventas.setText(new SimpleDateFormat("dd/MM/yyyy")
                                .format(fechahoy));
        
        fileComprobante = null;
        isAddComprobante = false;

    }
}

package Secciones;

import Clases.Cargando;
import Clases.Tools;
import Clases.Txt_buscador;
import Clases.X;
import Dto.DtoItemSugerenciaProductos;
import General.UtilMessage;
import General.userAuth;
import Objects.Pedido_ob;
import Objects.Producto_ob;
import Objects.Turno_ob;
import com.google.gson.reflect.TypeToken;
import com.mxrck.autocompleter.AutoCompleter;
import com.mxrck.autocompleter.AutoCompleterCallback;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.crypto.AEADBadTagException;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class Sub_nuevaventa {

    private Ventas v;

    private Txt_buscador buscadorProductosVentas;
    private List<Pedido_ob> preVenta = new ArrayList<>();
    private TextAutoCompleter autoCompleterNuevaventa;
    private JDialog d_addProducto;

    private Producto_ob productoSelected = null;

    private boolean existProductPedido = false;
    private List<DtoItemSugerenciaProductos> sugerenciasWin;

    private Timer timer = new Timer(0, null);

    private boolean bloqueadoPorAutoCompleter = false;

    private int idPedidoSelected = 0;

    public Sub_nuevaventa(Ventas ventas) {
        this.v = ventas;

        v.home.turnos.ValidarTurnoUserAuth();
        if (userAuth.getIdTurnoActivo() > 0) {
            Turno_ob turno = v.home.turnos.turnoSelected(userAuth.getIdTurnoActivo());
            v.home.jL_info_nuevaventa.setText("Usuario: " + userAuth.getUsuario().getNombre() + " | "
                    + "Caja: " + turno.getCaja().getNombre() + " | "
                    + "Turno nro: " + turno.getId());
        } else {
            v.home.jTabbedPane1.setSelectedComponent(v.home.jP_turno);
            Cargando.doSomething(new X() {
                @Override
                public void execute() {
                    v.home.turnos.ValidarTurnoUserAuth();
                    v.home.turnos.llenarInfoTurno();
                    v.home.turnos.tabledates("", 0);
                }
            });
        }

        Tools.diseñotabla1(v.home.tableNuevaVenta);
        Tools.txt_cantidad(v.panel.txt_addstock_nuevaventa);
        v.panel.txt_addstock_nuevaventa.setText("0");

        buscadorProductosVentas = new Txt_buscador("Buscar por codigo o descripcion..",
                v.home.txt_buscador_nuevaventa);

        d_addProducto = Tools.newWindow(v.panel.jP_infoproducto_nuevaventa);
        d_addProducto.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        sugerenciasBuscadorInventario();

        autoCompleterNuevaventa = new TextAutoCompleter(v.home.txt_buscador_nuevaventa, new AutoCompleterCallback() {
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

        Listeners();
    }

    public void Listeners() {
        v.home.btn_generar_nuevaventa.addActionListener(e -> {
            validarVenta();
        });
        v.panel.btn_añadir_nuevaventa.addActionListener(e -> {
            Cargando.doSomething(new X() {
                @Override
                public void execute() {
                    añadirProductoManual();
                }
            });
        });
        v.home.txt_buscador_nuevaventa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String texto = v.home.txt_buscador_nuevaventa.getText().trim();

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
                
                idPedidoSelected = Integer.parseInt(v.home.tableNuevaVenta
                        .getValueAt(row, 0).toString());
            }
        });
        v.home.jM_eliminar_nuevaventa.addActionListener(e -> {
            if(idPedidoSelected<1){
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
                preVenta.removeIf(p -> p.getId_producto()== idPedidoSelected);
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
            v.home.txt_buscador_nuevaventa.setText("");
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
            Cargando.doSomething(new X() {
                @Override
                public void execute() {
                    v.home.turnos.ValidarTurnoUserAuth();
                    v.home.turnos.llenarInfoTurno();
                    v.home.turnos.tabledates("", 0);
                }
            });
            return false;
        }
    }

    private void activarTable() {
        DefaultTableModel model = (DefaultTableModel) v.home.tableNuevaVenta.getModel();
        model.setRowCount(0);

        if(preVenta.size()<1){
            v.home.tableNuevaVenta.setModel(model);
            v.home.jL_totalapagar_nuevaventa.setText("Total a pagar Q. 0.00");
            return;
        }
        
        for (Pedido_ob pedido : preVenta) {
            model.addRow(new Object[]{
                pedido.getId_producto(),
                pedido.getCodigo(),
                pedido.getDescripcion(),
                pedido.getCantidad(),
                "Q. " + pedido.getPrecio(),
                "Q. " + pedido.getSubtotal()});
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

        BigDecimal Stock = BigDecimal.ONE;
        BigDecimal precio = new BigDecimal(producto.getPrecio_venta());

        addProducto(new Pedido_ob(producto.getId(),
                producto.getCodigo(),
                producto.getDescripcion(),
                Stock,
                precio,
                Stock.multiply(precio)));
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
                    precio,
                    stockAñadir.multiply(precio)));
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

            BigDecimal subtotal = nuevoStock.multiply(pedido.getPrecio());

            existente.setSubtotal(subtotal);
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
            JOptionPane.showMessageDialog(v.home, "Generando venta");
        }
    }
    
    //private void WinFor

}

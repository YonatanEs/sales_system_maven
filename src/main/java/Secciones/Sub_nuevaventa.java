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

        sugerenciasBuscadorInventario();

        Listeners();

        autoCompleterNuevaventa = new TextAutoCompleter(v.home.txt_buscador_nuevaventa, new AutoCompleterCallback() {
            @Override
            public void callback(Object o) {
                DtoItemSugerenciaProductos dto = (DtoItemSugerenciaProductos) o;

                productoSelected = v.home.inventario.productoSelected(dto.getId());

                winAddProducto();
            }
        });
    }

    public void Listeners() {
        v.home.btn_generar_nuevaventa.addActionListener(e -> {

            if (initNuevaVenta()) {
                JOptionPane.showMessageDialog(null, "Generando venta...");
            }
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
                }
            }
        });
    }

    private void procesarEntrada(String input) {
        input = input.trim();
        
        if (input.matches("\\d{8,14}")) {
            v.home.txt_buscador_nuevaventa.setText("");
            añadirProductoAutomatico(input);
        }
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

    private void mostrarErrorEscaner(String error){
       v.home.jL_error_nuevaventa.setText(error);
       
       timer = new Timer(5000, e->{
           v.home.jL_error_nuevaventa.setText("");
       });
       
       timer.setRepeats(false);
       timer.start();
    }
    
    private void añadirProductoAutomatico(String codigo) {
        if(timer.isRunning()){
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
        if (preVenta.size() == 30) {
            JOptionPane.showMessageDialog(null, "Has alcanzado el límite recomendado de ítems por factura. "
                    + "Considera generar una nueva factura para mayor claridad.");
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

}

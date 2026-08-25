

package Secciones;

import Clases.Cargando;
import Clases.Tools;
import Clases.Txt_buscador;
import Clases.X;
import Dto.DtoFiltroHistorialVentas;
import Dto.DtoHistorialVentas;
import Dto.DtoIdVenta;
import Dto.Dto_datosEmpresa;
import Dto.RespuestaPaginada;
import Dto.ValorRequestPag;
import General.UtilMessage;
import General.userAuth;
import Objects.DatosEmpresaOb;
import Objects.Turno_ob;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.CardLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.tools.Tool;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sub_historialventa {

    private Ventas v;
    private JDialog d_historial;
    private int pagina_actual;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private DtoFiltroHistorialVentas filtro;

    private int idReciboSelected = 0;
    private String metodoPagoHV;

    private Txt_buscador buscadorhistorialventa;
    private Txt_buscador buscadorfecha;
    private Txt_buscador buscadorRecibo;
    
    private JDialog jD_comprobanteDeposito;

    public sub_historialventa(Ventas v) {
        this.v = v;
        filtro = new DtoFiltroHistorialVentas();
        filtro.setPageSize(20);
        filtro.setPage(0);
        d_historial = Tools.newWindow(v.panel.jP_historialventas);
        Tools.diseñotabla1(v.panel.tableHistorialventas);
        Tools.btn_animacion_size(v.panel.btn_sigpag_historialventas);
        Tools.btn_animacion_size(v.panel.btn_anteriorpag_historialventas);

        jD_comprobanteDeposito = Tools.newWindow(v.panel.jP_visualizadorComprobanteDeposito);
        buscadorhistorialventa = new Txt_buscador("Buscar cliente o NIT ",
                v.panel.txt_buscadornit_historialventa);
        buscadorfecha = new Txt_buscador("Buscar fecha",
                v.panel.txt_fechafactura_historialventas);
        buscadorRecibo = new Txt_buscador("Buscar por No. Recibo ", v.panel.txt_buscadornorecibo_historialventa);

        Listener();
    }

    private void Listener() {
        v.panel.tableHistorialventas.addMouseListener(new MouseAdapter() {

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
                    int row = v.panel.tableHistorialventas.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < v.panel.tableHistorialventas.getRowCount()) {

                        v.panel.tableHistorialventas.setRowSelectionInterval(row, row);
                        idReciboSelected = Integer.parseInt(v.panel.tableHistorialventas.getValueAt(row, 0).toString());

                        metodoPagoHV = v.panel.tableHistorialventas.getValueAt(row, 5).toString();

                        if (metodoPagoHV.equalsIgnoreCase("Deposito") || metodoPagoHV.equalsIgnoreCase("Depósito")) {
                            v.panel.jM_verComprobanteDeposito.setVisible(true);
                        } else {
                            v.panel.jM_verComprobanteDeposito.setVisible(false);
                        }

                        v.panel.jPopupHistorialVenta.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        v.panel.jM_verRecibo.addActionListener((e) -> {
            Cargando.doAsync((onFinish) -> {
                v.home.reportes.generarRecibo(new DtoIdVenta(idReciboSelected), onFinish);
            });

        });

        v.panel.jM_verComprobanteDeposito.addActionListener(e->{
            WinComprobanteDeposito();
        });
        
        v.home.btn_historialventas.addActionListener(e -> {
            winHistorialGeneral();
        });

        Tools.buscadorTablaValidate(v.panel.txt_buscadornit_historialventa,
                "Buscar cliente o NIT ", (consulta) -> {
                    pagina_actual = 0;

                    v.panel.jD_fechabusqueda_historialventas.setDate(null);

                    filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit);
                    filtro.setBusqueda(consulta);
                    filtro.setPage(pagina_actual);
                    tabledates(filtro);

                });

        v.panel.txt_buscadornit_historialventa.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String busqueda = buscadorfecha.getTextoReal();
                if (!busqueda.trim().isEmpty()) {
                    pagina_actual = 0;

                    v.panel.jD_fechabusqueda_historialventas.setDate(null);
                    buscadorfecha.setTextoManual("");

                    filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit);
                    filtro.setBusqueda("");
                    filtro.setPage(pagina_actual);
                    tabledates(filtro);
                }
            }
        });

        Tools.buscadorTablaValidate(v.panel.txt_buscadornorecibo_historialventa,
                "Buscar por No. Recibo ", (consulta) -> {
                    pagina_actual = 0;

                    v.panel.jD_fechabusqueda_historialventas.setDate(null);

                    filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadornorecibo);
                    filtro.setBusqueda(consulta);
                    filtro.setPage(pagina_actual);
                    tabledates(filtro);

                });

        v.panel.txt_buscadornorecibo_historialventa.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String busqueda = buscadorfecha.getTextoReal();
                if (!busqueda.trim().isEmpty()) {
                    pagina_actual = 0;

                    v.panel.jD_fechabusqueda_historialventas.setDate(null);
                    buscadorfecha.setTextoManual("");

                    filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit);
                    filtro.setBusqueda("");
                    filtro.setPage(pagina_actual);
                    tabledates(filtro);
                }
            }
        });
        
        //el datachooser le pasa la fecha al txt
        v.panel.jD_fechabusqueda_historialventas.addPropertyChangeListener("date", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (v.panel.jD_fechabusqueda_historialventas.getDate() != null) {
                    v.panel.txt_buscadornit_historialventa.setText("");
                    String fecha = new SimpleDateFormat("yyyy-MM-dd")
                            .format(v.panel.jD_fechabusqueda_historialventas.getDate());

                    String fechaview = new SimpleDateFormat("dd/MM/yyyy")
                            .format(v.panel.jD_fechabusqueda_historialventas.getDate());

                    filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadorfecha);
                    filtro.setFecha(fecha);
                    buscadorfecha.setTextoManual(fechaview);
                    v.panel.txt_fechafactura_historialventas.requestFocus();
                    
                    tabledates(filtro);
                }
            }
        });

        v.panel.txt_fechafactura_historialventas.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    String fec = buscadorfecha.getTextoReal();
                    if (!fec.trim().isEmpty()) {
                        buscadorfecha.setTextoManual("");
                        v.panel.jD_fechabusqueda_historialventas.requestFocus();

                        pagina_actual = 0;

                        v.panel.jD_fechabusqueda_historialventas.setDate(null);

                        filtro.setContextoBusqueda(DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit);
                        filtro.setBusqueda("");
                        filtro.setPage(pagina_actual);
                        tabledates(filtro);
                    }
                }
            }
        });

        //boton siguiente de tabla turno
        v.panel.btn_sigpag_historialventas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean isEnabled = v.panel.btn_sigpag_historialventas.isEnabled();
                if (isEnabled) {
                    pagina_actual++;
                    filtro.setPage(pagina_actual);
                    tabledates(filtro);
                }
            }
        });

        //btn anterior de tabla turno
        v.panel.btn_anteriorpag_historialventas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual > 0) {
                    boolean isEnabled = v.panel.btn_anteriorpag_historialventas.isEnabled();
                    if (isEnabled) {
                        pagina_actual--;
                        filtro.setPage(pagina_actual);
                        tabledates(filtro);
                    }
                }
            }
        });
        
        v.panel.cbx_buscadorhistorial.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                buscadorHistorialVentas();
            }
        });
    }
    
    
    private void buscadorHistorialVentas() {
        String seleccion = v.panel.cbx_buscadorhistorial.getSelectedItem().toString();

        String target = "buscadorfecha";
        if(seleccion.equalsIgnoreCase("NIT/Cliente")){
            target = "buscadornit";
        }else if(seleccion.equalsIgnoreCase("No. Recibo")){
            target = "buscadornorecibo";
        }else{
            target = "buscadorfecha";
        }
        
        CardLayout cl = (CardLayout) (v.panel.jP_buscadorhistorial.getLayout());
        cl.show(v.panel.jP_buscadorhistorial, target);


    }
    
    public void winHistorialGeneral() {
        //La fecha actual: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        pagina_actual = 0;
        v.panel.cbx_buscadorhistorial.setSelectedIndex(0);
        //v.panel.cbx_buscadorhistorial.addItem("");
        
        filtro = new DtoFiltroHistorialVentas(DtoFiltroHistorialVentas.ContextoOrigen.GENERAL,
                DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit, "", "",
                "",
                20, pagina_actual);

        v.panel.jP_buscadorhistorial_nit.setVisible(true);

        buscadorhistorialventa.setTextoManual("");
        buscadorfecha.setTextoManual("");
        v.panel.jD_fechabusqueda_historialventas.setDate(null);

        v.panel.jL_titulo_historialventa.setText("Historial de ventas");

        tabledates(filtro);
        d_historial.setVisible(true);
        /*SwingUtilities.invokeLater(() -> {
            v.panel.jD_fechabusqueda_historialventas.getDateEditor().getUiComponent().requestFocusInWindow();
        });*/
    }

    public void winHistorialTurno(int idTurno) {
        //La fecha actual: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        pagina_actual = 0;

        v.panel.cbx_buscadorhistorial.setSelectedIndex(0);
        
        filtro = new DtoFiltroHistorialVentas(DtoFiltroHistorialVentas.ContextoOrigen.TURNO,
                DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit, "", String.valueOf(idTurno),
                "",
                20, pagina_actual);

        v.panel.jP_buscadorhistorial_nit.setVisible(true);

        buscadorhistorialventa.setTextoManual("");
        buscadorfecha.setTextoManual("");
        v.panel.jD_fechabusqueda_historialventas.setDate(null);

        v.panel.jL_titulo_historialventa.setText("Historial de ventas del Turno No." + idTurno);

        tabledates(filtro);
        d_historial.setVisible(true);
    }

    public void winHistorialCliente(String nit, String nombrecliente) {
        //La fecha actual: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        pagina_actual = 0;

        v.panel.cbx_buscadorhistorial.setSelectedIndex(0);
        
        filtro = new DtoFiltroHistorialVentas(DtoFiltroHistorialVentas.ContextoOrigen.CLIENTE,
                DtoFiltroHistorialVentas.ContextoBusqueda.buscadornit, "", nit,
                "",
                20, pagina_actual);

        v.panel.jP_buscadorhistorial_nit.setVisible(false);

        buscadorhistorialventa.setTextoManual("");
        buscadorfecha.setTextoManual("");
        v.panel.jD_fechabusqueda_historialventas.setDate(null);
        v.panel.jL_titulo_historialventa.setText("Historial de " + nombrecliente);

        tabledates(filtro);
        d_historial.setVisible(true);
    }

    public void tabledates(DtoFiltroHistorialVentas filtro) {
        pagina_actual = filtro.getPage();
        try {

            DefaultTableModel modelo = (DefaultTableModel) v.panel.tableHistorialventas.getModel();

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api

            String jsonBody = gson.toJson(filtro);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/ventas/listarHistorialVentas")
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

                    Type type = new TypeToken<RespuestaPaginada<DtoHistorialVentas>>() {
                    }.getType();
                    RespuestaPaginada<DtoHistorialVentas> rs = gson.fromJson(json, type);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);

                        if (rs != null && rs.getContent() != null) {
                            for (DtoHistorialVentas ventas : rs.getContent()) {
                                modelo.addRow(new Object[]{
                                    ventas.getId(),
                                    ventas.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")),
                                    ventas.getNitCliente(),
                                    ventas.getNombreCliente(),
                                    "Q. " + ventas.getTotal(),
                                    ventas.getMetodoPago(),
                                    ventas.getEstado()
                                });
                            }

                            v.panel.btn_sigpag_historialventas.setEnabled(!rs.isLast());
                            v.panel.btn_anteriorpag_historialventas.setEnabled(filtro.getPage() > 0);
                            v.panel.jL_pags_historialventas.setText("Pagina     " + (filtro.getPage() + 1) + "     de    " + rs.getTotalPages());

                        } else {
                            v.panel.jL_pags_historialventas.setText("Pagina 0 de 0");
                        }

                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }

    private void WinComprobanteDeposito() {
        try {
            DtoIdVenta idventa = new DtoIdVenta(idReciboSelected);

            String jsonBody = gson.toJson(idventa);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/ventas/obtenerComprobanteDeposito")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    General.UtilMessage.messageError("Error al obtener datos: " + ioe.getMessage());
                }

                @Override
                public void onResponse(Call call, Response rspns) throws IOException {
                    try (rspns) {
                        if (!rspns.isSuccessful() || rspns.body() == null) {
                            General.UtilMessage.messageError("Error al obtener comprobante. Code: "
                                    + rspns.code() + " Mensaje: " + rspns.message());
                            return;
                        }
                        // Convertir respuesta en bytes → imagen
                        String noDeposito = rspns.header("X-Numero-Comprobante");
                        byte[] imageBytes = rspns.body().bytes();
                        ImageIcon comprobante = new ImageIcon(imageBytes);

                        // Mostrar en tu ventana Swing
                        SwingUtilities.invokeLater(() -> {
                            Tools.adaptarIconoJLabel(v.panel.jL_img_vcd, comprobante);
                            jD_comprobanteDeposito.setTitle("Comprobante de depósito - "+noDeposito);
                            jD_comprobanteDeposito.setVisible(true);
                        });
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "");
        }
    }

}

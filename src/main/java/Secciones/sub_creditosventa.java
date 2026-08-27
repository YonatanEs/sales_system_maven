package Secciones;

import Clases.Cargando;
import Clases.OnCredito;
import Clases.OnGenerico;
import Clases.Tools;
import Clases.Tools.BuscadorCallback;
import Clases.Txt_buscador;
import Clases.onComprobanteAbono;
import Dto.DtoAbonoComprobante;
import Dto.DtoCredito;
import Dto.DtoFiltroHistorialabonos;
import Dto.DtoId;
import Dto.DtoIdPage;
import Dto.DtoIdVenta;
import Dto.DtoItemCredito;
import Dto.DtoItemHistorialabonos;
import Dto.DtoRegistrarAbono;
import Dto.DtoResponseOb;
import Dto.RespuestaPaginada;
import General.UtilMessage;
import General.userAuth;
import Objects.Producto_ob;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.FileDialog;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;

public class sub_creditosventa {

    private Ventas v;

    private int pagina_actual;
    private int pagina_actual_abonos;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private JDialog d_creditoclientes;
    private JDialog d_abonar;
    private JDialog jD_comprobanteDeposito;

    private Long idCreditoSelected = 0L;
    private DtoItemCredito creditoSelected = null;

    private DtoCredito dtoCredito = null;
    
    private Long idAbonoSelected = 0L;

    //variables para la opcion de ventas con deposito
    private File fileComprobante = null;
    private boolean isAddComprobante = false;

    private Txt_buscador buscadoAbonos;
    KeyEvent enterEvent;

    public sub_creditosventa(Ventas ventas) {
        this.v = ventas;

        d_creditoclientes = Tools.newWindow(v.panel.jP_creditoClientes);

        d_abonar = Tools.newWindow(v.panel.jP_abonar_credito);
        d_abonar.setTitle("Abonar Pago");
        jD_comprobanteDeposito = Tools.newWindow(v.panel.jP_visualizadorComprobanteDeposito);
        
        Tools.diseñotabla1(v.panel.tableCredito);
        Tools.diseñotabla1(v.panel.tableHistorialabono);

        Tools.btn_animacion_size(v.panel.btn_sigpag_credito);
        Tools.btn_animacion_size(v.panel.btn_anteriorpag_credito);

        Tools.btn_animacion_size(v.panel.btn_sigpag_historialabono);
        Tools.btn_animacion_size(v.panel.btn_anteriorpag_historialabono);

        Tools.txt_precio(v.panel.txt_monto_abonar);

        buscadoAbonos = new Txt_buscador("buscar por ", v.panel.txt_buscador_historialabono);

        enterEvent = new KeyEvent(
                v.panel.txt_buscador_historialabono, // componente destino
                KeyEvent.KEY_RELEASED, // tipo de evento
                System.currentTimeMillis(), // timestamp
                0, // modifiers (ninguno)
                KeyEvent.VK_ENTER, // tecla Enter
                '\n' // carácter asociado
        );

        //Tools.txt_precio(v.panel.txt_);
        Listeners();
    }

    private void Listeners() {
        v.panel.tableCredito.addMouseListener(new MouseAdapter() {

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
                    int row = v.panel.tableCredito.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < v.panel.tableCredito.getRowCount()) {

                        v.panel.tableCredito.setRowSelectionInterval(row, row);

                        idCreditoSelected = Long.valueOf(v.panel.tableCredito.getValueAt(row, 0).toString());

                        List<DtoItemCredito> creditos = dtoCredito.getListCredito().getContent();

                        creditoSelected = creditos.stream()
                                .filter(c -> Objects.equals(c.getId(), idCreditoSelected))
                                .findFirst()
                                .orElse(null);
                        String estado = v.panel.tableCredito.getValueAt(row, 6).toString();

                        if (estado.equalsIgnoreCase("PAGADO")) {
                            v.panel.jM_abonar_creditos.setVisible(false);
                        } else {
                            v.panel.jM_abonar_creditos.setVisible(true);
                        }

                        v.panel.jPopupCreditos.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        
        v.panel.tableHistorialabono.addMouseListener(new MouseAdapter() {

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
                    int row = v.panel.tableHistorialabono.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < v.panel.tableHistorialabono.getRowCount()) {

                        v.panel.tableHistorialabono.setRowSelectionInterval(row, row);

                        idAbonoSelected = Long.valueOf(v.panel.tableHistorialabono.getValueAt(row, 0).toString());

                        String formaPago = v.panel.tableHistorialabono.getValueAt(row, 4).toString();

                        if (formaPago.equalsIgnoreCase("EFECTIVO")) {
                            v.panel.jM_verDeposito_abono.setVisible(false);
                        } else {
                            v.panel.jM_verDeposito_abono.setVisible(true);
                        }

                        v.panel.jPopupAbonos.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        v.panel.jM_verAbono_abono.addActionListener((e) -> {
            Cargando.doAsync((onFinish) -> {
                v.home.reportes.generarAbonoComprobante(new DtoId(idAbonoSelected), onFinish);
            });
            
        });
        
        Tools.buscadorTablaValidate(v.panel.txt_buscador_historialabono, "buscar por ", new BuscadorCallback() {
            @Override
            public void ejecutar(String consulta) {
                tabledatesabonos(0, consulta, new OnGenerico() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(String error) {
                        General.UtilMessage.messageError(error);
                    }
                });
            }
        });

        v.panel.jM_abonar_creditos.addActionListener(e -> {
            WinAbonar();
        });

        v.panel.jM_vercomprobante_creditos.addActionListener(e -> {
            int idRecibo = Math.toIntExact(creditoSelected.getIdVenta());
            Cargando.doAsync((onFinish) -> {
                v.home.reportes.generarRecibo(
                        new DtoIdVenta(idRecibo), onFinish);
            });
        });

        v.panel.cbx_formaPago_credito.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String formaPago = v.panel.cbx_formaPago_credito.getSelectedItem().toString();
                if (formaPago.equals("Efectivo")) {
                    v.panel.jP_detallesDeposito_credito.setVisible(false);
                } else {
                    v.panel.jP_detallesDeposito_credito.setVisible(true);
                }
            }
        });

        
        v.home.item_credito.addActionListener(e -> {
            winCreditoCliente();
        });
        //boton siguiente de tabla credito
        v.panel.btn_sigpag_credito.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean isEnabled = v.panel.btn_sigpag_credito.isEnabled();
                if (isEnabled) {
                    pagina_actual++;
                    tabledates(pagina_actual, new OnCredito() {
                        @Override
                        public void onSuccess(DtoCredito dtoCredito) {
                        }

                        @Override
                        public void onError(String error) {
                            General.UtilMessage.messageError(error);
                        }
                    });
                }
            }
        });

        //btn anterior de tabla credito
        v.panel.btn_anteriorpag_credito.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual > 0) {
                    boolean isEnabled = v.panel.btn_anteriorpag_credito.isEnabled();
                    if (isEnabled) {
                        pagina_actual--;
                        tabledates(pagina_actual, new OnCredito() {
                            @Override
                            public void onSuccess(DtoCredito dtoCredito) {
                            }

                            @Override
                            public void onError(String error) {
                                General.UtilMessage.messageError(error);
                            }

                        });
                    }
                }
            }
        });

        v.panel.btn_sigpag_historialabono.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual_abonos > 0) {
                    boolean isEnabled = v.panel.btn_sigpag_historialabono.isEnabled();
                    if (isEnabled) {
                        pagina_actual_abonos++;
                        tabledatesabonos(pagina_actual_abonos, buscadoAbonos.getTextoReal(),
                                new OnGenerico() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(String error) {
                                General.UtilMessage.messageError(error);
                            }

                        });
                    }

                }
            }
        });

        v.panel.btn_anteriorpag_historialabono.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual_abonos > 0) {
                    boolean isEnabled = v.panel.btn_anteriorpag_historialabono.isEnabled();
                    if (isEnabled) {
                        pagina_actual_abonos--;
                        tabledatesabonos(pagina_actual_abonos, buscadoAbonos.getTextoReal(),
                                new OnGenerico() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(String error) {
                                General.UtilMessage.messageError(error);
                            }

                        });
                    }

                }
            }
        });

        v.panel.txt_monto_abonar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                BigDecimal monto = new BigDecimal(Tools.getPrecioLimpio(v.panel.txt_monto_abonar));
                BigDecimal saldoPendiente = creditoSelected.getSaldoPendiente();

                if (monto.compareTo(saldoPendiente) > 0) {
                    v.panel.txt_monto_abonar.setText("Q " + String.valueOf(saldoPendiente));
                }
            }
        });

        v.panel.btn_addcombrobante_credito.addActionListener(e -> {
            selectComprobanteDeposito();
        });

        v.panel.btn_confirmarAbono_credito.addActionListener(e -> {
            String formaPago = v.panel.cbx_formaPago_credito.getSelectedItem().toString();
            if (formaPago.equalsIgnoreCase("Efectivo")) {
                registrarAbonoEfectivo();
            } else {
                registrarAbonoDeposito();
            }
        });

        v.panel.cbx_buscadorhistorialabono.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    tabledatesabonos(0, buscadoAbonos.getTextoReal(), new OnGenerico() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(String error) {
                            General.UtilMessage.messageError(error);
                        }
                    });
                }
            }
        });

        v.panel.jM_verabonos_creditos.addActionListener(e -> {
            verAbonosRecibo(creditoSelected.getIdVenta());
        });
    
        v.panel.jM_verDeposito_abono.addActionListener(e->{
            WinComprobanteDeposito();
        });
        
    }
    
    private void WinComprobanteDeposito() {
        try {
            DtoId idabono = new DtoId(idAbonoSelected);

            String jsonBody = gson.toJson(idabono);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/creditos/obtenerComprobanteDeposito")
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

            v.panel.btn_addcombrobante_credito.setText(nombreArchivo);

        } else {
            JOptionPane.showMessageDialog(null, "ha pasado un error al cargar el comprobante");
        }
    }

    private void winCreditoCliente() {
        tabledatesabonos(0, "", new OnGenerico() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

        tabledates(0, new OnCredito() {
            @Override
            public void onSuccess(DtoCredito dtoCredito) {
                d_creditoclientes.setVisible(true);
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

    }

    private void verAbonosRecibo(Long idRecibo) {
        v.panel.cbx_buscadorhistorialabono.setSelectedIndex(0);
        v.panel.jT_winCredito.setSelectedIndex(1);
        buscadoAbonos.setTextoManual(String.valueOf(idRecibo));
        v.panel.txt_buscador_historialabono.dispatchEvent(enterEvent);
    }

    private void WinAbonar() {
        resetWinAbonar();
        d_abonar.setVisible(true);
    }

    private void resetWinAbonar() {
        tabledatesabonos(0, "", new OnGenerico() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

        tabledates(0, new OnCredito() {
            @Override
            public void onSuccess(DtoCredito dtoCredito) {

                creditoSelected = dtoCredito.getListCredito().getContent().stream()
                        .filter(c -> Objects.equals(c.getId(), idCreditoSelected))
                        .findFirst()
                        .orElse(null);

                v.panel.jL_noRecibo_abonar.setText(String.valueOf(creditoSelected.getIdVenta()));
                v.panel.jL_cliente_abonar.setText(dtoCredito.getNombreCliente());
                v.panel.jL_montoTotal_abonar.setText("Q " + creditoSelected.getMontoTotal());
                v.panel.jL_saldo_abonar.setText("Q " + creditoSelected.getSaldoPendiente());

                v.panel.txt_monto_abonar.setText("Q 0");
                v.panel.txt_noDeposito_credito.setText("");
                v.panel.btn_addcombrobante_credito.setText("Adjuntar archivo");

                isAddComprobante = false;
                fileComprobante = null;

                String formaPago = v.panel.cbx_formaPago_credito.getSelectedItem().toString();
                if (formaPago.equals("Efectivo")) {
                    v.panel.jP_detallesDeposito_credito.setVisible(false);
                } else {
                    v.panel.jP_detallesDeposito_credito.setVisible(true);
                }

                if (creditoSelected.getSaldoPendiente().compareTo(BigDecimal.ZERO) <= 0) {
                    d_abonar.dispose();
                }
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

    }

    private void registrarAbonoEfectivo() {
        BigDecimal montoAbonado = new BigDecimal(
                Tools.getPrecioLimpio(v.panel.txt_monto_abonar));
        String formaPago = v.panel.cbx_formaPago_credito.getSelectedItem().toString();
        boolean val = true;

        if (montoAbonado.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "El monto a abonar debe ser mayor que 0",
                    "Error", JOptionPane.ERROR_MESSAGE);
            val = false;
            return;
        }

        if (val) {
            try {
                //conexion a la api
                DtoRegistrarAbono abonoEfectivo = new DtoRegistrarAbono(
                        creditoSelected.getId(), Long.valueOf(userAuth.getIdTurnoActivo()),
                        Long.valueOf(userAuth.getUsuario().getId()), montoAbonado, formaPago, "");

                String json = gson.toJson(abonoEfectivo);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/creditos/registrarAbonoEfectivo")
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

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<DtoId>>() {
                            }.getType();
                            DtoResponseOb<DtoId> dto = gson.fromJson(json, type);

                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {
                                    v.home.clientes.tabledates("");
                                    resetWinAbonar();

                                    Cargando.doAsync((onFinish) -> {
                                        v.home.reportes.generarAbonoComprobante(dto.getData(), onFinish);
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

    private void registrarAbonoDeposito() {
        BigDecimal montoAbonado = new BigDecimal(
                Tools.getPrecioLimpio(v.panel.txt_monto_abonar));
        String formaPago = v.panel.cbx_formaPago_credito.getSelectedItem().toString();
        String noDeposito = v.panel.txt_noDeposito_credito.getText();
        boolean val = true;

        if (montoAbonado.compareTo(BigDecimal.ZERO) <= 0) {
            JOptionPane.showMessageDialog(null,
                    "El monto a abonar debe ser mayor que 0",
                    "Error", JOptionPane.ERROR_MESSAGE);
            val = false;
            return;
        }

        if (!isAddComprobante) {
            JOptionPane.showMessageDialog(null,
                    "Es necesario adjuntar el comprobante del depósito",
                    "Error", JOptionPane.ERROR_MESSAGE);
            val = false;
            return;
        }

        if (noDeposito.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Es necesario el número de depósito",
                    "Error", JOptionPane.ERROR_MESSAGE);
            val = false;
            return;
        }

        if (val) {
            try {
                //conexion a la api
                DtoRegistrarAbono abonoEfectivo = new DtoRegistrarAbono(
                        creditoSelected.getId(), Long.valueOf(userAuth.getIdTurnoActivo()),
                        Long.valueOf(userAuth.getUsuario().getId()), montoAbonado, formaPago, noDeposito);

                String json = gson.toJson(abonoEfectivo);

                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("creditoJson", json);

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
                        .url(General.properties.getUrl() + "/api/creditos/registrarAbonoDeposito")
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

                            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<DtoResponseOb<DtoId>>() {
                            }.getType();
                            DtoResponseOb<DtoId> dto = gson.fromJson(json, type);

                            if (dto.isSuccess()) {
                                SwingUtilities.invokeLater(() -> {
                                    v.home.clientes.tabledates("");
                                    resetWinAbonar();

                                    //JOptionPane.showMessageDialog(null, "Abono No." + dto.getData().getId());
                                    Cargando.doAsync((onFinish) -> {
                                        v.home.reportes.generarAbonoComprobante(dto.getData(), onFinish);
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

    private void tabledates(int page, OnCredito listener) {
        DefaultTableModel modelo = (DefaultTableModel) v.panel.tableCredito.getModel();

        List<Object[]> datos = new ArrayList<Object[]>();
        pagina_actual = page;
        try {
            //Conexion a la api
            DtoIdPage dto = new DtoIdPage(v.home.clientes.clienteSelected.getId(), page, 25);

            String jsonBody = gson.toJson(dto);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/creditos/listarCreditos")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    listener.onError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    DtoCredito credito = gson.fromJson(json, DtoCredito.class);

                    dtoCredito = credito;

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);

                        v.panel.jL_cliente_credito.setText(credito.getNombreCliente());
                        v.panel.jL_nit_credito.setText(credito.getNitCliente());
                        v.panel.jL_deudatotal_credito.setText("Q " + String.valueOf(credito.getDeudaTotal()));
                        v.panel.jL_estado_credito.setText(credito.getEstadoGeneral());

                        RespuestaPaginada<DtoItemCredito> pagcredito = credito.getListCredito();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                        if (pagcredito != null && pagcredito.getContent() != null) {
                            for (DtoItemCredito itemCredito : pagcredito.getContent()) {
                                modelo.addRow(new Object[]{
                                    itemCredito.getId(),
                                    itemCredito.getIdVenta(),
                                    itemCredito.getFechaEmision().format(formatter),
                                    itemCredito.getPlazoPago().format(formatter),
                                    "Q " + itemCredito.getMontoTotal(),
                                    "Q " + itemCredito.getSaldoPendiente(),
                                    itemCredito.getEstado()
                                });
                            }

                            v.panel.btn_sigpag_credito.setEnabled(!pagcredito.isLast());
                            v.panel.btn_anteriorpag_credito.setEnabled(pagina_actual > 0);
                            v.panel.jL_pags_credito.setText("Pagina " + (pagina_actual + 1) + " de " + pagcredito.getTotalPages());

                        } else {
                            v.panel.jL_pags_credito.setText("Pagina 0 de 0");
                        }
                    });

                    listener.onSuccess(dtoCredito);

                }
            });

        } catch (Exception e) {
            listener.onError("Error al cargar datos de credito" + e);
        }
    }

    /**
     * *********Subseccion de historial de abonos ***********
     */
    private void tabledatesabonos(int page, String contentBusqueda, OnGenerico listener) {
        DefaultTableModel modelo = (DefaultTableModel) v.panel.tableHistorialabono.getModel();

        List<Object[]> datos = new ArrayList<Object[]>();
        pagina_actual_abonos = page;
        String contextoBusqueda = v.panel.cbx_buscadorhistorialabono.getSelectedItem().toString();

        try {
            //Conexion a la api
            DtoFiltroHistorialabonos dto = new DtoFiltroHistorialabonos(Long.valueOf(v.home.clientes.clienteSelected.getId()),
                    contextoBusqueda, contentBusqueda, page, 20);

            String jsonBody = gson.toJson(dto);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/creditos/listarAbonos")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    listener.onError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if (response.isSuccessful()) {
                        Type listType = new TypeToken<RespuestaPaginada<DtoItemHistorialabonos>>() {
                        }.getType();
                        RespuestaPaginada<DtoItemHistorialabonos> rs = gson.fromJson(json, listType);

                        SwingUtilities.invokeLater(() -> {
                            modelo.setRowCount(0);

                            if (rs != null && rs.getContent() != null) {
                                for (DtoItemHistorialabonos itemHabono : rs.getContent()) {
                                    modelo.addRow(new Object[]{
                                        itemHabono.getIdAbono(),
                                        itemHabono.getIdRecibo(),
                                        itemHabono.getFecha(),
                                        itemHabono.getMonto(),
                                        itemHabono.getFormaPago()
                                    });
                                }

                                v.panel.btn_sigpag_historialabono.setEnabled(!rs.isLast());
                                v.panel.btn_anteriorpag_historialabono.setEnabled(pagina_actual_abonos > 0);
                                v.panel.jL_pags_historialabono.setText("Pagina " + (pagina_actual_abonos + 1) + " de " + rs.getTotalPages());

                            } else {
                                v.panel.jL_pags_historialabono.setText("Pagina 0 de 0");
                            }
                        });
                        listener.onSuccess();
                    } else {
                        String error = General.Error.parseJsonError(json);
                        listener.onError(error);
                    }

                }
            });

        } catch (Exception e) {
            listener.onError("Error al cargar datos de credito" + e);
        }
    }

    public void obtenerDatosAbono(DtoId dtoId, onComprobanteAbono onAbono) {
        try {

            String jsonBody = gson.toJson(dtoId);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/creditos/obtenerDatosAbono")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    onAbono.onError(ioe.getMessage());
                }

                @Override
                public void onResponse(Call call, Response rspns) throws IOException {
                    String json = rspns.body().string();
                    if (rspns.isSuccessful()) {
                        DtoAbonoComprobante dtoAbonoComprobante = gson.fromJson(json, DtoAbonoComprobante.class);
                        onAbono.onSuccess(dtoAbonoComprobante);
                    } else {
                        onAbono.onError(json);
                    }
                }
            });

        } catch (Exception e) {
            onAbono.onError(e.getMessage());
        }
    }

}

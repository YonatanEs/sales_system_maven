package Secciones;

import Clases.CalculadoraBilletes;
import Clases.ConvertidorPanelAPdf;
import Clases.OnDatosEmpresa;
import Clases.Tools;
import Clases.Txt_buscador;
import Dto.DtoCerrarTurno;
import Dto.DtoEfectivoCaja;
import Dto.DtoResponseOb;
import Dto.DtoUnirATurno;
import Dto.Dto_cajaSelected_abierta;
import Dto.Dto_turnoUserAuth;
import Dto.RequestMessage;
import Dto.RespuestaPaginada;
import Dto.ValorRecuest;
import Dto.ValorRecuestAndId;
import Dto.ValorRequestPag;
import General.UtilMessage;
import General.userAuth;
import Objects.Cajas_ob;
import Objects.DatosEmpresaOb;
import Objects.MovimientosTurno_ob;
import Objects.TurnoRegistrar;
import Objects.Turno_ob;
import Vistas.Home;
import Vistas.UtilPanels;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Turnos {

    private Home home;
    private UtilPanels panel;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Txt_buscador buscadorFecha;
    private Txt_buscador buscadorFechaMT;

    private Turno_ob turnoLogged;

    private Turno_ob turnoSelected;

    private int pagina_actual;

    private JDialog d_selectedCaja;
    private JDialog d_saldoInicial;
    private JDialog d_arqueo;
    private JDialog d_cierreTurno;
    private JDialog d_efectivo;
    private JDialog d_movimientosTurno;
    private JDialog d_reportecierre;

    private boolean turnoAbierto;
    private Cajas_ob cajaPorAbrir = null;

    private CalculadoraBilletes calculadora;
    private DtoCerrarTurno dtoCerrarTurno;

    private boolean ingresoEfectivo;
    private boolean turnoForzado = false;

    public Turnos(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;

        ValidarTurnoUserAuth();
        Tools.btn_animacion_size(home.jL_abrir_turno);
        Tools.btn_animacion_size(home.jL_depositrar_turno);
        Tools.btn_animacion_size(home.jL_retirar_turno);
        Tools.btn_animacion_size(home.btn_anteriorpag_turno);
        Tools.btn_animacion_size(home.btn_sigpag_turno);

        Tools.txt_precio(panel.txt_saldo_inicial_turno);
        Tools.txt_precio(panel.txt_efectivoAC_turno);
        Tools.txt_precio(panel.txt_importeEfectivo_turno);

        Tools.focusWhite_comp(Arrays.asList(panel.txt_saldo_inicial_turno, panel.txt_efectivoAC_turno,
                panel.txt_importeEfectivo_turno, panel.txt_importeEfectivo_turno, panel.txt_conceptoEfectivo_turno));

        d_selectedCaja = Tools.newWindow(panel.jP_seleccionarCaja_turno);
        d_saldoInicial = Tools.newWindow(panel.jP_saldoInicial_turno);
        d_arqueo = Tools.newWindow(panel.jP_arqueoCaja_turno);
        d_cierreTurno = Tools.newWindow(panel.jP_cierreCaja_turno);
        d_efectivo = Tools.newWindow(panel.jP_efectivo_turno);
        d_movimientosTurno = Tools.newWindow(panel.jP_historial_movimientos_turno);
        d_reportecierre = Tools.newWindow(panel.jP_reporte_cierre_de_turno);

        calculadora = new CalculadoraBilletes();

        buscadorFecha = new Txt_buscador("Buscar por fecha..", home.txt_buscadorfecha_turno);
        buscadorFechaMT = new Txt_buscador("Buscar por fecha..", panel.txt_buscadorfechaMT_turno);

        Tools.diseñotabla1(home.tableTurno);
        Tools.diseñotabla1(panel.tableMoviemtosTurno);
        Listeners();
    }

    private void Listeners() {

        home.tableTurno.addMouseListener(new MouseAdapter() {

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
                    int row = home.tableTurno.rowAtPoint(e.getPoint());

                    if (row >= 0 && row < home.tableTurno.getRowCount()) {

                        home.tableTurno.setRowSelectionInterval(row, row);
                        int id = Integer.parseInt(home.tableTurno.getValueAt(row, 0).toString());

                        turnoSelected = turnoSelected(id);

                        if (turnoSelected == null || turnoSelected.getId() == 0) {
                            return;
                        }

                        if (turnoSelected.getEstado().equals("Abierto")
                                && userAuth.getUsuario().getPermisos().endsWith("Administrador")
                                && userAuth.getUsuario().getId()!=turnoSelected.getUserMaster().getId()) {
                            home.jM_forzarcierredecaja.setVisible(true);
                        } else {
                            home.jM_forzarcierredecaja.setVisible(false);
                        }

                        home.jPopupTurno.show(e.getComponent(), e.getX(), e.getY());

                    } else {
                        turnoSelected = null;
                    }
                }
            }
        });

        home.jM_vermovimientos.addActionListener((e) -> {
            if (turnoSelected != null && turnoSelected.getId() > 0) {
                winVerMovimientos();
            } else {
                JOptionPane.showMessageDialog(null, "Seleccione una fila");
            }
        });

        home.jM_reportecierredecaja.addActionListener((e) -> {
            winReporteCierre(turnoSelected);
        });

        home.jM_forzarcierredecaja.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(null, "Forzar cierre del turno", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                panel.txt_efectivoAC_turno.setText("Q ");
                panel.txt_efectivoAC_turno.setBackground(Color.WHITE);
                turnoForzado = true;
                d_arqueo.setVisible(true);
            }
        });

        home.jD_selectFecha_turno.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                // Aquí obtienes la nueva fecha seleccionada
                java.util.Date fechaSeleccionada = home.jD_selectFecha_turno.getDate();

                if (fechaSeleccionada != null) {
                    java.text.SimpleDateFormat formateador = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    String fechaFormateada = formateador.format(home.jD_selectFecha_turno.getDate());

                    buscadorFecha.setTextoManual(fechaFormateada);
                }
            }
        });

        panel.jD_selectFechaMT_turno.getDateEditor().addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                // Aquí obtienes la nueva fecha seleccionada
                java.util.Date fechaSeleccionada = panel.jD_selectFechaMT_turno.getDate();

                if (fechaSeleccionada != null) {
                    java.text.SimpleDateFormat formateador = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    String fechaFormateada = formateador.format(panel.jD_selectFechaMT_turno.getDate());

                    buscadorFechaMT.setTextoManual(fechaFormateada);
                }
            }
        });

        Tools.buscadorTablaValidate(home.txt_buscadorfecha_turno, "Buscar por fecha..", (consulta) -> {
            tabledates(buscadorFecha.getTextoReal(), pagina_actual);
        });

        Tools.buscadorTablaValidate(panel.txt_buscadorfechaMT_turno, "Buscar por fecha..", (consulta) -> {
            tableDatesMT(consulta);
        });

        //boton siguiente de tabla turno
        home.btn_sigpag_turno.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                boolean isEnabled = home.btn_sigpag_turno.isEnabled();
                if (isEnabled) {
                    pagina_actual++;
                    tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                }
            }
        });

        //btn anterior de tabla turno
        home.btn_anteriorpag_turno.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pagina_actual > 0) {
                    boolean isEnabled = home.btn_anteriorpag_turno.isEnabled();
                    if (isEnabled) {
                        pagina_actual--;
                        tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                    }
                }
            }
        });

        home.jL_abrir_turno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!turnoAbierto) {
                    llenarCbxSelectedCaja();
                    d_selectedCaja.setVisible(true);
                } else {
                    panel.txt_efectivoAC_turno.setText("Q ");
                    panel.txt_efectivoAC_turno.setBackground(Color.WHITE);
                    turnoForzado = false;
                    d_arqueo.setVisible(true);
                }
            }
        });

        panel.btn_siguienteSC_turno.addActionListener((e) -> {
            validarSeleccionCaja();
        });

        panel.btn_abrirturno_turno.addActionListener((e) -> {
            abrirTurno();
        });

        panel.btn_siguienteAC_turno.addActionListener((e) -> {
            if(turnoForzado){
                arqueoCaja(turnoSelected.getId());
            }else{
                arqueoCaja(userAuth.getIdTurnoActivo());
            }
        });

        panel.btn_calculadoraBilletes_turno.addActionListener((e) -> {
            calculadora.setVisible();
        });

        calculadora.addAction(() -> {
            calculadora.dispose();
            panel.txt_efectivoAC_turno.setText("Q " + calculadora.getTotalEfectivo());
        });

        panel.btn_cerrarturnoCC_turno.addActionListener((e) -> {
            int resp = JOptionPane.showConfirmDialog(null, "¿Estas seguro de cerrar el turno?", "Message", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                cerrarTurno();
            }
        });

        home.jL_depositrar_turno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (home.jL_depositrar_turno.isEnabled()) {

                    ingresoEfectivo = true;
                    panel.jL_tituloEfectivo_turno.setText("<html><u>Ingresar efectivo</u></html>");
                    panel.btn_continuarEfectivo_turno.setText("Ingresar");
                    panel.txt_importeEfectivo_turno.setText("Q ");
                    panel.txt_importeEfectivo_turno.setBackground(Color.white);
                    panel.txt_conceptoEfectivo_turno.setText("");
                    panel.txt_conceptoEfectivo_turno.setBackground(Color.white);
                    d_efectivo.setVisible(true);

                }
            }
        });

        home.jL_retirar_turno.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (home.jL_retirar_turno.isEnabled()) {
                    ingresoEfectivo = false;
                    panel.jL_tituloEfectivo_turno.setText("<html><u>Retirar efectivo</u></html>");
                    panel.btn_continuarEfectivo_turno.setText("Retirar");
                    panel.txt_importeEfectivo_turno.setText("Q ");
                    panel.txt_importeEfectivo_turno.setBackground(Color.white);
                    panel.txt_conceptoEfectivo_turno.setText("");
                    panel.txt_conceptoEfectivo_turno.setBackground(Color.white);
                    d_efectivo.setVisible(true);
                }
            }
        });

        panel.btn_continuarEfectivo_turno.addActionListener((e) -> {
            if (ingresoEfectivo) {
                ingresarEfectivo();
            } else {
                retirarEfectivo();
            }
        });

        panel.btn_imprimirpdf_reporteCierre.addActionListener((e) -> {
            imprimirReportePreview();
        });
    }

    //llena los datos de la tabla principal de turnos
    public void tabledates(String busqueda, int pagina) {
        pagina_actual = pagina;
        try {
            DefaultTableModel modelo = (DefaultTableModel) home.tableTurno.getModel();

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRequestPag valorRecuest = new ValorRequestPag(busqueda, pagina, 20);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/listarTurnos")
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

                    Type type = new TypeToken<RespuestaPaginada<Turno_ob>>() {
                    }.getType();
                    RespuestaPaginada<Turno_ob> rs = gson.fromJson(json, type);

                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);

                        if (rs != null && rs.getContent() != null) {
                            for (Turno_ob turno : rs.getContent()) {

                                String fApertura = (turno.getFechaApertura() != null) ? Tools.formateadorFechaGuatemala(turno.getFechaApertura()) : "";
                                String fCierre = (turno.getFechaCierre() != null) ? Tools.formateadorFechaGuatemala(turno.getFechaCierre()) : "Sin cerrar";

                                BigDecimal ingresosTotales = BigDecimal.ZERO;
                                if (turno.getIngresos() != null) {
                                    ingresosTotales = ingresosTotales.add(turno.getIngresos());
                                }
                                if (turno.getVentas() != null) {
                                    ingresosTotales = ingresosTotales.add(turno.getVentas());
                                }

                                BigDecimal diferencia = BigDecimal.ZERO;
                                if (turno.getSaldoFinal() != null && turno.getArqueo() != null) {
                                    diferencia = turno.getArqueo().subtract(turno.getSaldoFinal());
                                }
                                modelo.addRow(new Object[]{
                                    turno.getId(),//id del turno
                                    turno.getCaja().getNombre(),//nombre de la caja
                                    fApertura,//fecha apertura
                                    fCierre,//fecha cierre
                                    turno.getSaldoInicial(),//saldo inicial
                                    ingresosTotales,//ingresos de ingreso + ventas
                                    turno.getSalidas() != null ? turno.getSalidas() : BigDecimal.ZERO,//salidas de efectivo
                                    turno.getSaldoFinal() != null ? turno.getSaldoFinal() : BigDecimal.ZERO,//saldo total esperado
                                    turno.getArqueo() != null ? turno.getArqueo() : BigDecimal.ZERO,//efectivo fisico
                                    diferencia,//la diferencia entre saldo final y arqueo
                                    turno.getEstado()//estado de la caja

                                });

                                home.btn_sigpag_turno.setEnabled(!rs.isLast());
                                home.btn_anteriorpag_turno.setEnabled(pagina > 0);
                                home.jLabel_pags_turno.setText("Pagina     " + (pagina + 1) + "     de    " + rs.getTotalPages());
                            }

                        } else {
                            home.btn_sigpag_turno.setEnabled(false);
                            home.btn_anteriorpag_turno.setEnabled(false);
                            home.jLabel_pags_turno.setText("Pagina     " + 0 + "     de    " + 0);
                        }

                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }

    //llenar la informacion de la ventana de turnos
    public void llenarInfoTurno() {
        turnoLogged = turnoSelected(userAuth.getIdTurnoActivo());
        if (userAuth.getIdTurnoActivo() > 0) {
            turnoAbierto = true;

            home.jL_t_caja_turno.setVisible(true);
            home.jL_caja_turno.setVisible(true);
            home.jL_caja_turno.setText(turnoLogged.getCaja().getNombre());

            home.jL_t_apertura_turno.setVisible(true);
            home.jL_apertura_turno.setVisible(true);
            home.jL_apertura_turno.setText(Tools.formateadorFechaGuatemala(turnoLogged.getFechaApertura()));

            home.jL_t_abiertopor_turno.setVisible(true);
            home.jL_abiertopor_turno.setVisible(true);
            home.jL_abiertopor_turno.setText(turnoLogged.getUserMaster().getNombre());

            home.jL_t_saldo_actual.setVisible(true);
            home.jL_saldoactual_turno.setVisible(true);
            home.jL_saldoactual_turno.setText("Q. " + turnoLogged.getSaldoFinal());

            home.jL_depositrar_turno.setEnabled(true);
            home.jL_retirar_turno.setEnabled(true);

            if (userAuth.isIsMasterCaja()) {
                home.jL_abrir_turno.setVisible(true);
                home.jL_abrir_turno.setText("Cerrar turno");
            } else {
                home.jL_abrir_turno.setVisible(false);
            }

        } else {
            turnoAbierto = false;

            home.jL_t_caja_turno.setVisible(false);
            home.jL_caja_turno.setVisible(false);

            home.jL_t_apertura_turno.setVisible(false);
            home.jL_apertura_turno.setVisible(false);

            home.jL_t_abiertopor_turno.setVisible(false);
            home.jL_abiertopor_turno.setVisible(false);

            home.jL_t_saldo_actual.setVisible(false);
            home.jL_saldoactual_turno.setVisible(false);

            home.jL_abrir_turno.setVisible(true);
            home.jL_abrir_turno.setText("Abrir turno");

            home.jL_depositrar_turno.setEnabled(false);
            home.jL_retirar_turno.setEnabled(false);

        }
    }

    //valida que el usuario tenga un turno abierto
    public void ValidarTurnoUserAuth() {
        try {
            //Conexion a la api
            RequestBody body = RequestBody.create("" + userAuth.getUsuario().getId(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/validarTurno")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Dto_turnoUserAuth turno = gson.fromJson(json, Dto_turnoUserAuth.class);
                userAuth.validarTurno(turno.getIdTurnoActivo(), turno.isMasterCaja());
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
    }

    //retorna un objeto turno al enviarle el id
    public Turno_ob turnoSelected(int id) {
        try {
            //Conexion a la api
            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/turnoSelected")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            //JOptionPane.showMessageDialog(null, response.body().string());
            if (response.isSuccessful()) {
                String json = response.body().string();
                Turno_ob turno = gson.fromJson(json, Turno_ob.class);
                return turno;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }

    //llena el combobox caja ubicado en abrir turno 
    private void llenarCbxSelectedCaja() {
        //Conexion a la api
        ValorRecuest valorRecuest = new ValorRecuest("");
        String jsonBody = gson.toJson(valorRecuest);
        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/cajas/ListaCajas")
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
                int code = response.code();

                if (response.isSuccessful()) {
                    Type listType = new TypeToken<List<Cajas_ob>>() {
                    }.getType();
                    List<Cajas_ob> cajas = gson.fromJson(json, listType);

                    SwingUtilities.invokeLater(() -> {
                        panel.cbx_seleccionarcaja_turno.removeAllItems();
                        panel.cbx_seleccionarcaja_turno.addItem(new Cajas_ob(0, "Seleccione un opción", "Activo"));
                        for (Cajas_ob caja : cajas) {
                            panel.cbx_seleccionarcaja_turno.addItem(caja);
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

    //valida que la caja seleccionada no tenga un turno abierto
    private void validarSeleccionCaja() {
        boolean val = true;

        Cajas_ob cajaSelected = (Cajas_ob) panel.cbx_seleccionarcaja_turno.getSelectedItem();

        if (cajaSelected.getId() <= 0) {
            JOptionPane.showMessageDialog(null, "¡Debes seleccionar una caja!");
            val = false;
        }

        if (val) {
            //Conexion a la api
            cajaPorAbrir = cajaSelected;

            String jsonBody = gson.toJson(cajaSelected);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/validarCajaSeleccionada")
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
                    int code = response.code();

                    if (response.isSuccessful()) {

                        Dto_cajaSelected_abierta dto = gson.fromJson(json, Dto_cajaSelected_abierta.class);
                        SwingUtilities.invokeLater(() -> {
                            if (dto.isAbierta()) {
                                Object[] opciones = {"Sí, unirme al turno", "No, cancelar"};

                                int respuesta = JOptionPane.showOptionDialog(
                                        null, // Ventana padre
                                        dto.getMessage(),
                                        "Turno Activo Detectado",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null, // Icono por defecto
                                        opciones, // Botones personalizados
                                        opciones[0] // Botón enfocado por defecto
                                );

                                if (respuesta == JOptionPane.YES_OPTION) {
                                    d_selectedCaja.dispose();
                                    unirAlTurno(dto.getIdTurno());
                                }
                            } else {
                                d_selectedCaja.dispose();
                                panel.txt_saldo_inicial_turno.setText("Q ");
                                panel.jL_nombrecajaSI_turno.setText(cajaPorAbrir.getNombre());
                                d_saldoInicial.setVisible(true);
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
    }

    //abre un turno
    private void abrirTurno() {
        boolean val = true;

        BigDecimal saldoInicial = BigDecimal.valueOf(Tools.getPrecioLimpio(panel.txt_saldo_inicial_turno));

        if (saldoInicial.compareTo(BigDecimal.ZERO) <= 0) {
            panel.txt_saldo_inicial_turno.setBackground(Color.red);
            val = false;
        }

        if (val) {

            //conexion a la api
            TurnoRegistrar turnoRegistrar = new TurnoRegistrar(cajaPorAbrir, userAuth.getUsuario(), saldoInicial);
            String json = gson.toJson(turnoRegistrar);

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/abrirTurno")
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
                                ValidarTurnoUserAuth();
                                llenarInfoTurno();
                                d_saldoInicial.dispose();
                                tabledates(buscadorFecha.getTextoReal(), pagina_actual);
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
            JOptionPane.showMessageDialog(null, "Debes llenar el campo saldo inicial");
        }
    }

    //en caso de que la caja seleccionada tenga un turno abierto se une a ese turno
    private void unirAlTurno(int idTurno) {
        DtoUnirATurno unirATurno = new DtoUnirATurno(idTurno, userAuth.getUsuario().getId());
        String jsonBody = gson.toJson(unirATurno);
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/turnos/unirATurno")
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
                int code = response.code();

                if (response.isSuccessful()) {
                    RequestMessage dto = gson.fromJson(json, RequestMessage.class);
                    SwingUtilities.invokeLater(() -> {
                        d_selectedCaja.dispose();
                        ValidarTurnoUserAuth();
                        llenarInfoTurno();
                        tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                        UtilMessage.messageAprobation(dto.getMessage());

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

    //funcion de conteo del efectivo disponible
    private void arqueoCaja(int id) {
        turnoLogged = turnoSelected(id);
        double efectivo = Tools.getPrecioLimpio(panel.txt_efectivoAC_turno);
        int resp = JOptionPane.YES_OPTION;
        if (efectivo <= 0) {
            resp = JOptionPane.showConfirmDialog(null, "El efectivo es 0 \n  ¿Estás seguro de continuar?", "Message", JOptionPane.YES_NO_OPTION);
        }

        if (resp == JOptionPane.YES_OPTION) {
            d_arqueo.dispose();
            dtoCerrarTurno = new DtoCerrarTurno(turnoLogged.getId(), userAuth.getUsuario(), BigDecimal.valueOf(efectivo));
            winCerrarTurno();

        }
    }

    //muestra la venta de cerrar el turno con la info necesaria
    private void winCerrarTurno() {

        panel.jL_datosTurno.setText(turnoLogged.getId() + " - " + turnoLogged.getCaja().getNombre());
        panel.jL_saldoinicialCC_turno.setText("Q. " + turnoLogged.getSaldoInicial());
        panel.jL_ingresosCC_turno.setText("Q. " + turnoLogged.getIngresos());
        panel.jL_ventasCC_turno.setText("Q. " + turnoLogged.getVentas());
        panel.jL_cobrocreditosCC_turno.setText("Q. " + turnoLogged.getCobroCredito());
        panel.jL_depositosventas_turno.setText("Q. " + turnoLogged.getVentaDepositos());
        panel.jL_retirosCC_turno.setText("Q. " + turnoLogged.getSalidas());

        panel.jL_saldoesperadoCC_turno.setText("Q. " + turnoLogged.getSaldoFinal());
        panel.jL_efectivoCC_turno.setText("Q. " + dtoCerrarTurno.getArqueo());

        BigDecimal diferencia = dtoCerrarTurno.getArqueo().subtract(turnoLogged.getSaldoFinal());

        BigDecimal saldoFaltante = BigDecimal.ZERO;
        BigDecimal saldoSobrante = BigDecimal.ZERO;

        int comparacion = diferencia.compareTo(BigDecimal.ZERO);

        if (comparacion < 0) {
            saldoFaltante = diferencia.abs();
            saldoSobrante = BigDecimal.ZERO;
        } else if (comparacion > 0) {
            saldoFaltante = BigDecimal.ZERO;
            saldoSobrante = diferencia;
        } else {
            saldoFaltante = BigDecimal.ZERO;
            saldoSobrante = BigDecimal.ZERO;
        }

        panel.jL_saldofaltanteCC_turno.setText("Q. " + saldoFaltante);
        panel.jL_saldosobranteCC_turno.setText("Q. " + saldoSobrante);

        d_cierreTurno.setVisible(true);
    }

    //funcion para cerrar turno
    private void cerrarTurno() {
        String jsonBody = gson.toJson(dtoCerrarTurno);
        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));
        String url = "/cerrarTurno";
        
        if(turnoForzado){
            url = "/cerrarTurnoForzado";
        }
        
        Request request = new Request.Builder()
                .url(General.properties.getUrl() + "/api/turnos"+url)
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
                int code = response.code();

                if (response.isSuccessful()) {
                    java.lang.reflect.Type tipoRespuesta = new com.google.gson.reflect.TypeToken<DtoResponseOb<Turno_ob>>() {
                    }.getType();

                    DtoResponseOb<Turno_ob> dto = gson.fromJson(json, tipoRespuesta);
                    SwingUtilities.invokeLater(() -> {
                        d_cierreTurno.dispose();
                        ValidarTurnoUserAuth();
                        llenarInfoTurno();
                        tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                        winReporteCierre(dto.getData());
                        turnoLogged = null;
                        UtilMessage.messageAprobation(dto.getMessage());

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

    //funcion para ingresar efectivo a la caja
    private void ingresarEfectivo() {
        boolean val = true;
        BigDecimal importe = BigDecimal.valueOf(Tools.getPrecioLimpio(panel.txt_importeEfectivo_turno));
        String concepto = panel.txt_conceptoEfectivo_turno.getText();

        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            panel.txt_importeEfectivo_turno.setBackground(Color.red);
            val = false;
        }
        if (concepto.trim().isEmpty()) {
            panel.txt_conceptoEfectivo_turno.setBackground(Color.red);
            val = false;
        }

        if (val) {
            DtoEfectivoCaja efectivo = new DtoEfectivoCaja(userAuth.getIdTurnoActivo(), "Ingreso", importe, concepto);
            String jsonBody = gson.toJson(efectivo);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/ingresoRetiroEfectivo")
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
                    int code = response.code();

                    if (response.isSuccessful()) {
                        RequestMessage dto = gson.fromJson(json, RequestMessage.class);
                        SwingUtilities.invokeLater(() -> {
                            d_efectivo.dispose();
                            ValidarTurnoUserAuth();
                            llenarInfoTurno();
                            tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                            UtilMessage.messageAprobation(dto.getMessage());

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
        } else {
            JOptionPane.showMessageDialog(null, "Debes llenar todos los campos");
        }
    }

    //funcion para retirar efectivo de la caja
    private void retirarEfectivo() {
        boolean val = true;
        BigDecimal importe = BigDecimal.valueOf(Tools.getPrecioLimpio(panel.txt_importeEfectivo_turno));
        String concepto = panel.txt_conceptoEfectivo_turno.getText();

        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            panel.txt_importeEfectivo_turno.setBackground(Color.red);
            val = false;
        }
        if (concepto.trim().isEmpty()) {
            panel.txt_conceptoEfectivo_turno.setBackground(Color.red);
            val = false;
        }

        if (val) {
            DtoEfectivoCaja efectivo = new DtoEfectivoCaja(userAuth.getIdTurnoActivo(), "Retiro", importe, concepto);
            String jsonBody = gson.toJson(efectivo);
            RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/ingresoRetiroEfectivo")
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
                    int code = response.code();

                    if (response.isSuccessful()) {
                        RequestMessage dto = gson.fromJson(json, RequestMessage.class);
                        SwingUtilities.invokeLater(() -> {
                            d_efectivo.dispose();
                            ValidarTurnoUserAuth();
                            llenarInfoTurno();
                            tabledates(buscadorFecha.getTextoReal(), pagina_actual);
                            UtilMessage.messageAprobation(dto.getMessage());

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
    }

    private void winVerMovimientos() {
        panel.jL_infoturnoMT_turno.setText(turnoSelected.getId() + " - " + turnoSelected.getCaja().getNombre());
        panel.jL_fechaMT_turno.setText(Tools.formateadorFechaGuatemala(turnoSelected.getFechaApertura()));

        tableDatesMT("");
        d_movimientosTurno.setVisible(true);
    }

    public void tableDatesMT(String busqueda) {
        try {
            DefaultTableModel modelo = (DefaultTableModel) panel.tableMoviemtosTurno.getModel();
            /*Eliminar los elementos en la tabla*/
            modelo.setRowCount(0);

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRecuestAndId valorRecuest = new ValorRecuestAndId(turnoSelected.getId(), busqueda);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/turnos/listarMovimientosTurnos")
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
                    int code = response.code();

                    if (response.isSuccessful()) {
                        Type listType = new TypeToken<List<MovimientosTurno_ob>>() {
                        }.getType();
                        List<MovimientosTurno_ob> movimientos = gson.fromJson(json, listType);

                        List<Object[]> datos = new ArrayList<>();
                        for (MovimientosTurno_ob movimiento : movimientos) {
                            datos.add(new Object[]{
                                movimiento.getId(),
                                Tools.formateadorFechaGuatemala(movimiento.getFecha()),
                                movimiento.getTipo(),
                                movimiento.getConcepto(),
                                "Q " + movimiento.getImporte()
                            });
                        }

                        SwingUtilities.invokeLater(() -> {
                            try {
                                for (Object[] fila : datos) {
                                    modelo.addRow(fila);
                                }
                                panel.tableMoviemtosTurno.setModel(modelo);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, " Error en la tabla " + e);
                            }
                        });
                    } else {
                        String error = General.Error.parseJsonError(json);
                        UtilMessage.messageWarning("Error " + response.code() + ": " + error);
                    }
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }

    private void winReporteCierre(Turno_ob turno) {
        home.configuracion.obtenerDatosEmpresa(new OnDatosEmpresa() {
            @Override
            public void onSuccess(DatosEmpresaOb empresaOb) {
                Tools.adaptarIconoJLabel(panel.jL_logo_reporteCierre, empresaOb.getLogo());
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });

        panel.jL_noCierre_reporteCierre.setText("Cierre No. " + String.format("%08d", turno.getId()));

        panel.jL_encargado_reporteCierre.setText(turno.getUserMaster().getNombre());
        panel.jL_caja_reporteCierre.setText(turno.getCaja().getNombre());
        panel.jL_estado_reporteCierre.setText(turno.getEstado());

        panel.jL_fechaapertura_reporteCierre.setText(Tools.formateadorFechaGuatemala(turno.getFechaApertura()));
        String fechaCierreTexto = (turno.getFechaCierre() != null)
                ? Tools.formateadorFechaGuatemala(turno.getFechaCierre())
                : "--------";

        panel.jL_fechacierre_reporteCierre.setText(fechaCierreTexto);
        panel.jL_impresion_reporteCierre.setText(Tools.formateadorFechaGuatemala(LocalDateTime.now()));

        panel.jL_saldoinicial_reporteCierre.setText("Q. " + turno.getSaldoInicial().toPlainString());
        panel.jL_ingresos_reporteCierre.setText("Q. " + turno.getIngresos().toPlainString());
        panel.jL_salidas_reporteCierre.setText("Q. " + turno.getSalidas().toPlainString());
        panel.jL_ventas_reporteCierre.setText("Q. " + turno.getVentas().toPlainString());
        panel.jL_cobrocredito_reporteCierre.setText("Q. " + turno.getCobroCredito().toPlainString());
        panel.jL_depositoventa_reporteCierre.setText("Q. " + turno.getVentaDepositos().toPlainString());
        panel.jL_saldofaltante_reporteCierre.setText("Q. " + turno.getSaldoFaltante().toPlainString());
        panel.jL_saldosobrante_reporteCierre.setText("Q. " + turno.getSaldoFaltante().toPlainString());

        panel.jL_saldofinal_reporteCierre.setText("Q. " + turno.getSaldoFinal().toPlainString());
        panel.jL_arqueo_reporteCierre.setText("Q. " + turno.getSaldoFinal().toPlainString());

        d_reportecierre.setVisible(true);
    }

    private void imprimirReportePreview() {
        String nroCierre = String.format("%08d", turnoSelected.getId());

        String rutaPdfTemporal = System.getProperty("java.io.tmpdir")
                + File.separator + "Temp_Cierre_" + nroCierre + ".pdf";

        JPanel panelAImprimir = panel.jP_hoja_reporteCierre;

        boolean conversionOk = ConvertidorPanelAPdf.guardarPanelComoPdf(panelAImprimir, rutaPdfTemporal);

        if (conversionOk) {
            // 5. LEVANTAR EL BOCETO ESTILO OFFICE
            // Buscamos el Frame padre de tu aplicación para centrar el Dialog modal
            home.configuracion.obtenerDatosEmpresa(new OnDatosEmpresa() {
                @Override
                public void onSuccess(DatosEmpresaOb empresaOb) {
                    SwingUtilities.invokeLater(() -> {
                        Clases.VentanaImpresionWord preview = new Clases.VentanaImpresionWord(home, rutaPdfTemporal, nroCierre,
                                empresaOb.getNombre(), empresaOb.getLogo());
                        preview.setVisible(true);
                    });
                }

                @Override
                public void onError(String error) {
                    General.UtilMessage.messageError(error);
                }
            });

            // Bloquea la pantalla de atrás y muestra la vista previa moderna
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo generar el archivo de vista previa.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}

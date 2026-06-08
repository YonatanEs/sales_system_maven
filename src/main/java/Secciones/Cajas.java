package Secciones;

import Clases.MenuEditarYEstado;
import Clases.Tools;
import Clases.Txt_buscador;
import Dto.RequestMessage;
import Dto.ValorRecuest;
import General.UtilMessage;
import General.userAuth;
import Objects.CajaModificar;
import Objects.CajaRegistrar;
import Objects.Cajas_ob;
import Objects.Cliente_ob;
import Vistas.Home;
import Vistas.UtilPanels;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mxrck.autocompleter.TextAutoCompleter;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Cajas {

    private Home home;
    private UtilPanels panel;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private Txt_buscador buscadorCajas;
    private TextAutoCompleter autoCompleter;
    
    private Cajas_ob cajaSelected = null;

    private JDialog d_registrar;

    private MenuEditarYEstado menuTabla;
    private boolean registrando;

    public Cajas(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;

        buscadorCajas = new Txt_buscador("Buscar caja..", home.txt_buscador_caja);
        d_registrar = Tools.newWindow(panel.jP_reg_caja);
        Tools.btn_animacion_size(home.btn_registrarcaja);
        Tools.diseñotabla1(home.tableCajas);
        Tools.focusWhite_comp(Arrays.asList(panel.txt_nombre_caja));
        autoCompleter = new TextAutoCompleter(home.txt_buscador_caja);
        
        Listeners();
    }

    private void Listeners() {

        home.tableCajas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = home.tableCajas.getSelectedRow();
                int id = Integer.parseInt(home.tableCajas.getValueAt(row, 0).toString());

                cajaSelected = cajaSelected(id);
            }
        });

        Tools.buscadorTablaValidate(home.txt_buscador_caja, "Buscar caja..", (consulta) -> {
            tabledates(consulta);
        });

        menuTabla = new MenuEditarYEstado(home.tableCajas, false,
                (id) -> WineditarCaja((int) id),
                (id) -> inactivarCaja((int) id));

        home.btn_registrarcaja.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WinregistrarCaja();
            }
        });

        panel.btn_guardar_caja.addActionListener((e) -> {
            if (registrando) {
                registrarCaja();
            } else {
                modificarCaja();
            }
        });

    }

    public void WinregistrarCaja() {
        registrando = true;
        panel.jL_titulo_caja.setText("<html><u>Nueva caja</u></html>");
        panel.txt_nombre_caja.setText("");
        panel.txt_nombre_caja.setBackground(Color.white);
        panel.btn_guardar_caja.setText("Registrar");

        d_registrar.setVisible(true);
    }

    private void WineditarCaja(int id) {
        registrando = false;
        cajaSelected = cajaSelected(id);
        panel.jL_titulo_caja.setText("<html><u>Editar caja</u></html>");
        panel.txt_nombre_caja.setText(cajaSelected.getNombre());
        panel.txt_nombre_caja.setBackground(Color.white);
        panel.btn_guardar_caja.setText("Actualizar");

        d_registrar.setVisible(true);
    }

    /*-------------------------------Conexion a api cajas----------------------*/
    //actualiza la tabla de cajas
    public void tabledates(String busqueda) {
        try {
            boolean buscando = true;
            if (busqueda.trim().equals("")) {
                buscando = false;
            }

            DefaultTableModel modelo = (DefaultTableModel) home.tableCajas.getModel();
            /*Eliminar los elementos en la tabla*/
            modelo.setRowCount(0);

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRecuest valorRecuest = new ValorRecuest(busqueda);
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

                        List<Object[]> datos = new ArrayList<>();
                        for (Cajas_ob caja : cajas) {
                            datos.add(new Object[]{
                                caja.getId(),
                                caja.getNombre(),
                                caja.getEstado()
                            });
                        }

                        SwingUtilities.invokeLater(() -> {
                            try {
                                for (Object[] fila : datos) {
                                    modelo.addRow(fila);
                                }
                                home.tableCajas.setModel(modelo);
                                sugerenciasBuscadorCajas();
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

    public void registrarCaja() {
        boolean val = true;
        String nombre = panel.txt_nombre_caja.getText();

        if (nombre.trim().isEmpty()) {
            val = false;
            panel.txt_nombre_caja.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "Debes ingresar un nombre");
        }

        if (val) {
            //conexion a la api

            CajaRegistrar cajaRegistrar = new CajaRegistrar(
                    nombre);
            String json = gson.toJson(cajaRegistrar);

            RequestBody body = RequestBody.create(json, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/cajas/registrar")
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

                                Tools.paintgreen(Arrays.asList(panel.txt_nombre_caja));
                                Tools.clean(Arrays.asList(panel.txt_nombre_caja));

                                tabledates("");
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
    }

    public void modificarCaja() {
        boolean val = true;
        String nombre = panel.txt_nombre_caja.getText();

        if (nombre.trim().isEmpty()) {
            val = false;
            panel.txt_nombre_caja.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, "Debes ingresar un nombre");
        }

        if (val) {
            if (cajaSelected.getId()>0) {
                //conexion a la api

                CajaModificar cajaModificar = new CajaModificar(
                        cajaSelected.getId(),
                        nombre);
                String json = gson.toJson(cajaModificar);

                RequestBody body = RequestBody.create(json, JSON);

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/cajas/modificar")
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

                                    d_registrar.dispose();
                                    
                                    tabledates("");
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
            }else{
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
            }
        }
    }

    //activa o inactiva el estado de la caja
    public void inactivarCaja(int id) {
        try {
            //conexion a api
            if (cajaSelected.getId() == 0) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
            } else {
                //Conexion a la api

                String pregunta = "Activar";
                if ("Activo".equals(cajaSelected.getEstado())) {
                    pregunta = "Inactivar";
                }

                int resp = JOptionPane.showConfirmDialog(null, "¿Estas seguro de " + pregunta + " la caja " + cajaSelected.getNombre() + " ?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {

                    RequestBody body = RequestBody.create(String.valueOf(cajaSelected.getId()), MediaType.parse("application/json"));

                    Request request = new Request.Builder()
                            .url(General.properties.getUrl() + "/api/cajas/inactivar")
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
                                        tabledates("");
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
                                    UtilMessage.messageWarning("Error " + response.code());
                                }
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }
    
    //retorna un objeto tipo caja_ob desde la base de datos 
    private Cajas_ob cajaSelected(int id) {
        try {
            //Conexion a la api

            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/cajas/CajaSeleccionada")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String json = response.body().string();
                Cajas_ob caja = gson.fromJson(json, Cajas_ob.class);
                return caja;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }
    
     //llena las sugerencias del buscador de cajas
    private void sugerenciasBuscadorCajas(){
        try {
            //conexion a api
            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/cajas/listarSugerencias")
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
                        autoCompleter.removeAllItems();

                        for (String string : sugerencias) {
                            autoCompleter.addItem(string);
                        }
                    });

                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }
    
}

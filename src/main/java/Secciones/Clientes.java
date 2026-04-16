package Secciones;

import Clases.Tools;
import Dto.ValorRecuest;
import General.UtilMessage;
import General.userAuth;
import Objects.Cliente_ob;
import Vistas.Home;
import Vistas.UtilPanels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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

public class Clientes {
    
    Home home;
    UtilPanels panel;
    
    private static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    
    public Clientes(Home home, UtilPanels panel){
        this.home = home;
        this.panel = panel;
        
        Tools.btn_animacion_size(home.btn_registrarcliente);
        Tools.buscador(home.txt_buscador_cliente, "Buscar cliente..");
        
        customTable();
        Listeners();
        
        tabledates("");
    }
    
  
    private void customTable(){
        home.tableClientes.getTableHeader().setReorderingAllowed(false);
        home.tableClientes.setRowHeight(25);
        Tools.headers(home.tableClientes);
    }
    
    private void Listeners(){
        Tools.aplicarMenuEstado(home.tableClientes, false,
            (id) -> ActoinactivarUsuario((int) id),
            (id) -> editarCliente((int) id));
    }
    
    private void editarCliente(int id){
        
    }
    
    private void ActoinactivarUsuario(int id){
        
    };
    
    public void tabledates(String busqueda) {
        try {
            boolean buscando = true;
            if (busqueda.trim().equals("")) {
                buscando = false;
            }

            DefaultTableModel modelo = (DefaultTableModel) home.tableClientes.getModel();
            /*Eliminar los elementos en la tabla*/
            modelo.setRowCount(0);

            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRecuest valorRecuest = new ValorRecuest(busqueda);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/clientes/ListaClientes")
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
                        Type listType = new TypeToken<List<Cliente_ob>>() {
                        }.getType();
                        List<Cliente_ob> clientes = gson.fromJson(json, listType);

                        List<Object[]> datos = new ArrayList<>();
                        for (Cliente_ob cliente : clientes) {
                            datos.add(new Object[]{
                                cliente.getId(),
                                cliente.getNombre(),
                                cliente.getNit(),
                                cliente.getTelefono(),
                                cliente.getDireccion(),
                                cliente.getEstado()
                            });
                        }

                        SwingUtilities.invokeLater(() -> {
                            try {
                                for (Object[] fila : datos) {
                                    modelo.addRow(fila);
                                }
                                home.tableClientes.setModel(modelo);
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
    
}

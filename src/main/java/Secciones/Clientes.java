package Secciones;

import Clases.Tools;
import Dto.RequestMessage;
import Dto.ValorRecuest;
import General.UtilMessage;
import General.userAuth;
import Objects.ClienteRegistrar;
import Objects.Cliente_ob;
import Objects.UsuarioRegistrar;
import Vistas.Home;
import Vistas.UtilPanels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JComponent;
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

public class Clientes {
    
    Home home;
    UtilPanels panel;
    
    private static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private JDialog d_registrar;
    private boolean registrando;
    
    public Clientes(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;
        
        Tools.btn_animacion_size(home.btn_registrarcliente);
        Tools.buscador(home.txt_buscador_cliente, "Buscar cliente..");
      
        Tools.clickwhite_comp(Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente));
        customTable();
        Listeners();
        
        
        
        tabledates("");
    }
    
    private void customTable() {
        home.tableClientes.getTableHeader().setReorderingAllowed(false);
        home.tableClientes.setRowHeight(25);
        Tools.headers(home.tableClientes);
    }
    
    private void Listeners() {
        Tools.aplicarMenuEstado(home.tableClientes, false,
                (id) -> ActoinactivarUsuario((int) id),
                (id) -> editarCliente((int) id));
        
        home.btn_registrarcliente.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                d_registrar = new JDialog();
                d_registrar.getContentPane().add(panel.jP_reg_cliente);
                d_registrar.pack();
                d_registrar.setLocationRelativeTo(null);
                d_registrar.setResizable(false);
                d_registrar.setModal(true);
                
                panel.jL_titulo_pcliente.setText("<html><u>Nuevo Clientes</u></html>");
                panel.btn_registrar_p_cliente.setText("Registrar");
                registrando = true;
                panel.btn_nuevocliente_p_cliente.setVisible(true);
                Tools.clean(Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente));
                Tools.resetOriginalColor(Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente));
                
                d_registrar.setVisible(true);
            }
        });
        
        panel.btn_nuevocliente_p_cliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tools.clean(Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente));
                
                Tools.resetOriginalColor(Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente));
            }
        });
        
        Arrays.asList(panel.txt_nombre_p_cliente,
                        panel.txt_nit_p_cliente,
                        panel.txt_telefono_p_cliente,
                        panel.txt_direccion_p_cliente)
    }
    
    private void editarCliente(int id) {
        
    }
    
    private void ActoinactivarUsuario(int id) {
        
    }
    
    ;
    
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
    
    public void registrarCliente() {
        boolean valido = true;
        
        String nombre = panel.txt_nombre_p_cliente.getText();
        String nit = panel.txt_nit_p_cliente.getText().trim();
        String telefono = panel.txt_telefono_p_cliente.getText().trim();
        String direccion = panel.txt_direccion_p_cliente.getText();
        
        if (nombre.isEmpty()) {
            panel.txt_nombre_p_cliente.setBackground(Color.red);
            valido = false;
        }
        if (nit.isEmpty()) {
            panel.txt_nit_p_cliente.setBackground(Color.red);
            valido = false;
        }
        if (telefono.isEmpty()) {
            panel.txt_telefono_p_cliente.setBackground(Color.red);
            valido = false;
        }
        if (direccion.isEmpty()) {
            panel.txt_direccion_p_cliente.setBackground(Color.red);
            valido = false;
        }
        
        if (valido) {
            //conexion a la api

            ClienteRegistrar clienteRegistrar = new ClienteRegistrar(
                    nombre,
                    nit,
                    telefono,
                    direccion);
            String json = gson.toJson(clienteRegistrar);
            
            RequestBody body = RequestBody.create(json, JSON);
            
            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/usuarios/registrar")
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
                                
                                Tools.paintgreen(Arrays.asList(panel.txt_nombre_p_cliente,
                                        panel.txt_nit_p_cliente,
                                        panel.txt_telefono_p_cliente,
                                        panel.txt_direccion_p_cliente));
                                Tools.clean(Arrays.asList(panel.txt_nombre_p_cliente,
                                        panel.txt_nit_p_cliente,
                                        panel.txt_telefono_p_cliente,
                                        panel.txt_direccion_p_cliente));
                                
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
}

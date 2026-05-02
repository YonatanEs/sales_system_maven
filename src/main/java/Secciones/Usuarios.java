package Secciones;

import Clases.Cargando;
import Clases.Tools;
import Dto.RequestMessage;
import Dto.Tabla_paginacion;
import Dto.ValorRecuest;
import General.UtilMessage;
import General.properties;
import General.userAuth;
import Objects.UsuarioModificar;
import Objects.UsuarioRegistrar;
import Objects.Usuarios_p;
import Vistas.Home;
import Vistas.Login;
import Vistas.UtilPanels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.Usuario_Ob;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.lang.reflect.Type;
import javax.print.ServiceUIFactory;
import javax.swing.SwingUtilities;
import Clases.X;

public class Usuarios {
    
    private Home home;
    private UtilPanels panel;
    JDialog d_registrar;
    List<JTextField> listparamUser;
    private int id_selected_user;
    Usuarios_p userSelected;
    boolean registrando = true;
    private static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //datos para paginacion tabla
    private static int tamaño_de_pagina = 25;
    private static int pagina_actual = 0;
    private static int total_paginas = 0;
    
    public Usuarios(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;
        
        Tools.btn_animacion_size(home.btn_registrarusuario);
        Tools.buscador(home.txt_buscadorp_usuario, "Buscar usuario..");
        
        listparamUser = new ArrayList<>();
        
        listparamUser.add(panel.txt_nombre_p_usuario);
        
        listparamUser.add(panel.txt_telefono_p_usuario);
        listparamUser.add(panel.txt_usuario_p_usuario);
        listparamUser.add(panel.txt_contraseña_p_usuario);
        listparamUser.add(panel.txt_confirmar_p_usuario);
        Tools.focusWhite_comp((List<JComponent>) (List<?>) listparamUser);
        
        customTable();
        tabledates("");
        Listeners();
        try {
            Tools.aplicarMenuEstado(home.tableUsuarios, true,
                    (id) -> editarUsuario((int) id),
                    (id) -> ActoinactivarUsuario((int) id));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void Listeners() {

        //le damos funcionalidad al hacer click en las filas de usuario
        /*home.tableUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {

                    int row = home.tableUsuarios.rowAtPoint(e.getPoint());

                    if (row != -1) {

                        home.tableUsuarios.setRowSelectionInterval(row, row);
                        String estado = (String) home.tableUsuarios.getValueAt(row, 5);
                        id_selected_user = Integer.parseInt(home.tableUsuarios.getValueAt(row, 0).toString());
                        userSelected = userSelected(id_selected_user);

                        if (id_selected_user == userAuth.getIdUser()) {
                            home.menu_estado_pusuario.setVisible(false);
                        } else {
                            home.menu_estado_pusuario.setVisible(true);
                            if (estado.equalsIgnoreCase("Activo")) {
                                home.menu_estado_pusuario.setText("Inactivar");
                                home.menu_estado_pusuario.setIcon(new ImageIcon(getClass().getResource("/Images/eliminar.png")));
                            } else {
                                home.menu_estado_pusuario.setText("Activar");
                                home.menu_estado_pusuario.setIcon(new ImageIcon(getClass().getResource("/Images/activar.png")));
                            }
                        }
                        home.jPopupMenu_p_usuario.show(e.getComponent(), e.getX(), e.getY());
                    }

                }
            }
        });*/
        //funcionalidad del boton para cambiar el estado de un usuario
        /*home.menu_estado_pusuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActoinactivarUsuario();
            }
        });*/
        //funcionalidad del boton para editar info de los usuarios
        /*home.menu_editar_pusuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                d_registrar = new JDialog();
                d_registrar.getContentPane().add(panel.jP_reg_usuario);
                d_registrar.pack();
                d_registrar.setLocationRelativeTo(null);
                d_registrar.setResizable(false);
                d_registrar.setModal(true);

                registrando = false;
                obtenerdatoseditar();
                Tools.resetOriginalColor(((List<JComponent>) (List<?>) listparamUser));

                d_registrar.setVisible(true);
            }
        });*/
        //funcionalidad del boton de registrar o actualizar del panel de usuarios
        panel.btn_registrar_p_usuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (registrando) {
                    RegistrarUsuario();
                } else {
                    modificarUsuario();
                }
                
            }
        });

        //funcionalidad del boton de nuevo usuario
        panel.btn_nuevousuario_p_usuario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Tools.clean(listparamUser);
                Tools.resetOriginalColor((List<JComponent>) (List<?>) listparamUser);
            }
        });

        //funcionalidad del boton registrar nuevousuario que muestra una ventana registrar usuario
        home.btn_registrarusuario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                d_registrar = new JDialog();
                d_registrar.getContentPane().add(panel.jP_reg_usuario);
                d_registrar.pack();
                d_registrar.setLocationRelativeTo(null);
                d_registrar.setResizable(false);
                d_registrar.setModal(true);
                
                panel.jL_titulo_pusuarios.setText("<html><u>Nuevo Usuario</u></html>");
                panel.btn_registrar_p_usuario.setText("Registrar");
                registrando = true;
                panel.btn_nuevousuario_p_usuario.setVisible(true);
                panel.cb_modificar_contraseña.setVisible(false);
                panel.txt_contraseña_p_usuario.setEnabled(true);
                panel.txt_confirmar_p_usuario.setEnabled(true);
                Tools.clean(listparamUser);
                Tools.resetOriginalColor((List<JComponent>) (List<?>) listparamUser);
                
                d_registrar.setVisible(true);
            }
        });

        //fracmento de codigo que define en la tabla de usuario el color de letra en celda estado
        home.tableUsuarios.getColumnModel().getColumn(Tools.indexColumnEstado(home.tableUsuarios)).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if ("Activo".equals(value)) {
                    cell.setForeground(Color.GREEN);
                } else if ("Inactivo".equals(value)) {
                    cell.setForeground(Color.RED);
                } else {
                    cell.setForeground(Color.BLACK);
                }
                
                return cell;
            }
        });

        //buscador de tabla cliente
        Tools.buscadorTablaValidate(home.txt_buscadorp_usuario,"Buscar usuario..", (consulta) -> {
            Cargando.doSomething(new X() {
                @Override
                public void execute() {
                    tabledates(consulta);
                }
            });
        });
        
    }
    
    private void editarUsuario(int id_selected_user) {
        userSelected = userSelected(id_selected_user);
        
        d_registrar = new JDialog();
        d_registrar.getContentPane().add(panel.jP_reg_usuario);
        d_registrar.pack();
        d_registrar.setLocationRelativeTo(null);
        d_registrar.setResizable(false);
        d_registrar.setModal(true);
        
        registrando = false;
        obtenerdatoseditar();
        Tools.resetOriginalColor(((List<JComponent>) (List<?>) listparamUser));
        
        d_registrar.setVisible(true);
    }
    
    private void customTable() {
        home.tableUsuarios.getTableHeader().setReorderingAllowed(false);
        home.tableUsuarios.setRowHeight(25);
        Tools.headers(home.tableUsuarios);
        
    }
    
    public void tabledates(String busqueda) {
        try {
            boolean buscando = true;
            if (busqueda.trim().equals("")) {
                buscando = false;
            }
            
            DefaultTableModel modelo = (DefaultTableModel) home.tableUsuarios.getModel();
            /*Eliminar los elementos en la tabla*/
            modelo.setRowCount(0);
            
            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRecuest valorRecuest = new ValorRecuest(busqueda);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            Request request = new Request.Builder()
                    .url(properties.getUrl() + "/api/usuarios/ListaUsuarios")
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
                        Type listType = new TypeToken<List<Usuario_Ob>>() {
                        }.getType();
                        List<Usuario_Ob> usuarios = gson.fromJson(json, listType);
                        
                        List<Object[]> datos = new ArrayList<>();
                        for (Usuario_Ob usuario : usuarios) {
                            datos.add(new Object[]{
                                usuario.getId(),
                                usuario.getNombre(),
                                usuario.getTelefono(),
                                usuario.getUsername(),
                                usuario.getPermisos(),
                                usuario.getEstado()
                            });
                        }
                        
                        SwingUtilities.invokeLater(() -> {
                            try {
                                for (Object[] fila : datos) {
                                    modelo.addRow(fila);
                                }
                                home.tableUsuarios.setModel(modelo);
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

            /*String sql = buscando ? "SELECT * FROM users WHERE nombre LIKE ? OR username LIKE ?"
                    : "SELECT * FROM users";
            ArrayList<Usuarios_p> listaUsuarios = new ArrayList<>();

            Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(sql);

            if (buscando) {
                p.setString(1, "%" + busqueda + "%");
                p.setString(2, "%" + busqueda + "%");
            }

            ResultSet r = p.executeQuery();
            while (r.next()) {
                listaUsuarios.add(new Usuarios_p(
                        r.getInt("id_user"),
                        r.getString("nombre"),
                        r.getString("telefono"),
                        r.getString("username"),
                        r.getString("permisos"),
                        r.getString("estado")
                ));
            }

            DefaultTableModel model = (DefaultTableModel) home.tableUsuarios.getModel();
            Tools.vaciartabla(model);

            for (Usuarios_p usuario : listaUsuarios) {
                Object[] fila = new Object[]{usuario.getId(), usuario.getNombre(), usuario.getTelefono(),
                    usuario.getUsername(), usuario.getPermisos(), usuario.getEstado()};
                model.addRow(fila);
            }

            home.tableUsuarios.setModel(model);*/
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }
    
    public void RegistrarUsuario() {
        boolean valido = true;
        
        for (JTextField fiel : listparamUser) {
            if (fiel.getText().trim().equals("")) {
                valido = false;
                fiel.setBackground(Color.red);
            }
        }
        
        String nombre;
        String telefono;
        String username;
        String permisos;
        String contraseña;
        
        if (valido) {
            if (panel.txt_contraseña_p_usuario.getText().trim().equals(
                    panel.txt_confirmar_p_usuario.getText().trim())) {
                try {
                    //conexion a la api

                    UsuarioRegistrar usuarioRegistrar = new UsuarioRegistrar(
                            panel.txt_nombre_p_usuario.getText(),
                            panel.txt_telefono_p_usuario.getText(),
                            panel.txt_usuario_p_usuario.getText(),
                            panel.txt_contraseña_p_usuario.getText(),
                            (String) panel.cbx_permisos_p_usuario.getSelectedItem());
                    String json = gson.toJson(usuarioRegistrar);
                    
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
                                        
                                        Tools.paintgreen((List<JComponent>) (List<?>) listparamUser);
                                        Tools.clean(listparamUser);
                                        
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

                    /*Connection c = Conexion.conectar();
                    PreparedStatement p = c.prepareStatement("SELECT * FROM users WHERE username=?");
                    p.setString(1, panel.txt_usuario_p_usuario.getText());

                    ResultSet r = p.executeQuery();

                    if (r.next()) {
                        c.close();
                        JOptionPane.showMessageDialog(null, "El nombre de usuario ya existe");
                    } else {
                        PreparedStatement p2 = c.prepareStatement("INSERT INTO users VALUE(?,?,?,?,?,?,?)");
                        p2.setInt(1, 0);
                        p2.setString(2, panel.txt_nombre_p_usuario.getText());
                        p2.setString(3, panel.txt_telefono_p_usuario.getText());
                        p2.setString(4, panel.txt_usuario_p_usuario.getText());
                        p2.setString(5, panel.txt_contraseña_p_usuario.getText());
                        p2.setString(6, panel.cbx_permisos_p_usuario.getSelectedItem().toString());
                        p2.setString(7, "Activo");

                        p2.executeUpdate();
                        c.close();

                        Tools.paintgreen((List<JComponent>)(List<?>)listparamUser);
                        Tools.clean(listparamUser);

                        tabledates("");

                        JOptionPane.showMessageDialog(null, "¡Usuario Registrado correctamente!", "", JOptionPane.PLAIN_MESSAGE, home.bien);

                    }*/
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error " + e);
                }
                
            } else {
                JOptionPane.showMessageDialog(null, "¡Las contraseñas no coinciden!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
        
    }
    
    private void ActoinactivarUsuario(int id_user_seleted) {
        
        userSelected = userSelected(id_user_seleted);
        try {
            //conexion a api
            if (id_user_seleted == 0) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
            } else {
                //Conexion a la api
                userSelected = userSelected(userSelected.getId());
                
                String pregunta = "Activar";
                if ("Activo".equals(userSelected.getEstado())) {
                    pregunta = "Inactivar";
                }
                
                int resp = JOptionPane.showConfirmDialog(null, "¿Estas seguro de " + pregunta + "  al usuario " + userSelected.getUsername() + " ?",
                        "Confirmar", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    
                    RequestBody body = RequestBody.create(String.valueOf(userSelected.getId()), MediaType.parse("application/json"));
                    
                    Request request = new Request.Builder()
                            .url(General.properties.getUrl() + "/api/usuarios/inactivar")
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
    
    private void obtenerdatoseditar() {
        try {
            userSelected = userSelected(userSelected.getId());
            
            panel.jL_titulo_pusuarios.setText("<html><u>Editar usuario</u></html>");
            panel.btn_registrar_p_usuario.setText("Actualizar");
            panel.btn_nuevousuario_p_usuario.setVisible(false);
            panel.txt_confirmar_p_usuario.setEnabled(false);
            panel.txt_contraseña_p_usuario.setEnabled(false);
            panel.cb_modificar_contraseña.setSelected(false);
            panel.cb_modificar_contraseña.setVisible(true);
            
            panel.txt_nombre_p_usuario.setText(userSelected.getNombre());
            panel.txt_telefono_p_usuario.setText(userSelected.getTelefono());
            panel.txt_usuario_p_usuario.setText(userSelected.getUsername());
            panel.cbx_permisos_p_usuario.setSelectedItem(userSelected.getPermisos());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }
    
    private void modificarUsuario() {
        
        boolean val = true;
        boolean modificarcontraseña = panel.cb_modificar_contraseña.isSelected();
        int list = listparamUser.size();
        
        if (!modificarcontraseña) {
            list = list - 2;
        }
        
        for (int i = 0; i < list; i++) {
            if (listparamUser.get(i).getText().trim().isEmpty()) {
                val = false;
                listparamUser.get(i).setBackground(Color.red);
            }
        }
        
        if (val) {
            if (modificarcontraseña) {
                if (panel.txt_contraseña_p_usuario.getText().equals(panel.txt_confirmar_p_usuario.getText())) {
                    actualizarusuario(true);
                } else {
                    JOptionPane.showMessageDialog(null, "¡Las contraseñas no coinciden!");
                }
            } else {
                actualizarusuario(false);
            }
        } else {
            JOptionPane.showMessageDialog(null, "¡Debes llenar todos los campos!");
        }
    }
    
    private void actualizarusuario(boolean modificarcontraseña) {
        try {
            //Conexion a la api
            UsuarioModificar usuarioModificar = new UsuarioModificar(
                    userSelected.getId(),
                    panel.txt_nombre_p_usuario.getText(),
                    panel.txt_telefono_p_usuario.getText(),
                    panel.txt_usuario_p_usuario.getText().trim(),
                    panel.txt_contraseña_p_usuario.getText(),
                    panel.cbx_permisos_p_usuario.getSelectedItem().toString(),
                    modificarcontraseña
            );
            
            String json = gson.toJson(usuarioModificar);
            
            RequestBody body = RequestBody.create(json, JSON);
            
            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/usuarios/modificar")
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
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
    }
    
    private Usuarios_p userSelected(long id) {
        try {
            //Conexion a la api

            RequestBody body = RequestBody.create(String.valueOf(id), MediaType.parse("application/json"));
            
            Request request = new Request.Builder()
                    .url(properties.getUrl() + "/api/usuarios/UsuarioSeleccionado")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();
            
            Response response = client.newCall(request).execute();
            
            if (response.isSuccessful()) {
                String json = response.body().string();
                Usuarios_p usuario = gson.fromJson(json, Usuarios_p.class);
                return usuario;
            } else {
                String error = General.Error.parseJsonError(response.body().string());
                UtilMessage.messageWarning("Error " + response.code() + ": " + error);
            }
            /*Connection c = Conexion.conectar();
            PreparedStatement p = c.prepareStatement(
                "SELECT * FROM users WHERE id_user=?");
            p.setInt(1, id);
            ResultSet r = p.executeQuery();
           
            if(r.next()){
                return new Usuarios_p(id, r.getString("nombre"), r.getString("telefono"),
                        r.getString("username"), r.getString("permisos"), r.getString("estado"));
            }*/
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error en conexion " + e);
        }
        return null;
    }
    
}

package Secciones;

import Clases.Cargando;
import Clases.OnDatosEmpresa;
import Clases.Tools;
import Clases.X;
import Dto.Dto_cambiarcontraseña;
import Dto.Dto_datosEmpresa;
import Dto.RequestMessage;
import General.UtilMessage;
import General.userAuth;
import Objects.DatosEmpresaOb;
import Vistas.Home;
import Vistas.UtilPanels;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FileChooserUI;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Configuracion {

    private Home home;
    private UtilPanels panel;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private JDialog d_cambiarcontraseña;
    private JDialog d_infoempresa;

    private ImageIcon iconoAddLogo = new ImageIcon(getClass().getResource("/Images/imagen_icon.png"));
    private File SelectedImageLogo = null;
    private boolean existsLogo = false;

    public Configuracion(Home home, UtilPanels panel) {
        this.home = home;
        this.panel = panel;

        d_cambiarcontraseña = Tools.newWindow(panel.jP_cambiarcontraseña);
        d_infoempresa = Tools.newWindow(panel.jP_editarinforEmpresa);

        Tools.focusWhite_comp(Arrays.asList(panel.txt_passActual, panel.txt_passNuevo,
                panel.txt_passConfirmar));
        Tools.focusWhite_comp(Arrays.asList(panel.txt_nombre_infoEmpresa, panel.txt_nit_infoEmpresa,
                panel.txt_direccion_infoEmpresa, panel.txt_telefono_infoEmpresa, panel.txt_slogan_infoEmpresa));

        Listeners();
    }

    private void Listeners() {
        home.jL_cambiarcontrase_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                home.ocultar_menu();
                winCambiarContraseña();
            }
        });
        home.jL_editarinfoempresa_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                home.ocultar_menu();
                InfoEmpresa();
                d_infoempresa.setVisible(true);
            }
        });
        panel.btn_cambiarContraseña.addActionListener((e) -> {
            cambiarContraseña();
        });

        panel.jL_logo_infoEmpresa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SelectImageLogo();
            }
        });

        panel.btn_actualizar_infoEmpresa.addActionListener((e) -> {
            Cargando.doSomething(new X() {
                @Override
                public void execute() {
                    actualizarDatosEmpresa();
                }
            });
        });

    }

    private void SelectImageLogo() {
        FileDialog fileDialog = new FileDialog(home, "Seleccionar logo de la empresa", FileDialog.LOAD);

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
            SelectedImageLogo = new File(directorio + nombreArchivo);
            existsLogo = true;

            try {
                ImageIcon iconoOriginal = new ImageIcon(SelectedImageLogo.getAbsolutePath());

                Tools.adaptarIconoJLabel(panel.jL_logo_infoEmpresa, iconoOriginal);

            } catch (Exception e) {
                panel.jL_logo_infoEmpresa.setIcon(iconoAddLogo);
                UtilMessage.messageError("No se pudo cargar la vista previa de la imagen: " + e.getMessage());
            }
        }

    }

    private void winCambiarContraseña() {
        panel.txt_passActual.setText("");
        panel.txt_passActual.setBackground(Color.WHITE);

        panel.txt_passNuevo.setText("");
        panel.txt_passActual.setBackground(Color.WHITE);

        panel.txt_passConfirmar.setText("");
        panel.txt_passConfirmar.setBackground(Color.WHITE);

        d_cambiarcontraseña.setVisible(true);
    }

    private void cambiarContraseña() {
        char[] passActual = panel.txt_passActual.getPassword();
        char[] passNuevo = panel.txt_passNuevo.getPassword();
        char[] passConfirmar = panel.txt_passConfirmar.getPassword();

        boolean val = true;

        try {
            if (passActual.length == 0 || passNuevo.length == 0 || passConfirmar.length == 0) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Por favor, completa todos los campos.", "Campos vacíos", javax.swing.JOptionPane.WARNING_MESSAGE);
                val = false;
                return;
            }

            if (!java.util.Arrays.equals(passNuevo, passConfirmar)) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La nueva contraseña y la confirmación no coinciden. Verifícalas.", "Error de concordancia", javax.swing.JOptionPane.ERROR_MESSAGE);
                val = false;
                return;
            }

            if (passNuevo.length < 5) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "La nueva contraseña debe tener al menos 5 caracteres.", "Contraseña muy corta", javax.swing.JOptionPane.WARNING_MESSAGE);
                val = false;
                return;
            }

            int resp = JOptionPane.showConfirmDialog(null, "¿Actualizar contraseña?","Confirmar", JOptionPane.YES_NO_OPTION);
            
            if (val && resp==JOptionPane.YES_OPTION) {
                Dto_cambiarcontraseña contraseña = new Dto_cambiarcontraseña(userAuth.getUsuario().getId(),
                        new String(passActual), new String(passNuevo));
                String jsonBody = gson.toJson(contraseña);
                RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json"));

                Request request = new Request.Builder()
                        .url(General.properties.getUrl() + "/api/usuarios/cambiarContraseña")
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
                                d_cambiarcontraseña.dispose();
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
        } finally {
            // Recuerda limpiar siempre los arreglos de la memoria RAM
            java.util.Arrays.fill(passActual, '0');
            java.util.Arrays.fill(passNuevo, '0');
            java.util.Arrays.fill(passConfirmar, '0');
        }
    }

    private void InfoEmpresa() {
        panel.txt_nombre_infoEmpresa.setBackground(Color.WHITE);
        panel.txt_nit_infoEmpresa.setBackground(Color.WHITE);
        panel.txt_telefono_infoEmpresa.setBackground(Color.WHITE);
        panel.txt_direccion_infoEmpresa.setBackground(Color.WHITE);
        panel.txt_slogan_infoEmpresa.setBackground(Color.WHITE);
        panel.jL_logo_infoEmpresa.setIcon(iconoAddLogo);
        existsLogo = false;

        obtenerDatosEmpresa(new OnDatosEmpresa() {
            @Override
            public void onSuccess(DatosEmpresaOb dto) {
                SwingUtilities.invokeLater(() -> {
                    panel.txt_nombre_infoEmpresa.setText(dto.getNombre());
                    panel.txt_nit_infoEmpresa.setText(dto.getNit());
                    panel.txt_telefono_infoEmpresa.setText(dto.getTelefono());
                    panel.txt_direccion_infoEmpresa.setText(dto.getDireccion());
                    panel.txt_slogan_infoEmpresa.setText(dto.getSlogan());
                    Tools.adaptarIconoJLabel(panel.jL_logo_infoEmpresa, dto.getLogo());

                });
            }

            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> UtilMessage.messageError(error));
            }
        });
    }

    public void obtenerDatosEmpresa(OnDatosEmpresa listener) {
        Request requestDatos = new Request.Builder()
                .url(General.properties.getUrl() + "/api/empresa/obtener")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .get()
                .build();

        client.newCall(requestDatos).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                listener.onError("Error al obtener datos: " + ioe.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (response) {
                    if (!response.isSuccessful()) {
                        listener.onError("Error en el servidor al obtener datos de la empresa.");
                        return;
                    }

                    String json = response.body().string();
                    Dto_datosEmpresa dto = gson.fromJson(json, Dto_datosEmpresa.class);

                    if (dto.getLogo() == null || dto.getLogo().isEmpty()) {
                        DatosEmpresaOb empresaLogoDefault = new DatosEmpresaOb(
                                dto.getNombre(), dto.getNit(), dto.getTelefono(),
                                dto.getDireccion(), dto.getSlogan(), iconoAddLogo
                        );

                        existsLogo=false;
                        listener.onSuccess(empresaLogoDefault);
                        return;
                    }

                    Request requestImagen = new Request.Builder()
                            .url(General.properties.getUrl() + "/api/empresa/logo/" + dto.getLogo())
                            .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                            .get()
                            .build();

                    client.newCall(requestImagen).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException ioe) {
                            DatosEmpresaOb empresaConErrorLogo = new DatosEmpresaOb(
                                    dto.getNombre(), dto.getNit(), dto.getTelefono(),
                                    dto.getDireccion(), dto.getSlogan(), iconoAddLogo
                            );
                            existsLogo=false;
                            listener.onSuccess(empresaConErrorLogo);
                            
                        }

                        @Override
                        public void onResponse(Call call, Response responseImg) throws IOException {
                            try (responseImg) {
                                ImageIcon iconoOriginal = null;
                                if (responseImg.isSuccessful() && responseImg.body() != null) {
                                    byte[] imageBytes = responseImg.body().bytes();
                                    iconoOriginal = new ImageIcon(imageBytes);
                                }

                                DatosEmpresaOb empresaCompleta = new DatosEmpresaOb(
                                        dto.getNombre(), dto.getNit(), dto.getTelefono(),
                                        dto.getDireccion(), dto.getSlogan(), iconoOriginal
                                );
                                
                                existsLogo=true;
                                listener.onSuccess(empresaCompleta);
                            }
                        }
                    });
                }
            }
        });
    }

    public void actualizarDatosEmpresa() {
        boolean val = true;
        String nombre = panel.txt_nombre_infoEmpresa.getText();
        String nit = panel.txt_nit_infoEmpresa.getText();
        String telefono = panel.txt_telefono_infoEmpresa.getText();
        String direccion = panel.txt_direccion_infoEmpresa.getText();
        String slogan = panel.txt_slogan_infoEmpresa.getText();

        if (nombre.trim().isEmpty()) {
            panel.txt_nombre_infoEmpresa.setBackground(Color.red);
            val = false;
        }
        if (nit.trim().isEmpty()) {
            panel.txt_nit_infoEmpresa.setBackground(Color.red);
            val = false;
        }
        if (telefono.trim().isEmpty()) {
            panel.txt_telefono_infoEmpresa.setBackground(Color.red);
            val = false;
        }
        if (direccion.trim().isEmpty()) {
            panel.txt_direccion_infoEmpresa.setBackground(Color.red);
            val = false;
        }
        if (slogan.trim().isEmpty()) {
            panel.txt_slogan_infoEmpresa.setBackground(Color.red);
            val = false;
        }

        if (!existsLogo) {
            val = false;
            JOptionPane.showMessageDialog(null, "¡Debes añadir tu logo!");
            return;
        }
        
        int resp = JOptionPane.showConfirmDialog(null, "¿Actualizar datos de la empresa?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if (val && resp==JOptionPane.YES_OPTION) {
            Dto_datosEmpresa empresa = new Dto_datosEmpresa(nombre, nit, telefono, direccion, slogan, "");
            String jsonBody = gson.toJson(empresa);

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("datosJson", jsonBody);

            if (SelectedImageLogo != null && SelectedImageLogo.exists()) {
                String contentType = "application/octet-stream";
                try {
                    String detectado = java.nio.file.Files.probeContentType(SelectedImageLogo.toPath());
                    if (detectado != null) {
                        contentType = detectado;
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "No se pudo determinar el Content-Type de la imagen, usando default. " + e.getMessage());
                }

                RequestBody fileBody = RequestBody.create(
                        SelectedImageLogo,
                        MediaType.parse(contentType)
                );

                multipartBuilder.addFormDataPart("logo", SelectedImageLogo.getName(), fileBody);
            }

            RequestBody body = multipartBuilder.build();

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/empresa/guardar")
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
                            InfoEmpresa();
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

}

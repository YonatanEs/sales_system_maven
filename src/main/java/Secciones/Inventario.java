package Secciones;

import Clases.Tools;
import Dto.RespuestaPaginada;
import Dto.ValorRecuest;
import Dto.ValorRequestPag;
import General.UtilMessage;
import General.userAuth;
import Objects.Cliente_ob;
import Objects.Producto_ob;
import Vistas.Home;
import Vistas.UtilPanels;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

public class Inventario {

    private Home home;
    private UtilPanels panel;
    
    private static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private int pagina_actual = 0;
    
    
    
    public Inventario(Home home, UtilPanels panel){
        this.home=home;
        this.panel=panel;
        
        Tools.buscador(home.txt_buscador_inventario, "Buscar producto..");
        
        customTable();
        
        Listeners();
        
    }
    
    
    /*---------- --- Encargados de darle funcion a los objetos con listener ----*/
    public void Listeners(){
        //boton siguiente
        home.btn_sigpag_inv.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                pagina_actual++;
                tabledates(home.txt_buscador_inventario.getText(), pagina_actual);
            }
        });
        
        home.btn_anteriorpag_inv.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if(pagina_actual>0){
                    pagina_actual--;
                    tabledates(home.txt_buscador_inventario.getText(), pagina_actual);
                }
            }
        });
        
        Tools.buscadorTablaValidate(home.txt_buscador_inventario, (consulta) -> {
            pagina_actual = 0;
            tabledates(consulta, pagina_actual);
        });
    }
    
    /*------Metodos para controlar objetos visuales y no visuales en la interfaz grafica */
    public void customTable(){
        home.tableInventario.getTableHeader().setReorderingAllowed(false);
        home.tableInventario.setRowHeight(25);
        Tools.headers(home.tableInventario);
        
        Tools.aplicarMenuEstado(home.tableInventario, false,
                (id) -> editarProducto((int) id),
                (id) -> ActoinactivarProducto((int) id));
        
    }
    
    public void editarProducto(int id){
        
    }
    
    /*---------- validacion de datos y conexion a la api---------*/
    
    public void tabledates(String busqueda, int pagina) {
        try {
            DefaultTableModel modelo = (DefaultTableModel) home.tableInventario.getModel();

            
            List<Object[]> datos = new ArrayList<Object[]>();
            //Conexion a la api
            ValorRequestPag valorRecuest = new ValorRequestPag(busqueda, pagina, 30);
            String jsonBody = gson.toJson(valorRecuest);
            RequestBody body = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/productos/ListaProductos")
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
                    
                    Type type = new TypeToken<RespuestaPaginada<Producto_ob>>(){}.getType();
                    RespuestaPaginada<Producto_ob> rs = gson.fromJson(json, type);
                    
                    SwingUtilities.invokeLater(() -> {
                        modelo.setRowCount(0);
                        for(Producto_ob producto : rs.getContent()){
                            modelo.addRow(new Object[]{
                                producto.getId(),
                                producto.getDescripcion(),
                                producto.getStock(),
                                producto.getPrecio(),
                                producto.getPrecio_por_mayor(),
                                producto.getCategoria(),
                                producto.getProveedor(),
                                producto.getEstado()
                            });
                            
                            home.btn_sigpag_inv.setEnabled(!rs.isLast());
                            home.btn_anteriorpag_inv.setEnabled(pagina>0);
                            home.jLabel_pags_inv.setText("Pagina "+pagina+1 +" de "+ rs.getTotalPages());
                        }
                        
                    });
                }
            });

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos de usuarios " + e);
        }
    }
    
    public void ActoinactivarProducto(int id){
        
    }
}

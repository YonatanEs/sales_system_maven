package Secciones;

import Vistas.Home;
import Vistas.UtilPanels;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class Ventas {

    public Home home;
    public UtilPanels panel;
    
    public Sub_nuevaventa nv;
    
    public static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    public static OkHttpClient client = new OkHttpClient();
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    public Ventas(Home home, UtilPanels panel){
        this.home=home;
        this.panel=panel;
        
        nv = new Sub_nuevaventa(this);
        
        Listeners();
    }
    
    private void Listeners(){
        home.jL_nuevaventa_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
                home.jTabbedPane1.setSelectedComponent(home.jP_nuevaventas);
                nv.initNuevaVenta();
            }
        });
        home.jL_historialventas_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
            }
        });
        home.jL_gestioncredito_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
            }
        });
        home.jL_gestiondevoluciones_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
            }
        });
    }

    
    
}

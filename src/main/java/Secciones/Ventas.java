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
    
    public sub_nuevaventa nv;
    public sub_historialventa hv;
    public sub_creditosventa cv;
    public sub_devolucionesventa dv;
    
    public static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    public static OkHttpClient client = new OkHttpClient();
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    public Ventas(Home home, UtilPanels panel){
        this.home=home;
        this.panel=panel;
        
        nv = new sub_nuevaventa(this);
        hv = new sub_historialventa(this);
        cv = new sub_creditosventa(this);
        dv = new sub_devolucionesventa(this);
        
        Listeners();
    }
    
    private void Listeners(){
        home.jL_nuevaventa_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
                home.jTabbedPane1.setSelectedComponent(home.jP_nuevaventas);
                nv.initNuevaVenta();
                nv.sugerenciasBuscadorInventario();
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

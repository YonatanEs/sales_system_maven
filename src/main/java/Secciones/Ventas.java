package Secciones;

import Vistas.Home;
import Vistas.UtilPanels;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Ventas {

    public Home home;
    public UtilPanels panel;
    
    public Ventas(Home home, UtilPanels panel){
        this.home=home;
        this.panel=panel;
        
        Listeners();
    }
    
    private void Listeners(){
        home.jL_nuevaventa_menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                home.ocultar_menu();
                home.jTabbedPane1.setSelectedComponent(home.jP_nuevaventas);
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

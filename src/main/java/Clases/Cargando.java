package Clases;

import Vistas.UtilPanels;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

public class Cargando {
    
    public static void doSomething(X x) {
        UtilPanels p = new UtilPanels();
        JDialog carga = new JDialog();
        carga.setUndecorated(true);
        carga.getContentPane().add(p.cargando);
        carga.pack();
        carga.setLocationRelativeTo(null);
        carga.setResizable(false);
        carga.setModal(true);
        SwingWorker<String, Void> worker;
        worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws InterruptedException {
                x.execute();
                return null;
            }

            @Override
            protected void done() {
                carga.dispose();
            }
        };
        worker.execute();
        carga.setVisible(true);
        try {
            worker.get();
        } catch (Exception ef) {
            ef.printStackTrace();
        }
    }
}

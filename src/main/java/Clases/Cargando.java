package Clases;

import Vistas.UtilPanels;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class Cargando {

    // ==========================================
    // INTERFACES FUNCIONALES (Nuestros Contratos)
    // ==========================================
    
    @FunctionalInterface
    public interface Accion {
        void execute() throws Exception;
    }

    @FunctionalInterface
    public interface AccionAsincrona {
        /**
         * @param onFinish Hilo/Callback que detiene la animación de carga.
         */
        void run(Runnable onFinish);
    }

    // ==========================================
    // MÉTODOS DE LA CLASE
    // ==========================================

    /**
     * Construye el JDialog de carga de forma estandarizada.
     */
    private static JDialog crearDialogoCarga() {
        UtilPanels p = new UtilPanels();
        JDialog carga = new JDialog();
        carga.setUndecorated(true);
        carga.getContentPane().add(p.cargando);
        carga.pack();
        carga.setLocationRelativeTo(null);
        carga.setResizable(false);
        carga.setModal(true); 
        return carga;
    }

    /**
     * OPCIÓN SÍNCRONA: Para tareas pesadas locales o peticiones Http usando ".execute()"
     */
    public static void doSomething(Accion accion) {
        JDialog carga = crearDialogoCarga();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                accion.execute();
                return null;
            }

            @Override
            protected void done() {
                carga.dispose(); 
                try {
                    get(); 
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, 
                        "Error en proceso síncrono: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        carga.setVisible(true);
    }

    /**
     * OPCIÓN ASÍNCRONA: Para peticiones de OkHttp que usan ".enqueue()"
     */
    public static void doAsync(AccionAsincrona accion) {
        JDialog carga = crearDialogoCarga();

        // El control remoto que apaga la pantalla de carga de forma segura en la UI
        Runnable finalizarCarga = () -> {
            SwingUtilities.invokeLater(() -> {
                if (carga.isVisible()) {
                    carga.dispose();
                }
            });
        };

        // Ejecutamos tu código pasándole el interruptor de apagado
        accion.run(finalizarCarga);

        // Abrimos el telón modal
        SwingUtilities.invokeLater(() -> carga.setVisible(true));
    }
}

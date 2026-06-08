package Clases;

import Vistas.UtilPanels;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JTextField;

public class CalculadoraBilletes {

    private UtilPanels panel;

    private List<JTextField> txt;
    private JDialog d_calculadora;
    private double total_billetes;
    private double total_monedas;
    private double total_efectivo;

    public CalculadoraBilletes() {
        panel = new UtilPanels();

        txt = new ArrayList<>();
        txt.add(panel.txt_200);
        txt.add(panel.txt_100);
        txt.add(panel.txt_50);
        txt.add(panel.txt_20);
        txt.add(panel.txt_10);
        txt.add(panel.txt_5);
        txt.add(panel.txt_1);
        txt.add(panel.txt_1m);
        txt.add(panel.txt_050m);
        txt.add(panel.txt_025m);
        txt.add(panel.txt_010m);
        txt.add(panel.txt_005m);
        txt.add(panel.txt_001m);

        d_calculadora = Tools.newWindow(panel.jP_conteoBilletes_turno);

        for (JTextField fl : txt) {
            Tools.txt_cantidad_int(fl);
        }

        for (JTextField f2 : txt) {
            f2.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    calcular();
                }
            });
        }

    }

    public void setVisible() {
        for (JTextField t : txt) {
            t.setText("0");
        }
        panel.jL_totalBilletes_turno.setText("Q. 0");
        panel.jL_totalmonedas_turno.setText("Q. 0");
        panel.jL_totalefectivo_turno.setText("Q. 0");
        d_calculadora.setVisible(true);
    }

    public void dispose() {
        d_calculadora.dispose();
    }

    private void calcular() {
        try {
            int ef200 = Integer.parseInt(panel.txt_200.getText().trim());
            int ef100 = Integer.parseInt(panel.txt_100.getText().trim());
            int ef50 = Integer.parseInt(panel.txt_50.getText().trim());
            int ef20 = Integer.parseInt(panel.txt_20.getText().trim());
            int ef10 = Integer.parseInt(panel.txt_10.getText().trim());
            int ef5 = Integer.parseInt(panel.txt_5.getText().trim());
            int ef1 = Integer.parseInt(panel.txt_1.getText().trim());
            int ef1m = Integer.parseInt(panel.txt_1m.getText().trim());
            int ef050m = Integer.parseInt(panel.txt_050m.getText().trim());
            int ef025m = Integer.parseInt(panel.txt_025m.getText().trim());
            int ef010m = Integer.parseInt(panel.txt_010m.getText().trim());
            int ef005m = Integer.parseInt(panel.txt_005m.getText().trim());
            int ef001m = Integer.parseInt(panel.txt_001m.getText().trim());

            total_billetes = (ef200 * 200) + (ef100 * 100) + (ef50 * 50) + (ef20 * 20) + (ef10 * 10) + (ef5 * 5) + (ef1 * 1);
            total_monedas = (ef1m * 1) + (ef050m * 0.50) + (ef025m * 0.25) + (ef010m * 0.10) + (ef005m * 0.05) + (ef001m * 0.01);
            total_efectivo = total_billetes + total_monedas;

            panel.jL_totalBilletes_turno.setText(String.format("Q. %.2f", total_billetes));
            panel.jL_totalmonedas_turno.setText(String.format("Q. %.2f", total_monedas));
            panel.jL_totalefectivo_turno.setText(String.format("Q. %.2f", total_efectivo));
        }catch(Exception e){
        }
    }

    public double getTotalEfectivo() {
        return total_efectivo;
    }

    public void addAction(Runnable action) {
        panel.btn_continuarCE_turno.addActionListener((e) -> {
            action.run();
        });
    }

}

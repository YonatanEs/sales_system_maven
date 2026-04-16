package Objects;

import Clases.Tools;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.text.*;

public class Precio_ob {

    private final JTextField textFiel;
    private DocumentFilter my_df;

    public Precio_ob(JTextField textfiel) {
        this.textFiel = textfiel;
        textFiel.setText("Q 0");
        Tools.clickwhite(Collections.singletonList(textFiel));
        precioFormat(this.textFiel);
        agregarFocusListener();
    }

    private void precioFormat(JTextField txt_precios) {

        my_df = new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                Document doc = fb.getDocument();
                String datos = doc.getText(0, doc.getLength());
                StringBuilder mod = new StringBuilder(doc.getText(0, doc.getLength()));
                mod.replace(offset, offset + string.length(), string);
                String comprobarDatos = mod.toString();

                if (textFiel.getCaretPosition() < 2) {
                    textFiel.setCaretPosition(2);
                }

                if (validarFormato(comprobarDatos)) {
                    double valornumerico = (datos.substring(2).trim().equals("")) ? 0
                            : Double.parseDouble(datos.substring(2));
                    if (valornumerico == 0 && !datos.substring(2).isEmpty()) {
                        if (string.equals(".") || datos.substring(2).equals("0.")) {
                            fb.insertString(offset, string, attr);
                        } else if (!string.equals("0")) {
                            fb.replace(2, 1, string, attr);
                        }
                    } else {
                        fb.insertString(offset, string, attr);
                    }
                }
            }

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Document doc = fb.getDocument();
                String datos = doc.getText(0, doc.getLength());
                StringBuilder mod = new StringBuilder(doc.getText(0, doc.getLength()));
                mod.replace(offset, offset + length, text);
                String comprobarDatos = mod.toString();

                if (textFiel.getCaretPosition() < 2) {
                    textFiel.setCaretPosition(2);
                }

                if (offset == 2 && length == datos.substring(2).length() && text.equals(".")) {
                    return;
                }

                if (validarFormato(comprobarDatos)) {
                    double valornumerico = (datos.substring(2).trim().equals("")) ? 0
                            : Double.parseDouble(datos.substring(2));
                    if (valornumerico == 0 && !datos.substring(2).isEmpty()) {
                        if (text.equals(".") || datos.substring(2).equals("0.") || datos.substring(2).equals("0.0")) {
                            fb.replace(offset, length, text, attrs);
                        } else if (!text.equals("0")) {
                            fb.replace(2, 1, text, attrs);
                        }
                    } else {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            }

            @Override
            public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
                if (offset < 2) { // Evita borrar "Q "
                    return;
                }
                String cont = textFiel.getText();
                String precio = cont.substring(2);

                if (precio.contains(".")) {
                    int pointPosition = -1;
                    int index = 0;
                    for (int j = 0; j < cont.length(); j++) {
                        if (cont.charAt(j) == '.') {
                            index = j;
                        }
                    }

                    String subPrecio = cont.substring(2, index);
                    if (subPrecio.length() == 1 && textFiel.getCaretPosition() == 3) {
                        fb.replace(offset, 1, "0", null);
                        return;
                    }
                }

                if (precio.length() == length) {
                    super.remove(fb, offset, length);
                    SwingUtilities.invokeLater(() -> {
                        try {
                            fb.insertString(offset, "0", null);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }
                super.remove(fb, offset, length);
            }
        };

        ((AbstractDocument) textFiel.getDocument()).setDocumentFilter(my_df);

    }

    private boolean validarFormato(String texto) {
        if (texto.isEmpty()) {
            return true;
        }
        return texto.matches("Q [0-9]{0,}(\\.[0-9]{0,2})?");
    }

    private void agregarFocusListener() {
        this.textFiel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!textFiel.getText().substring(2).equals("0")) {
                    setPrecio(textFiel.getText().substring(2));
                    //setPrecio("546.51");
                }
            }
        });
    }

    public double getPrecio() {
        return Double.parseDouble(textFiel.getText().substring(2));
    }

    public void setPrecio(String precio) {
        try {
            AbstractDocument doc = (AbstractDocument) textFiel.getDocument();
            double pars = 0;
            try {
                pars = Double.parseDouble(precio);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error brou" + e);
            }
            precio = String.valueOf(pars);
            int lengthReemplace = textFiel.getText().length();
            doc.replace(2, lengthReemplace - 2, precio, null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error " + e);
        }
    }

    public void setError(){
        textFiel.setBackground(Color.red);
    }
    
    public void setAprobation(){
        textFiel.setBackground(Color.green);
        setPrecio("0");
    }
    
    public void resert(){
        textFiel.setBackground(Color.WHITE);
        setPrecio("0");
    }
}

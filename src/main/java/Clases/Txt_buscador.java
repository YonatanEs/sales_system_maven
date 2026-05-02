package Clases;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public class Txt_buscador {
    private String hint;
    private JTextField textField;
    private Color colorOriginal;
    private Color colorHint = Color.GRAY;

    public Txt_buscador(String hint, JTextField textField) {
        this.hint = hint;
        this.textField = textField;
        this.colorOriginal = textField.getForeground();
        
        iniciar();
    }

    private void iniciar() {
        textField.setText(hint);
        textField.setForeground(colorHint);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(hint) && textField.getForeground().equals(colorHint)) {
                    textField.setText("");
                    textField.setForeground(colorOriginal);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(hint);
                    textField.setForeground(colorHint);
                }
            }
        });
    }

    public String getTextoReal() {
        if (textField.getText().equals(hint) && textField.getForeground().equals(colorHint)) {
            return "";
        }
        return textField.getText();
    }
}


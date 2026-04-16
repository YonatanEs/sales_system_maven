package Clases;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Abdi
 */
public class Styletable1 extends DefaultTableCellRenderer{
    
    int fila;
    
    public Styletable1(int fila){
        super();
        this.fila=fila;
    }
    
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        
        if(row == 0){
            setBackground(Color.red);
        }
        
        return comp;
    }
}

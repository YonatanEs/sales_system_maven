/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Abdi
 */
public class ElHeader extends DefaultTableCellRenderer{
    
    Color background;
    Color foreground;
    
    public ElHeader(Color background, Color foreground){
        super();
        this.background = background;
        this.foreground = foreground;
    }
    
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    
        comp.setBackground(background);
        comp.setForeground(foreground);
        comp.setFont(new Font("Tahoma", 1, 11));
        comp.setPreferredSize(new Dimension(comp.getWidth(), 25));
        //comp.setPreferredSize(new Dimension(100,50));
        //this.setBorder( BorderFactory.createLineBorder(Color.black));
        return comp;
    }
}

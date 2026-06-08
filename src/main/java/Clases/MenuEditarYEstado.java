package Clases;

import static Clases.Tools.indexColumnEstado;
import General.userAuth;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class MenuEditarYEstado {
    
    
    public MenuEditarYEstado(JTable tabla, boolean tableUser, Consumer<Object> accionEditar,
            Consumer<Object> accionCambiarEstado){
        aplicarpMenuEstado(tabla, tableUser, accionEditar, accionCambiarEstado);
    }
    
    private void aplicarpMenuEstado(JTable tabla, boolean tableUser, Consumer<Object> accionEditar,
            Consumer<Object> accionCambiarEstado) {
        Icon iconActivar = new ImageIcon(Tools.class.getResource("/Images/activar.png"));
        Icon iconInactivar = new ImageIcon(Tools.class.getResource("/Images/eliminar.png"));
        Dimension dimensionMenu = new Dimension(135, 30);

        JPopupMenu popup = new JPopupMenu();
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));
        JMenuItem itemEditar = new JMenuItem("Editar");
        itemEditar.setPreferredSize(dimensionMenu);
        itemEditar.setHorizontalAlignment(SwingConstants.LEFT);
        itemEditar.setIcon(new ImageIcon(Tools.class.getResource("/Images/Edit_icon-icons.com_71853.png")));

        JMenuItem itemEstado = new JMenuItem();
        itemEstado.setPreferredSize(dimensionMenu);
        itemEstado.setHorizontalAlignment(SwingConstants.LEFT);

        popup.add(itemEditar);
        popup.add(itemEstado);

        tabla.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                handlePopup(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handlePopup(e);
            }

            public void handlePopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tabla.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tabla.getRowCount()) {

                        tabla.setRowSelectionInterval(row, row);

                        int idValue = Integer.parseInt(tabla.getValueAt(row, 0).toString());
                        String estado = (String) tabla.getValueAt(row, indexColumnEstado(tabla));

                        if (idValue == userAuth.getUsuario().getId() && tableUser) {
                            itemEstado.setVisible(false);
                        } else {
                            itemEstado.setVisible(true);
                        }

                        boolean esActivo = "Activo".equals(estado);
                        itemEstado.setText(esActivo ? "Inactivar" : "Activar");
                        itemEstado.setIcon(esActivo ? iconInactivar : iconActivar);

                        popup.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        });

        itemEditar.addActionListener(ev
                -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                accionEditar.accept(tabla.getValueAt(row, 0));
            }
        });
        itemEstado.addActionListener(ev
                -> {
            int row = tabla.getSelectedRow();
            if (row != -1) {
                accionCambiarEstado.accept(tabla.getValueAt(row, 0));
            }
        });
    }
    
}

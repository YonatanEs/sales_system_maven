package Clases;

import General.userAuth;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.List;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Tools {

    private static Map<String, List<MouseListener>> ListenersAlmacenados = new HashMap<>();
    private static Map<JComponent, Color> coloresOriginales = new HashMap<>();

    private static BufferedImage roundedBorderImage;
    private static int cachedRadius = -1;

    public static void btn_animacion_size(JLabel label) {
        Font labelFont = label.getFont();
        int sizebase = labelFont.getSize();

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setFont(new Font("Segoe UI", 1, sizebase + 3));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setFont(new Font("segoe UI", 1, sizebase));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                label.setFont(new Font("segoe UI", 1, sizebase + 2));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                label.setFont(new Font("segoe UI", 1, sizebase + 3));
            }

        });
    }

    public static void btn_anim_enabled(JLabel label, boolean enabled) {
        if (!enabled) {
            MouseListener[] listListeners = label.getMouseListeners();
            List<MouseListener> copiaListeners = new ArrayList<>();

            for (MouseListener mouse : listListeners) {
                copiaListeners.add(mouse);
            }
            ListenersAlmacenados.put(label.getName(), copiaListeners);

            for (MouseListener mouse : listListeners) {
                label.removeMouseListener(mouse);
            }
            label.setEnabled(false);
        } else {
            List<MouseListener> listListeners = ListenersAlmacenados.get(label.getName());
            if (listListeners != null) {
                for (MouseListener mouse : listListeners) {
                    label.addMouseListener(mouse);
                }
            }
            label.setEnabled(true);
        }
    }

    public static void agregarOverlay(JLayeredPane layeredPane, JFrame frame) {
        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 0, 0, 150)); // Color negro semi-transparente
        overlay.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        overlay.setVisible(false);

        layeredPane.add(overlay, JLayeredPane.MODAL_LAYER);

        // Ejemplo de cómo mostrar el JDialog y oscurecer el fondo
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                overlay.setVisible(true);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
    }

    public static void buscador(JTextField jtextfield, String hynt) {
        jtextfield.setText(hynt);
        jtextfield.setForeground(Color.GRAY);
        jtextfield.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (jtextfield.getText().equals(hynt)) {
                    jtextfield.setText("");
                    jtextfield.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (jtextfield.getText().equals("")) {
                    jtextfield.setText(hynt);
                    jtextfield.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static void btns(JLabel label, Color color1, Color color2, Color color3) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(color2);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setBackground(color1);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                label.setBackground(color3);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                label.setBackground(color2);
            }
        });
    }

    public static void headers(JTable tabla) {
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setHeaderRenderer(
                    new ElHeader(new Color(250, 250, 250), Color.BLACK));
        }
        tabla.setBorder(null);
        tabla.getTableHeader().setReorderingAllowed(false);
    }

    public static void clickwhite(List<JTextField> list) {
        for (JTextField comp : list) {
            comp.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    comp.setBackground(Color.WHITE);
                }
            });
        }
    }

    public static void clickwhite_comp(List<JComponent> list) {
        for (JComponent comp : list) {

            if (!coloresOriginales.containsKey(comp)) {
                coloresOriginales.put(comp, comp.getBackground());
            }

            comp.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JComponent com = (JComponent) e.getSource();
                    com.setBackground(coloresOriginales.get(com));
                }
            });
        }
    }

    public static void resetOriginalColor(List<JComponent> list) {
        for (JComponent comp : list) {
            if (coloresOriginales.containsKey(comp)) {
                comp.setBackground(coloresOriginales.get(comp));
            }
        }
    }

    public static void clean(List<JTextField> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setText("");
        }
    }

    public static void paintgreen(List<JComponent> list) {
        for (JComponent comp : list) {
            comp.setBackground(Color.green);
        }
    }

    public static void vaciartabla(DefaultTableModel model) {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.removeRow(i);
            i = i - 1;
        }
    }

    public static long Diasrestantes(String inicio, String fin) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/y");

        Date fechaInicio = sdf.parse(inicio);
        Date fechaFin = sdf.parse(fin);

        long diferenciasMs = fechaFin.getTime() - fechaInicio.getTime();
        long dias = Math.floorDiv(diferenciasMs, (1000 * 60 * 60 * 24));

        return dias;
    }

    public static void addButtonEffects(JLabel label, Supplier<Boolean> menu) {
        // Efecto cuando el mouse está sobre el JLabel
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menu.get()) {
                    Tools.applyRoundedCorners(label, 25);
                    label.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!menu.get()) {
                    label.setBorder(null);
                    label.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!menu.get()) {
                    label.setBorder(null);
                    label.repaint();
                }
            }
        });
    }

    public static void applyRoundedCorners(JLabel label, int radius) {
        label.setBorder(new RoundedBorder(Color.BLACK, radius));
        label.setOpaque(false); // Esto permite pintar el fondo manualmente
        label.repaint(); // Forzar repintado para aplicar cambios
    }

    private static class RoundedBorder extends AbstractBorder {

        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.draw(new RoundRectangle2D.Double(x, y, width - 1, height - 1, radius, radius));
            g2d.dispose();
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

    }

    private static BufferedImage createRoundedBorderImage(int width, int height, int radius, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.draw(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, radius, radius));
        g2d.dispose();
        return image;
    }

    public static void diseñotabla1(JTable table) {
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if ("Activo".equals(value)) {
                    cell.setForeground(Color.GREEN);
                } else if ("Inactivo".equals(value)) {
                    cell.setForeground(Color.RED);
                } else {
                    cell.setForeground(Color.BLACK);
                }

                return cell;
            }
        });

        table.setFocusable(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        Tools.headers(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public static JDialog newWindow(JPanel panel) {
        JDialog window = new JDialog();
        window.getContentPane().add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setModal(true);
        return window;
    }

    public static void anim_icon_button(JLabel btn, Supplier<Boolean> menu) {
        Image icon = (btn.getIcon() instanceof ImageIcon) ? ((ImageIcon) btn.getIcon()).getImage() : null;

        int width = icon.getWidth(null);
        int height = icon.getHeight(null);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!menu.get()) {
                    btn.setIcon(new ImageIcon(icon.getScaledInstance(width + 5, height + 5, Image.SCALE_SMOOTH)));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!menu.get()) {
                    btn.setIcon(new ImageIcon(icon.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!menu.get()) {
                    btn.setIcon(new ImageIcon(icon.getScaledInstance(width + 3, height + 3, Image.SCALE_SMOOTH)));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }
        });

    }

    public static void showAprovationMessage(String message, String Title) {
        ImageIcon aprovado = new ImageIcon(Tools.class.getResource("/Images/bien.png"));
        JOptionPane.showMessageDialog(null, message, Title, JOptionPane.PLAIN_MESSAGE, aprovado);
    }

    public static void showMessage(String message, String Title, String ruta) {
        ImageIcon icon = new ImageIcon(Tools.class.getResource(ruta));
        JOptionPane.showMessageDialog(null, message, Title, JOptionPane.PLAIN_MESSAGE, icon);
    }

    public static void aplicarMenuEstado(JTable tabla, boolean tableUser, Consumer<Object> accionEditar,
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

                        if(idValue==userAuth.getIdUser() && tableUser){
                           itemEstado.setVisible(false);
                        }else{
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

    public static int indexColumnEstado(JTable tabla) {
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            if (tabla.getColumnName(i).equalsIgnoreCase("Estado")) {
                return i;
            }
        }
        return -1;
    }

    // Definimos una interfaz sencilla para el callback
    public interface BuscadorCallback {
        void ejecutar(String consulta);
    }

    public static void buscadorTablaValidate(JTextField textField, BuscadorCallback callback) {
        // Inicializamos la propiedad para evitar nulos
        textField.putClientProperty("ultimaBusqueda", "");

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String textoActual = textField.getText().trim();
                String ultimaBusqueda = (String) textField.getClientProperty("ultimaBusqueda");

                // 1. Caso: Presiona ENTER y hay texto
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !textoActual.isEmpty()) {
                    textField.putClientProperty("ultimaBusqueda", textoActual);
                    callback.ejecutar(textoActual);
                }
                
                // 2. Caso: Se limpia el campo (solo ejecuta una vez al quedar vacío)
                else if (textoActual.isEmpty() && !ultimaBusqueda.isEmpty()) {
                    textField.putClientProperty("ultimaBusqueda", "");
                    callback.ejecutar("");
                }
            }
        });
    }
}

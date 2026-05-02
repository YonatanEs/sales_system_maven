package Clases;

import General.userAuth;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
                if (label.isEnabled()) {
                    label.setFont(new Font("Segoe UI", 1, sizebase + 3));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (label.isEnabled()) {
                    label.setFont(new Font("segoe UI", 1, sizebase));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (label.isEnabled()) {
                    label.setFont(new Font("segoe UI", 1, sizebase + 2));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()) {
                    label.setFont(new Font("segoe UI", 1, sizebase + 3));
                }
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

    public static void focusWhite_comp(List<JComponent> list) {
        for (JComponent comp : list) {
            if (!coloresOriginales.containsKey(comp)) {
                coloresOriginales.put(comp, comp.getBackground());
            }

            comp.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    JComponent com = (JComponent) e.getSource();

                    if (coloresOriginales.containsKey(com)) {
                        com.setBackground(coloresOriginales.get(com));
                    }
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

                        if (idValue == userAuth.getIdUser() && tableUser) {
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

    public static void buscadorTablaValidate(JTextField textField, String hint, BuscadorCallback callback) {
        textField.putClientProperty("ultimaBusqueda", "");

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // Usamos el color para saber si es texto real o el hint
                boolean esHint = textField.getForeground().equals(Color.GRAY);
                String textoActual = esHint ? "" : textField.getText().trim();

                String ultimaBusqueda = (String) textField.getClientProperty("ultimaBusqueda");

                // Caso 1: Enter con texto real
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !textoActual.isEmpty()) {
                    if (!textoActual.equals(ultimaBusqueda)) {
                        textField.putClientProperty("ultimaBusqueda", textoActual);
                        callback.ejecutar(textoActual);
                    }
                } // Caso 2: El usuario borró todo (o regresó el hint)
                else if (textoActual.isEmpty() && !ultimaBusqueda.isEmpty()) {
                    textField.putClientProperty("ultimaBusqueda", "");
                    callback.ejecutar("");
                }
            }
        });
    }

    private static final Map<String, String> diccionario = new HashMap<>();

    static {
        // Masa
        diccionario.put("kilogramo", "kg");
        diccionario.put("gramo", "g");
        diccionario.put("miligramo", "mg");
        diccionario.put("libra", "lb");
        diccionario.put("onza", "oz");
        diccionario.put("tonelada", "t");
        // Longitud
        diccionario.put("metro", "m");
        diccionario.put("centimetro", "cm");
        diccionario.put("milimetro", "mm");
        diccionario.put("pulgada", "in");
        // Volumen
        diccionario.put("litro", "L");
        diccionario.put("mililitro", "mL");
        diccionario.put("galon", "gal");
        // Conteo / Logística
        diccionario.put("unidad", "und");
        diccionario.put("pieza", "pza");
        diccionario.put("paquete", "pqt");
        diccionario.put("caja", "cj");
        diccionario.put("bolsa", "bls");
        diccionario.put("frasco", "fco");
        diccionario.put("botella", "btl");
        diccionario.put("docena", "doc");
        diccionario.put("par", "par");
        diccionario.put("juego", "jgo");
        diccionario.put("rollo", "rll");
    }

    /**
     * Vincula dos JTextField para sugerir abreviaturas en tiempo real.
     *
     * @param txtOrigen Campo donde se escribe el nombre (ej. "Kilogramo")
     * @param txtDestino Campo donde aparecerá la abreviatura (ej. "kg")
     */
    public static void txt_sugerir(JTextField txtOrigen, JTextField txtDestino) {
        txtOrigen.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                procesar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                procesar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                procesar();
            }

            private void procesar() {
                SwingUtilities.invokeLater(() -> {
                    String rawText = txtOrigen.getText();

                    if (rawText.trim().isEmpty()) {
                        txtDestino.setText("");
                        return;
                    }

                    String busqueda = limpiarTexto(rawText);
                    String sugerencia = buscarEnDiccionario(busqueda);

                    if (sugerencia != null) {
                        txtDestino.setText(sugerencia);
                    }
                });
            }
        });
    }

    private static String limpiarTexto(String texto) {
        return texto.toLowerCase().trim()
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u");
    }

    private static String buscarEnDiccionario(String texto) {
        // 1. Intento de búsqueda exacta
        if (diccionario.containsKey(texto)) {
            return diccionario.get(texto);
        }

        // 2. Intento quitando el plural (si termina en 's')
        if (texto.endsWith("s")) {
            String sinPlural = texto.substring(0, texto.length() - 1);
            if (diccionario.containsKey(sinPlural)) {
                return diccionario.get(sinPlural);
            }
        }

        // 3. Intento de búsqueda parcial (si escribe "kilo..." ya sugiere "kg")
        // Solo buscamos parcialmente si el usuario ha escrito al menos 3 letras
        if (texto.length() >= 3) {
            for (Map.Entry<String, String> entry : diccionario.entrySet()) {
                if (entry.getKey().startsWith(texto)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public static void txt_precio(JTextField textField) {
        // Alineamos a la derecha para que parezca factura
        textField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formatear(textField);
            }
        });

        // Este evento captura cuando el usuario pega texto con el mouse o teclado
        textField.addPropertyChangeListener("ancestor", evt -> formatear(textField));

        // Al ganar el foco, nos aseguramos de que tenga la Q
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textField.getText().isEmpty()) {
                    textField.setText("Q 0.00");
                    textField.selectAll(); // Selecciona todo para que sea fácil borrar
                }
            }
        });
    }

    private static void formatear(JTextField txt) {
        String original = txt.getText();

        // 1. Extraemos solo lo que sea número o punto decimal
        // Esto limpia cualquier símbolo de moneda extraño o letras que se peguen
        String soloNumeros = original.replaceAll("[^\\d.]", "");

        // 2. Manejamos el caso de múltiples puntos (por si pegan algo como 10.50.20)
        if (soloNumeros.indexOf('.') != soloNumeros.lastIndexOf('.')) {
            int primerPunto = soloNumeros.indexOf('.');
            String parteEntera = soloNumeros.substring(0, primerPunto + 1);
            String parteDecimal = soloNumeros.substring(primerPunto + 1).replace(".", "");
            soloNumeros = parteEntera + parteDecimal;
        }

        // 3. Aplicamos el formato de Guatemala
        if (soloNumeros.isEmpty()) {
            txt.setText("Q ");
        } else {
            txt.setText("Q " + soloNumeros);
        }
    }

    public static void txt_cantidad(JTextField textField) {
        textField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        // Inicializamos con 0 si está vacío
        if (textField.getText().trim().isEmpty()) {
            textField.setText("0");
        }

        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formatearCantidadDecimal(textField);
            }
        });

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                // Selecciona todo para que al presionar cualquier número, el 0 desaparezca
                textField.selectAll();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                // Si al salir el usuario borró todo, ponemos el 0 de seguridad
                if (textField.getText().trim().isEmpty() || textField.getText().equals(".")) {
                    textField.setText("0");
                }
            }
        });
    }

    private static void formatearCantidadDecimal(JTextField txt) {
        String original = txt.getText();

        // 1. Extraemos números y punto
        String soloNumeros = original.replaceAll("[^\\d.]", "");

        // 2. Si el usuario borra todo, ponemos un 0 temporal
        if (soloNumeros.isEmpty()) {
            txt.setText("0");
            txt.selectAll(); // Seleccionamos para que el próximo número reemplace
            return;
        }

        // 3. Control de puntos decimales
        if (soloNumeros.contains(".")) {
            int primerPunto = soloNumeros.indexOf('.');
            String parteEntera = soloNumeros.substring(0, primerPunto + 1);
            String parteDecimal = soloNumeros.substring(primerPunto + 1).replace(".", "");

            if (parteDecimal.length() > 3) {
                parteDecimal = parteDecimal.substring(0, 3);
            }
            soloNumeros = parteEntera + parteDecimal;
        }

        // 4. ELIMINAR CEROS A LA IZQUIERDA (Evita que diga "08")
        // Pero solo si no es un decimal como "0.5"
        if (soloNumeros.length() > 1 && soloNumeros.startsWith("0") && !soloNumeros.startsWith("0.")) {
            soloNumeros = soloNumeros.substring(1);
        }

        // Para evitar bucles infinitos de eventos, solo seteamos si cambió
        if (!txt.getText().equals(soloNumeros)) {
            txt.setText(soloNumeros);
        }
    }

    public static double getPrecioLimpio(JTextField txt) {
        String texto = txt.getText().replace("Q ", "").trim();
        try {
            return texto.isEmpty() ? 0.0 : Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static String formatearStock(double valor) {
        DecimalFormat df = new DecimalFormat("#.###");
        return df.format(valor);
    }

    public static void configurarNavegacionFlechas(Container contenedor) {
        // Definimos las teclas que queremos usar para avanzar (ABAJO y TAB)
        Set<AWTKeyStroke> forwardKeys = new HashSet<>(
                contenedor.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS)
        );
        forwardKeys.add(KeyStroke.getKeyStroke("DOWN"));
        contenedor.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        // Definimos las teclas que queremos usar para retroceder (ARRIBA y SHIFT+TAB)
        Set<AWTKeyStroke> backwardKeys = new HashSet<>(
                contenedor.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS)
        );
        backwardKeys.add(KeyStroke.getKeyStroke("UP"));
        contenedor.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
    }

    public static ImageIcon escalarIcono(ImageIcon iconoOriginal, int ancho, int alto) {
        // Extraemos la imagen del icono
        Image img = iconoOriginal.getImage();

        // Redimensionamos con suavizado (SCALE_SMOOTH es clave para que no se vea pixelado)
        Image imgEscalada = img.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

        // Devolvemos el nuevo icono listo para usar
        return new ImageIcon(imgEscalada);
    }
    
    public static void buscador_sugerencias(){
        
    }
}

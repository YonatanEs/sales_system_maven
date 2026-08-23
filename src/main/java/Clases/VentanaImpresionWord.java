package Clases;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Imports oficiales de Apache PDFBox 2.0.x
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.printing.PDFPageable;

public class VentanaImpresionWord extends JDialog {

    private PDDocument documentoPdf;
    private PDFRenderer renderizador;
    private JPanel panelContenedorHoja;
    private final String rutaArchivoPdf;

    // Nuevas variables para Control de Páginas y Zoom
    private int paginaActual = 0;
    private int totalPaginas = 1;
    private BufferedImage imagenPaginaActual;
    private double zoomManual = -1.0; // -1.0 significa "Auto-Ajustar a la ventana"
    private JScrollPane scrollVistaPdf;
    private JLabel lblInfoPagina;

    public VentanaImpresionWord(Frame parent, String rutaPdf, String nombreReporte, String nombreEmpresa, ImageIcon icono) {
        super(parent, "Vista Previa de Impresión - " + nombreEmpresa, true);
        this.rutaArchivoPdf = rutaPdf;

        setSize(1150, 720);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        try {
            // Cargar el documento usando la sintaxis correcta de PDFBox 2.x
            documentoPdf = PDDocument.load(new File(rutaPdf));
            renderizador = new PDFRenderer(documentoPdf);
            totalPaginas = documentoPdf.getNumberOfPages(); // Averiguar el total de páginas
        } catch (Exception e) {
            System.err.println("Error al inicializar PDFBox: " + e.getMessage());
        }

        // -----------------------------------------------------------------
        // 1. DISEÑO DE LA BARRA LATERAL IZQUIERDA (Estilo Office)
        // -----------------------------------------------------------------
        JPanel barraLateral = new JPanel();
        barraLateral.setBackground(new Color(57, 68, 81)); // Gris azulado oscuro
        barraLateral.setPreferredSize(new Dimension(280, 0));
        barraLateral.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 18));
        barraLateral.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblLogo = new JLabel("Sistema de Ventas");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogo.setForeground(Color.WHITE);
        barraLateral.add(lblLogo);

        JLabel lblCopias = new JLabel("Copias:");
        lblCopias.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblCopias.setForeground(Color.WHITE);
        lblCopias.setPreferredSize(new Dimension(140, 30));
        barraLateral.add(lblCopias);

        JSpinner txtCopias = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        txtCopias.setPreferredSize(new Dimension(80, 30));
        barraLateral.add(txtCopias);

        JLabel lblImpresora = new JLabel("Seleccionar Impresora:");
        lblImpresora.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblImpresora.setForeground(Color.WHITE);
        lblImpresora.setPreferredSize(new Dimension(240, 20));
        barraLateral.add(lblImpresora);

        // Escaneo de las impresoras físicas instaladas en el sistema operativo
        javax.print.PrintService[] servicios = PrinterJob.lookupPrintServices();
        DefaultComboBoxModel<String> modelImpresoras = new DefaultComboBoxModel<>();
        for (javax.print.PrintService servicio : servicios) {
            modelImpresoras.addElement(servicio.getName());
        }
        JComboBox<String> cbImpresoras = new JComboBox<>(modelImpresoras);
        cbImpresoras.setPreferredSize(new Dimension(240, 35));
        barraLateral.add(cbImpresoras);

        JButton btnImprimir = new JButton("IMPRIMIR AHORA");
        btnImprimir.setFont(new Font("Segoe UI", Font.BOLD, 15));
        Tools.adaptarIconoJButton(btnImprimir, new ImageIcon(getClass().getResource("/Images/imprimir_icon.png")));
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setBackground(new Color(22, 105, 182)); // Azul vivo
        btnImprimir.setPreferredSize(new Dimension(240, 52));
        btnImprimir.setFocusPainted(false);
        btnImprimir.setBorder(BorderFactory.createLineBorder(new Color(15, 80, 140), 1));
        barraLateral.add(btnImprimir);

        JButton btnGuardarPDF = new JButton("GUARDAR PDF");
        btnGuardarPDF.setFont(new Font("Segoe UI", Font.BOLD, 15));
        Tools.adaptarIconoJButton(btnGuardarPDF, new ImageIcon(getClass().getResource("/Images/pdf_icon.png")));
        btnGuardarPDF.setForeground(Color.WHITE);
        btnGuardarPDF.setBackground(new Color(40, 167, 69)); // Verde
        btnGuardarPDF.setPreferredSize(new Dimension(240, 52));
        btnGuardarPDF.setFocusPainted(false);
        barraLateral.add(btnGuardarPDF);

        add(barraLateral, BorderLayout.WEST);

        // -----------------------------------------------------------------
        // 2. VISOR DERECHO: Barra de herramientas + Lienzo de la hoja
        // -----------------------------------------------------------------
        JPanel panelDerecho = new JPanel(new BorderLayout());

        // --- A. BARRA DE HERRAMIENTAS (Paginación y Zoom) ---
        JPanel barraHerramientas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        barraHerramientas.setBackground(new Color(240, 240, 240));

        JButton btnAnterior = new JButton("<< Anterior");
        lblInfoPagina = new JLabel("Página 1 de 1");
        lblInfoPagina.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton btnSiguiente = new JButton("Siguiente >>");

        JButton btnZoomOut = new JButton("- Zoom");
        JButton btnAjustar = new JButton("Auto Ajustar");
        JButton btnZoomIn = new JButton("+ Zoom");

        // Acciones de Paginación
        btnAnterior.addActionListener(e -> {
            if (paginaActual > 0) {
                paginaActual--;
                cargarPaginaActual();
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (paginaActual < totalPaginas - 1) {
                paginaActual++;
                cargarPaginaActual();
            }
        });

        // Acciones de Zoom
        btnZoomIn.addActionListener(e -> {
            if (zoomManual < 0) {
                zoomManual = calcularZoomAutomatico(); // Partimos del tamaño real que el usuario está viendo
            }
            zoomManual += 0.2;
            panelContenedorHoja.repaint();
        });

        btnZoomOut.addActionListener(e -> {
            if (zoomManual < 0) {
                zoomManual = calcularZoomAutomatico(); // Partimos del tamaño real que el usuario está viendo
            }
            zoomManual -= 0.2;
            if (zoomManual < 0.2) {
                zoomManual = 0.2; // Límite mínimo para que no desaparezca
            }
            panelContenedorHoja.repaint();
        });

        btnAjustar.addActionListener(e -> {
            zoomManual = -1.0;
            panelContenedorHoja.repaint();
        });

        barraHerramientas.add(btnAnterior);
        barraHerramientas.add(lblInfoPagina);
        barraHerramientas.add(btnSiguiente);
        barraHerramientas.add(new JLabel("   |   ")); // Separador visual
        barraHerramientas.add(btnZoomOut);
        barraHerramientas.add(btnAjustar);
        barraHerramientas.add(btnZoomIn);

        panelDerecho.add(barraHerramientas, BorderLayout.NORTH);

        // --- B. LIENZO DEL PDF ---
        panelContenedorHoja = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenPaginaActual == null) {
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                double factorZoomFinal = zoomManual;

                // Si es -1, calculamos el auto-ajuste
                if (zoomManual < 0) {
                    factorZoomFinal = calcularZoomAutomatico();
                }

                int anchoDibujo = (int) (imagenPaginaActual.getWidth() * factorZoomFinal);
                int altoDibujo = (int) (imagenPaginaActual.getHeight() * factorZoomFinal);

                // Actualizar ScrollPane dinámicamente
                int anchoPanel = Math.max(anchoDibujo + 40, scrollVistaPdf.getViewport().getWidth());
                int altoPanel = Math.max(altoDibujo + 40, scrollVistaPdf.getViewport().getHeight());

                Dimension curSize = getPreferredSize();
                if (curSize.width != anchoPanel || curSize.height != altoPanel) {
                    setPreferredSize(new Dimension(anchoPanel, altoPanel));
                    revalidate();
                }

                // Centrado
                int x = (anchoPanel - anchoDibujo) / 2;
                int y = (altoPanel - altoDibujo) / 2;

                // Sombra de la hoja
                g2d.setColor(new Color(80, 80, 80));
                g2d.fillRect(x + 5, y + 5, anchoDibujo, altoDibujo);

                // Estampar la hoja
                g2d.drawImage(imagenPaginaActual, x, y, anchoDibujo, altoDibujo, null);
                g2d.dispose();
            }
        };

        panelContenedorHoja.setBackground(new Color(127, 127, 127));

        // ScrollPane para habilitar desplazamiento con zoom
        scrollVistaPdf = new JScrollPane(panelContenedorHoja);
        scrollVistaPdf.setBorder(null);
        scrollVistaPdf.getVerticalScrollBar().setUnitIncrement(16);

        panelDerecho.add(scrollVistaPdf, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // 3. ACCIONES DE BOTONES ORIGINALES
        // -----------------------------------------------------------------
        btnImprimir.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Reporte_" + nombreReporte.replaceAll("\\s+", "_"));

                if (cbImpresoras.getSelectedItem() != null) {
                    String impresora = cbImpresoras.getSelectedItem().toString();
                    for (javax.print.PrintService servicio : servicios) {
                        if (servicio.getName().equals(impresora)) {
                            job.setPrintService(servicio);
                            break;
                        }
                    }
                }

                job.setCopies((int) txtCopias.getValue());
                job.setPageable(new PDFPageable(documentoPdf));

                setVisible(false);

                new Thread(() -> {
                    try {
                        job.print();
                        java.awt.EventQueue.invokeLater(() -> dispose());
                    } catch (Exception ex) {
                        System.err.println("Error durante la transferencia al spooler: " + ex.getMessage());
                        java.awt.EventQueue.invokeLater(() -> {
                            JOptionPane.showMessageDialog(null, "Error al enviar a la impresora: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }).start();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de configuración de impresión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnGuardarPDF.addActionListener(e -> guardarPdfExistente(nombreReporte));

        // Cargar primera página al terminar de construir la interfaz
        cargarPaginaActual();
    }

    // -----------------------------------------------------------------
    // MÉTODO NUEVO: CARGAR PÁGINA EN MEMORIA (Optimización)
    // -----------------------------------------------------------------
    private void cargarPaginaActual() {
        if (renderizador != null) {
            try {
                // Renderiza la hoja a buena resolución y guárdala en RAM
                imagenPaginaActual = renderizador.renderImageWithDPI(paginaActual, 180);
                if (lblInfoPagina != null) {
                    lblInfoPagina.setText("Página " + (paginaActual + 1) + " de " + totalPaginas);
                }
                // Obligar al panel a repintarse con la nueva hoja
                if (panelContenedorHoja != null) {
                    panelContenedorHoja.repaint();
                }
            } catch (Exception e) {
                System.err.println("Error al renderizar página: " + e.getMessage());
            }
        }
    }

    private double calcularZoomAutomatico() {
        if (imagenPaginaActual == null || scrollVistaPdf == null) {
            return 1.0;
        }

        double escalaX = (scrollVistaPdf.getViewport().getWidth() - 60) / (double) imagenPaginaActual.getWidth();
        double escalaY = (scrollVistaPdf.getViewport().getHeight() - 60) / (double) imagenPaginaActual.getHeight();
        double factor = Math.min(escalaX, escalaY);

        return factor > 1.0 ? 1.0 : factor;
    }

    // -----------------------------------------------------------------
    // 4. MÉTODO DISPOSE: Encargado de la recolección de basura
    // -----------------------------------------------------------------
    @Override
    public void dispose() {
        try {
            if (documentoPdf != null) {
                documentoPdf.close();
                System.out.println("Memoria de PDFBox liberada.");
            }

            if (rutaArchivoPdf != null) {
                File archivoTemporal = new File(rutaArchivoPdf);
                if (archivoTemporal.exists()) {
                    boolean borradoExitoso = archivoTemporal.delete();
                    if (borradoExitoso) {
                        System.out.println("Archivo temporal eliminado del disco duro con éxito.");
                    } else {
                        System.err.println("Java no pudo borrar el archivo (posiblemente sigue retenido).");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error crítico durante el proceso de limpieza: " + e.getMessage());
        }
        super.dispose();
    }

    private void guardarPdfExistente(String nombreReporte) {
        FileDialog fileDialog = new FileDialog(this, "Guardar " + nombreReporte, FileDialog.SAVE);
        String nombreSugerido = nombreReporte.replaceAll("[\\\\/:*?\"<>|]", "_");
        fileDialog.setFile(nombreSugerido + ".pdf");
        fileDialog.setVisible(true);

        String dir = fileDialog.getDirectory();
        String file = fileDialog.getFile();

        if (dir != null && file != null) {
            String nombreFinal = file.toLowerCase().endsWith(".pdf") ? file : file + ".pdf";
            File destino = new File(dir, nombreFinal);
            File origen = new File(rutaArchivoPdf);

            try {
                if (origen.getCanonicalPath().equals(destino.getCanonicalPath())) {
                    JOptionPane.showMessageDialog(this, "No puedes sobrescribir el archivo temporal.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                java.nio.file.Files.copy(origen.toPath(), destino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                JOptionPane.showMessageDialog(this, "PDF guardado exitosamente en:\n" + destino.getAbsolutePath());

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(destino);
                }

                this.dispose();

            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de acceso al disco: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

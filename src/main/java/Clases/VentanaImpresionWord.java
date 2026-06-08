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

    public VentanaImpresionWord(Frame parent, String rutaPdf, String nroCierre, String nombreEmpresa, ImageIcon icono) {
        super(parent, "Vista Previa de Impresión - " + nombreEmpresa, true);
        this.rutaArchivoPdf = rutaPdf;

        setSize(1150, 720);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        try {
            // Cargar el documento usando la sintaxis correcta de PDFBox 2.x
            documentoPdf = PDDocument.load(new File(rutaPdf));
            renderizador = new PDFRenderer(documentoPdf);
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

        JButton btnImprimir = new JButton("️ IMPRIMIR AHORA");
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
        btnGuardarPDF.setBackground(new Color(40, 167, 69)); // Verde para diferenciarlo del azul de imprimir
        btnGuardarPDF.setPreferredSize(new Dimension(240, 52));
        btnGuardarPDF.setFocusPainted(false);
        barraLateral.add(btnGuardarPDF);

        add(barraLateral, BorderLayout.WEST);

        // -----------------------------------------------------------------
        // 2. VISOR DERECHO: Pintar la página en el lienzo gris
        // -----------------------------------------------------------------
        panelContenedorHoja = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (renderizador == null) {
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();
                // Filtros de suavizado para que las letras no se vean pixeladas
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                try {
                    // Renderizar la primera página (índice 0) con buena resolución (150 DPI)
                    BufferedImage bImage = renderizador.renderImageWithDPI(0, 150);

                    // Cálculo matemático para el auto-zoom dinámico
                    double anchoContenedor = getWidth();
                    double altoContenedor = getHeight();
                    double escalaX = (anchoContenedor - 60) / bImage.getWidth();
                    double escalaY = (altoContenedor - 60) / bImage.getHeight();
                    double factorZoom = Math.min(escalaX, escalaY);

                    if (factorZoom > 1.0) {
                        factorZoom = 1.0;
                    }

                    // Centrar el reporte horizontal y verticalmente
                    int x = (int) ((anchoContenedor - (bImage.getWidth() * factorZoom)) / 2);
                    int y = (int) ((altoContenedor - (bImage.getHeight() * factorZoom)) / 2);

                    // Efecto visual: Dibujar la sombra de la hoja
                    g2d.setColor(new Color(80, 80, 80));
                    g2d.fillRect(x + 5, y + 5, (int) (bImage.getWidth() * factorZoom), (int) (bImage.getHeight() * factorZoom));

                    // Estampar la hoja del reporte real en el lienzo
                    g2d.drawImage(bImage, x, y, (int) (bImage.getWidth() * factorZoom), (int) (bImage.getHeight() * factorZoom), null);

                } catch (Exception ex) {
                    System.err.println("Error rendering PDF page: " + ex.getMessage());
                }
                g2d.dispose();
            }
        };

        panelContenedorHoja.setBackground(new Color(127, 127, 127)); // Gris de fondo estilo Office
        add(panelContenedorHoja, BorderLayout.CENTER);

        // -----------------------------------------------------------------
        // ACCIÓN DEL BOTÓN IMPRIMIR (Solución al PrinterAbortException)
        // -----------------------------------------------------------------
        btnImprimir.addActionListener(e -> {
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Cierre de Caja " + nroCierre);

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

                // STEP 1: Ocultamos la ventana de inmediato para que el usuario sienta que el sistema es rápido
                setVisible(false);

                // STEP 2: Ejecutamos la impresión y la limpieza en un hilo separado
                // Así evitamos congelar la interfaz y le damos tiempo al spooler de Windows de jalar el archivo
                new Thread(() -> {
                    try {
                        // Mandamos la orden al hardware (se queda esperando hasta que Windows absorba el PDF)
                        job.print();

                        // STEP 3: Una vez que job.print() termina con éxito, cerramos la ventana de forma segura
                        java.awt.EventQueue.invokeLater(() -> {
                            dispose();
                        });

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

        btnGuardarPDF.addActionListener(e -> {
            guardarPdfExistente(nroCierre);
        });
    }

    // -----------------------------------------------------------------
    // 4. MÉTODO DISPOSE: Encargado de la recolección de basura
    // -----------------------------------------------------------------
    @Override
    public void dispose() {
        try {
            // STEP 3: Cerrar el lector de Apache PDFBox para quitarle el candado de Windows
            if (documentoPdf != null) {
                documentoPdf.close();
                System.out.println("Memoria de PDFBox liberada.");
            }

            // STEP 4: Ir al disco duro y fulminar el archivo .pdf temporal de la carpeta Temp
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

        // STEP 5: Destruir oficialmente el JDialog de la pantalla
        super.dispose();
    }

    private void guardarPdfExistente(String nroCierre) {
        FileDialog fileDialog = new FileDialog(this, "Guardar Cierre de Caja", FileDialog.SAVE);
        fileDialog.setFile("Cierre_Caja_" + nroCierre + ".pdf");
        fileDialog.setVisible(true);

        String dir = fileDialog.getDirectory();
        String file = fileDialog.getFile();

        if (dir != null && file != null) {
            File destino = new File(dir + (file.endsWith(".pdf") ? file : file + ".pdf"));
            File origen = new File(rutaArchivoPdf); // Tu ruta del archivo temporal actual

            try {
                // Utilizamos java.nio.file.Files para una copia rápida y segura
                java.nio.file.Files.copy(origen.toPath(), destino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                JOptionPane.showMessageDialog(this, "PDF guardado exitosamente.");

                // Abrir con el visor nativo
                java.awt.Desktop.getDesktop().open(destino);

                // Opcional: Cerramos la vista previa
                this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage());
            }
        }
    }
}

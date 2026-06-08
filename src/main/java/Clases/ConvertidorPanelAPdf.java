package Clases;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JPanel;
public class ConvertidorPanelAPdf {

    public static boolean guardarPanelComoPdf(JPanel panelHoja, String rutaDestino) {
        // 1. Forzar al panel a calcular sus medidas reales en memoria
        panelHoja.setSize(panelHoja.getPreferredSize());
        panelHoja.doLayout();

        int ancho = panelHoja.getWidth();
        int alto = panelHoja.getHeight();

        // 2. Definir las dimensiones de la hoja del PDF basadas en tu JPanel
        Rectangle tamanoHojaPdf = new Rectangle(ancho, alto);
        Document documento = new Document(tamanoHojaPdf, 0, 0, 0, 0); // Márgenes en 0 porque el panel ya los tiene

        try {
            // 3. Crear el flujo del archivo
            PdfWriter writer = PdfWriter.getInstance(documento, new FileOutputStream(new File(rutaDestino)));
            documento.open();

            // 4. Obtener el lienzo interno del PDF (el canal de bytes gráficos)
            PdfContentByte canvas = writer.getDirectContent();
            
            // 5. Crear el puente Graphics2D que Java Swing entiende
            Graphics2D g2d = canvas.createGraphics(ancho, alto);

            // 6. Imprimir el contenido del JPanel directamente sobre el PDF
            panelHoja.printAll(g2d);

            // 7. Liberar recursos y cerrar el archivo
            g2d.dispose();
            documento.close();
            return true; // Conversión exitosa

        } catch (Exception e) {
            System.err.println("Error al transformar el JPanel a PDF: " + e.getMessage());
            return false;
        }
    }
}

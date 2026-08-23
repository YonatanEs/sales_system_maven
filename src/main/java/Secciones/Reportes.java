package Secciones;

import Clases.OnDatosEmpresa;
import Clases.OnDatosRecibo;
import Clases.VentanaImpresionWord;
import Clases.onComprobanteAbono;
import Dto.DtoAbonoComprobante;
import Dto.DtoDetallesVenta;
import Dto.DtoId;
import Dto.DtoIdVenta;
import Dto.DtoRecibo;
import General.userAuth;
import Objects.DatosEmpresaOb;
import Objects.ItemRecibo;
import Vistas.Home;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Reportes {

    private Home home;

    private static Gson gson = Converters.registerAll(new GsonBuilder()).create();
    private static OkHttpClient client = new OkHttpClient();
    private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Reportes(Home home) {
        this.home = home;
    }
    
    //metodos para general un Recibo
    public void generarRecibo(DtoIdVenta idVenta, Runnable onFinish) {
        home.configuracion.obtenerDatosEmpresa(new OnDatosEmpresa() {
            @Override
            public void onSuccess(DatosEmpresaOb empresaOb) {
                obtenerDatosRecibo(idVenta, new OnDatosRecibo() {
                    @Override
                    public void onSuccess(DtoRecibo recibo) {
                        reporteRecibo(empresaOb, recibo, onFinish);
                    }

                    @Override
                    public void onError(String error) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, error));
                        onFinish.run(); // ❌ Apagamos el cartel de carga
                    }
                });
            }

            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, error));
                onFinish.run(); // ❌ Apagamos el cartel de carga
            }
        });

    }

    public void reporteRecibo(DatosEmpresaOb datosEmpresa, DtoRecibo dtoRecibo, Runnable onFinish) {
        try {
            String infoEmpresa = "NIT: " + datosEmpresa.getNit() + " | Teléfono: " + datosEmpresa.getTelefono()
                    + " | Dirección: " + datosEmpresa.getDireccion();
            String hora = dtoRecibo.getFecha().format(DateTimeFormatter.ofPattern("hh:mm a"));
            String fecha = dtoRecibo.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            List<DtoDetallesVenta> detalles = dtoRecibo.getDetallesVenta();
            BigDecimal subtotalRP = detalles.stream()
                    .map(d -> d.getPrecio().multiply(d.getCantidad()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal descuentos = detalles.stream()
                    .map(det -> det.getDescuentos().multiply(det.getCantidad()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("iconLogo", datosEmpresa.getLogo().getImage());
            parametros.put("nombreEmpresa", datosEmpresa.getNombre());
            parametros.put("infoEmpresa", infoEmpresa);
            parametros.put("noRecibo", String.valueOf(dtoRecibo.getId()));
            parametros.put("fecha", fecha);
            parametros.put("hora", hora);
            parametros.put("vendedor", dtoRecibo.getVendedor().getNombre());
            parametros.put("nombreCliente", dtoRecibo.getNombreCliente());
            parametros.put("nitCliente", dtoRecibo.getNitCliente());
            parametros.put("subtotalRP", "Q "+subtotalRP.toPlainString());
            parametros.put("descuentosRP", "Q "+descuentos.toPlainString());
            parametros.put("totalpagar", "Q "+dtoRecibo.getTotal().setScale(2, RoundingMode.HALF_UP).toPlainString());
            parametros.put("metodoPago", dtoRecibo.getMetodoPago());

            List<ItemRecibo> contentRecibo = contentRecibo(detalles);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(contentRecibo);

            // Cargar y compilar el archivo .jrxml (Esto toma tiempo y corre seguro en el fondo)
            InputStream jasperStream = getClass().getResourceAsStream("/Reportes/Recibo_salessystem.jasper");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, parametros, ds);

            // Exportar a PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            File tempFile = File.createTempFile("reporte_", ".pdf"); // Corregido typo de .pfd a .pdf
            java.nio.file.Files.write(tempFile.toPath(), pdfBytes);

            // ⚠️ IMPORTANTE: Como estamos en un hilo secundario de OkHttp,
            // la creación y visualización de la ventana DEBE ejecutarse en el hilo de la UI
            SwingUtilities.invokeLater(() -> {
                VentanaImpresionWord vistaPrevia = new VentanaImpresionWord(
                        home, tempFile.getAbsolutePath(),
                        "Recibo_No." + dtoRecibo.getId() + "_" + dtoRecibo.getFecha()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        datosEmpresa.getNombre(),
                        datosEmpresa.getLogo());
                vistaPrevia.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Error al procesar el reporte: " + e.getMessage());
            });
        } finally {
            //  ¡Éxito o error de Jasper, el cartel se apaga aquí de forma fulminante!
            onFinish.run();
        }

    }

    public void obtenerDatosRecibo(DtoIdVenta dto, OnDatosRecibo listener) {
        String json = gson.toJson(dto);
        RequestBody body = RequestBody.create(json, JSON);

        Request requestDatos = new Request.Builder()
                .url(General.properties.getUrl() + "/api/ventas/obtenerRecibo")
                .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                .post(body)
                .build();

        client.newCall(requestDatos).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ioe) {
                listener.onError("Error al obtener datos de recibo: " + ioe.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (response) {
                    if (!response.isSuccessful()) {
                        listener.onError("Error en el servidor al obtener datos del recibo ");
                        return;
                    }

                    String json = response.body().string();
                    DtoRecibo dto = gson.fromJson(json, DtoRecibo.class);

                    listener.onSuccess(dto);
                }
            }
        });
    }

    public List<ItemRecibo> contentRecibo(List<DtoDetallesVenta> lista) {
        return lista.stream()
                .map(ItemRecibo::new)
                .collect(Collectors.toList());
    }
    
    //metodos para generar un comprobante de abono
    public void generarAbonoComprobante(DtoId idAbono, Runnable onFinish){
        home.configuracion.obtenerDatosEmpresa(new OnDatosEmpresa() {
            @Override
            public void onSuccess(DatosEmpresaOb empresaOb) {
                home.ventas.cv.obtenerDatosAbono(idAbono, new onComprobanteAbono(){
                    @Override
                    public void onSuccess(DtoAbonoComprobante dtoAbono) {
                        reporteAbono(empresaOb, dtoAbono, onFinish);
                    }

                    @Override
                    public void onError(String error) {
                        General.UtilMessage.messageError(error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                General.UtilMessage.messageError(error);
            }
        });
    }
    
    private void reporteAbono(DatosEmpresaOb empresaOb, DtoAbonoComprobante dtoAbono,
            Runnable onFinish){
        try{
            
        String infoEmpresa = "NIT: " + empresaOb.getNit() + " | Teléfono: " + empresaOb.getTelefono()
                    + " | Dirección: " + empresaOb.getDireccion();
        
        Map<String, Object> parametros = new HashMap<>();
            parametros.put("iconLogo", empresaOb.getLogo().getImage());
            parametros.put("nombreEmpresa", empresaOb.getNombre());
            parametros.put("infoEmpresa", infoEmpresa);
            parametros.put("noAbono", String.valueOf(dtoAbono.getId()));
            parametros.put("fecha", dtoAbono.getFechaAbono());
            parametros.put("hora", dtoAbono.getHoraAbono());
            parametros.put("vendedor", dtoAbono.getNombreVendedor());
            parametros.put("nombreCliente", dtoAbono.getNombreCliente());
            parametros.put("nitCliente", dtoAbono.getNitCliente());
            parametros.put("noDocumento", dtoAbono.getNoDocumento());
            parametros.put("montoTotalCredito", dtoAbono.getMontoTotalCredito());
            parametros.put("fechaAbono", dtoAbono.getFechaAbono());
            parametros.put("montoAbonado", dtoAbono.getMontoAbonado());
            parametros.put("saldoAnterior", dtoAbono.getSaldoAnterior());
            parametros.put("nuevoSaldo", dtoAbono.getNuevoSaldo());
            parametros.put("observaciones", dtoAbono.getObservaciones());
            parametros.put("totalAbonado", dtoAbono.getMontoAbonado());
            parametros.put("saldoPendiente", dtoAbono.getNuevoSaldo());
            parametros.put("estadoCredito", dtoAbono.getEstadoCredito());
            
            // Cargar y compilar el archivo .jrxml (Esto toma tiempo y corre seguro en el fondo)
            InputStream jasperStream = getClass().getResourceAsStream("/Reportes/Abono_salessystem.jasper");
            JasperPrint print = JasperFillManager.fillReport(jasperStream, parametros, new JREmptyDataSource());
            // Exportar a PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(print);

            File tempFile = File.createTempFile("reporte_", ".pdf"); // Corregido typo de .pfd a .pdf
            java.nio.file.Files.write(tempFile.toPath(), pdfBytes);
            
            SwingUtilities.invokeLater(() -> {
                VentanaImpresionWord vistaPrevia = new VentanaImpresionWord(
                        home, tempFile.getAbsolutePath(),
                        "Abono_No." + dtoAbono.getId() + "_" + dtoAbono.getFechaAbono(),
                        empresaOb.getNombre(),
                        empresaOb.getLogo());
                vistaPrevia.setVisible(true);
            });
        }catch(Exception e){
            General.UtilMessage.messageError(e.getMessage());
        }finally{
            onFinish.run();
        }
    }
    
}

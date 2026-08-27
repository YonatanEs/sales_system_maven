package Secciones;

import Clases.Cargando;
import Clases.Tools;
import Dto.DtoId;
import Dto.DtoResponseOb;
import Dto.DtoResumenDevolucion;
import Dto.Dto_infoDevoluciones;
import Dto.ItemProductosDevoluciones;
import General.UtilMessage;
import General.userAuth;
import com.google.gson.reflect.TypeToken;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class sub_devolucionesventa {

    private Ventas v;

    private JDialog d_devoluciones;
    private JDialog d_devolucionSimple;
    private JDialog d_resumentDevolucion;

    private Long idDetalleSelected = 0L;

    private Dto_infoDevoluciones ventaSelectedDev;

    private ItemProductosDevoluciones itemDevolucionSelected;

    public sub_devolucionesventa(Ventas v) {
        this.v = v;

        Tools.diseñotabla1(v.panel.tableDevoluciones_devoluciones);

        Tools.txt_cantidad(v.panel.txt_cant_devolucionsimple);

        d_devoluciones = Tools.newWindow(v.panel.jP_registrardevolucion);
        d_devolucionSimple = Tools.newWindow(v.panel.jP_devolucionSimple);
        d_resumentDevolucion = Tools.newWindow(v.panel.jP_resumenDevolucion);

        Listeners();
    }

    private void Listeners() {
        v.panel.tableDevoluciones_devoluciones.addMouseListener(new MouseAdapter() {

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
                    int row = v.panel.tableDevoluciones_devoluciones.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < v.panel.tableDevoluciones_devoluciones.getRowCount()) {

                        v.panel.tableDevoluciones_devoluciones.setRowSelectionInterval(row, row);
                        idDetalleSelected = Long.valueOf(v.panel.tableDevoluciones_devoluciones.getValueAt(row, 0).toString());

                        itemDevolucionSelected = ventaSelectedDev.getListaProductos().stream()
                                .filter(d -> Objects.equals(d.getIdProducto(), idDetalleSelected))
                                .findFirst()
                                .orElse(null);

                        v.panel.jPopupDevoluciones.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
        v.panel.jM_registrardevolucion.addActionListener(e -> {
            WinDevoluciones();
        });

        v.panel.jM_devolverProducto.addActionListener(e -> {
            WinDevolucionSimple();
        });

        v.panel.jM_devolverProductos.addActionListener(e -> {
            addAllDevolucion();
        });

        v.panel.jM_reiniciarDevolucion.addActionListener(e -> {
            reiniciarValoresDevolucion();
        });

        v.panel.btn_continuar_devolucionsimple.addActionListener(e -> {
            addDevolucionSimple();
        });

        v.panel.txt_cant_devolucionsimple.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    BigDecimal cantDevolver = new BigDecimal(v.panel.txt_cant_devolucionsimple.getText());

                    if (cantDevolver.compareTo(itemDevolucionSelected.getCantidad()) > 0) {
                        SwingUtilities.invokeLater(() -> {
                            v.panel.txt_cant_devolucionsimple.setText(
                                    itemDevolucionSelected.getCantidad()
                                            .setScale(2, RoundingMode.HALF_UP)
                                            .stripTrailingZeros()
                                            .toPlainString()
                            );
                        });
                    }
                } catch (NumberFormatException ex) {
                    // si no es número, lo limpiamos seguro
                    SwingUtilities.invokeLater(() -> v.panel.txt_cant_devolucionsimple.setText("0"));
                }
            }
        });

        v.panel.btn_registrarDevolucion_devoluciones.addActionListener(e -> {
            Cargando.doAsync((onFinish) -> {
                WinResumenDevolucion(onFinish);
            });
        });
    }

    private void WinResumenDevolucion(Runnable onFinish) {
        try {

            String jsonBody = v.gson.toJson(ventaSelectedDev);
            RequestBody body = RequestBody.create(jsonBody, v.JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/devoluciones/resumenDevolucion")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            v.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if (response.isSuccessful()) {
                        // Caso éxito → parsear el JSON a tu DTO genérico
                        DtoResumenDevolucion resumen = v.gson.fromJson(json, DtoResumenDevolucion.class);

                        SwingUtilities.invokeLater(() -> {
                            v.panel.jL_totalactualRecibo_resumenDevolucion.setText(
                                    resumen.getTotalActualRecibo());

                            v.panel.jL_metodoPago_resumendevolucion.setText(
                                    resumen.getMetodoPago());

                            v.panel.jL_totalAdevolver_resumenDevolucion.setText(
                                    resumen.getTotalDevolver());

                            v.panel.jL_metodoDevolucion_resumenDevolucion.setText(
                                    resumen.getMetodoDevolucion());
                            d_resumentDevolucion.setVisible(true);
                        });
                        onFinish.run();
                    } else {
                        // Intentar parsear igual, porque el backend puede mandar un DtoResponseOb con success=false
                        General.UtilMessage.messageError(General.Error.parseJsonError(json));
                        onFinish.run();
                    }
                }
            });

        } catch (Exception e) {
            General.UtilMessage.messageError(e.getMessage());
            onFinish.run();
        }
        //onFinish.run();

    }

    private void WinDevoluciones() {
        Cargando.doAsync((onFinish) -> {
            Long idRecibo = Long.valueOf(v.hv.idReciboSelected);
            dateWinDevoluciones(idRecibo, onFinish);
        });
        d_devoluciones.setVisible(true);
    }

    private void dateWinDevoluciones(Long idRecibo, Runnable onFinish) {
        try {
            DtoId dtoId = new DtoId(idRecibo);

            String jsonBody = v.gson.toJson(dtoId);
            RequestBody body = RequestBody.create(jsonBody, v.JSON);

            Request request = new Request.Builder()
                    .url(General.properties.getUrl() + "/api/devoluciones/infoDevolucion")
                    .addHeader("Authorization", "Bearer " + userAuth.getToken().trim())
                    .post(body)
                    .build();

            v.client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException ioe) {
                    UtilMessage.messageError("Error en la conexion " + ioe);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String json = response.body().string();
                    if (response.isSuccessful()) {
                        // Caso éxito → parsear el JSON a tu DTO genérico
                        Type type = new TypeToken<DtoResponseOb<Dto_infoDevoluciones>>() {
                        }.getType();
                        DtoResponseOb<Dto_infoDevoluciones> rs = v.gson.fromJson(json, type);

                        ventaSelectedDev = rs.getData();

                        SwingUtilities.invokeLater(() -> {
                            v.panel.jL_detallesrecibo1_devoluciones.setText(
                                    ventaSelectedDev.getDetalleRecibo1());

                            v.panel.jL_detallesrecibo2_devoluciones.setText(
                                    ventaSelectedDev.getDetalleRecibo2());

                            if (ventaSelectedDev.getEstado().equalsIgnoreCase("Activa")) {
                                v.panel.btn_historialdevoluciones.setVisible(false);
                            } else {
                                v.panel.btn_historialdevoluciones.setVisible(true);
                            }

                            ActualizarDatosTabla();

                        });
                        onFinish.run();
                    } else {
                        try {
                            // Intentar parsear igual, porque el backend puede mandar un DtoResponseOb con success=false
                            Type type = new TypeToken<DtoResponseOb<Dto_infoDevoluciones>>() {
                            }.getType();
                            DtoResponseOb<Dto_infoDevoluciones> rs = v.gson.fromJson(json, type);

                            General.UtilMessage.messageError(rs.getMessage());
                            onFinish.run();
                        } catch (Exception e) {
                            // Si no se pudo parsear, mostrar error genérico
                            General.UtilMessage.messageError(General.Error.parseJsonError(json));
                            onFinish.run();
                        }
                    }
                }
            });

        } catch (Exception e) {
            General.UtilMessage.messageError(e.getMessage());
            onFinish.run();
        }
    }

    private void ActualizarDatosTabla() {
        DefaultTableModel modelo = (DefaultTableModel) v.panel.tableDevoluciones_devoluciones.getModel();

        modelo.setRowCount(0);
        for (ItemProductosDevoluciones devolucion : ventaSelectedDev.getListaProductos()) {
            modelo.addRow(new Object[]{
                devolucion.getIdProducto(),
                devolucion.getCodigo(),
                devolucion.getDescripcion(),
                devolucion.getCantidad().setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString(),
                "Q. " + devolucion.getPrecio().setScale(2, RoundingMode.HALF_UP)
                .toPlainString(),
                devolucion.getCantidadadevolver().setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString()
            });
        }
    }

    private void WinDevolucionSimple() {
        ItemProductosDevoluciones dev = ventaSelectedDev.getListaProductos().stream()
                .filter(d -> Objects.equals(d.getIdProducto(), idDetalleSelected))
                .findFirst()
                .orElse(null);

        if (dev != null) {
            v.panel.jL_descripcion_devolucionsimple.setText(dev.getDescripcion());
            v.panel.txt_cant_devolucionsimple.setText(dev.getCantidadadevolver()
                    .setScale(2, RoundingMode.HALF_UP)
                    .stripTrailingZeros().toPlainString());
            d_devolucionSimple.setVisible(true);
        }
    }

    private void addDevolucionSimple() {
        if (itemDevolucionSelected != null) {
            BigDecimal cant = new BigDecimal(v.panel.txt_cant_devolucionsimple.getText());

            itemDevolucionSelected.setCantidadadevolver(cant);
            ActualizarDatosTabla();
            d_devolucionSimple.dispose();
        }
    }

    private void addAllDevolucion() {
        for (ItemProductosDevoluciones dev : ventaSelectedDev.getListaProductos()) {
            dev.setCantidadadevolver(dev.getCantidad());
        }
        ActualizarDatosTabla();
    }

    private void reiniciarValoresDevolucion() {
        for (ItemProductosDevoluciones dev : ventaSelectedDev.getListaProductos()) {
            dev.setCantidadadevolver(BigDecimal.ZERO);
        }
        ActualizarDatosTabla();
    }

}

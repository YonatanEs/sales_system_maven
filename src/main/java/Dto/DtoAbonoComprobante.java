package Dto;

public class DtoAbonoComprobante {

    private Long id;
    private String fechaAbono;
    private String horaAbono;
    private String nombreVendedor;
    private String nombreCliente;
    private String nitCliente;
    private String noDocumento;
    private String montoTotalCredito;
    private String montoAbonado;
    private String saldoAnterior;
    private String nuevoSaldo;
    private String observaciones;
    private String estadoCredito;

    public DtoAbonoComprobante(Long id, String fechaAbono, String horaAbono, String nombreVendedor, String nombreCliente, String nitCliente, String noDocumento, String montoTotalCredito, String montoAbonado, String saldoAnterior, String nuevoSaldo, String observaciones, String estadoCredito) {
        this.id = id;
        this.fechaAbono = fechaAbono;
        this.horaAbono = horaAbono;
        this.nombreVendedor = nombreVendedor;
        this.nombreCliente = nombreCliente;
        this.nitCliente = nitCliente;
        this.noDocumento = noDocumento;
        this.montoTotalCredito = montoTotalCredito;
        this.montoAbonado = montoAbonado;
        this.saldoAnterior = saldoAnterior;
        this.nuevoSaldo = nuevoSaldo;
        this.observaciones = observaciones;
        this.estadoCredito = estadoCredito;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFechaAbono() {
        return fechaAbono;
    }

    public void setFechaAbono(String fechaAbono) {
        this.fechaAbono = fechaAbono;
    }

    public String getHoraAbono() {
        return horaAbono;
    }

    public void setHoraAbono(String horaAbono) {
        this.horaAbono = horaAbono;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }

    public String getNoDocumento() {
        return noDocumento;
    }

    public void setNoDocumento(String noDocumento) {
        this.noDocumento = noDocumento;
    }

    public String getMontoTotalCredito() {
        return montoTotalCredito;
    }

    public void setMontoTotalCredito(String montoTotalCredito) {
        this.montoTotalCredito = montoTotalCredito;
    }

    public String getMontoAbonado() {
        return montoAbonado;
    }

    public void setMontoAbonado(String montoAbonado) {
        this.montoAbonado = montoAbonado;
    }

    public String getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(String saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public String getNuevoSaldo() {
        return nuevoSaldo;
    }

    public void setNuevoSaldo(String nuevoSaldo) {
        this.nuevoSaldo = nuevoSaldo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoCredito() {
        return estadoCredito;
    }

    public void setEstadoCredito(String estadoCredito) {
        this.estadoCredito = estadoCredito;
    }
    
    
}

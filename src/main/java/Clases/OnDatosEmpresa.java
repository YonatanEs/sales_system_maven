package Clases;

import Objects.DatosEmpresaOb;

public interface OnDatosEmpresa {
    void onSuccess(DatosEmpresaOb empresaOb);
    void onError(String error);
}

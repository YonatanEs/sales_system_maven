package Clases;

import Dto.DtoRecibo;

public interface OnDatosRecibo {
    void onSuccess(DtoRecibo recibo);
    void onError(String error);
}

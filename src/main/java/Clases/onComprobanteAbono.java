package Clases;

import Dto.DtoAbonoComprobante;

public interface onComprobanteAbono {
    void onSuccess(DtoAbonoComprobante dtoAbono);
    void onError(String error);
}

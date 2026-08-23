package Clases;

import Dto.DtoCredito;

public interface OnCredito {
    void onSuccess(DtoCredito dtoCredito);
    void onError(String error);
}

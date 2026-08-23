package Clases;

import Objects.Cliente_ob;
import Objects.DatosEmpresaOb;
import java.util.List;

public interface ListaClientesRep {
    
    void onSuccess(List<Cliente_ob> listaClientes);
    void onError(String error);

}

package General;

import Objects.Usuario_Ob;

public class userAuth {

    private static Usuario_Ob usuario;
    private static String token;
    private static int idTurnoActivo = 0;
    private static boolean isMasterCaja;
    
    public static void iniciarSesion(Usuario_Ob usuarios, String token){
        userAuth.usuario=usuarios;
        userAuth.token=token;
    }
    
    public static void validarTurno(int idTurno, boolean isMasterCaja){
        userAuth.idTurnoActivo=idTurno;
        userAuth.isMasterCaja=isMasterCaja;
    }

    public static int getIdTurnoActivo() {
        return idTurnoActivo;
    }

    public static void setIdTurnoActivo(int idTurnoActivo) {
        userAuth.idTurnoActivo = idTurnoActivo;
    }

    public static boolean isIsMasterCaja() {
        return isMasterCaja;
    }

    public static void setIsMasterCaja(boolean isMasterCaja) {
        userAuth.isMasterCaja = isMasterCaja;
    }
    
    public static Usuario_Ob getUsuario() {
        return usuario;
    }

    public static void setUsuario(Usuario_Ob usuario) {
        userAuth.usuario = usuario;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        userAuth.token = token;
    }
    
}

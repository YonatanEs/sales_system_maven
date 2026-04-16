package General;

public class userAuth {

    private static int idUser;
    private static int idCaja;
    private static String username;
    private static String roll;
    private static String token;

    public static int getIdUser() {
        return idUser;
    }

    public static void setIdUser(int idUser) {
        userAuth.idUser = idUser;
    }

    public static int getIdCaja() {
        return idCaja;
    }

    public static void setIdCaja(int idCaja) {
        userAuth.idCaja = idCaja;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        userAuth.username = username;
    }

    public static String getRoll() {
        return roll;
    }

    public static void setRoll(String roll) {
        userAuth.roll = roll;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        userAuth.token = token;
    }

    
    
    
    
}

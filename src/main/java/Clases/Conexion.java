
package Clases;
import java.sql.*;

public class Conexion {
    //Conexion Local
    public static Connection conectar(){
        try{
            int port = 7759;
            String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false","containers-us-west-69.railway.app",port,"Itsistem");
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/bd_system_one", "root", "" );//conexion a la base de datos de la ferreteria
            //Connection cn = DriverManager.getConnection(url,"root","9oQAUTZ4YD1jEJG9QOoV");//conexion a la base de datos local
            return cn;
        }catch (Exception e){
            System.out.println("Error en la conexion local" + e);
        }
        return (null);
    }
}

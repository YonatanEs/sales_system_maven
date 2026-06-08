package General;

import javax.swing.ImageIcon;

public class properties {

    private static String url = "http://localhost:5080";

    private static final ImageIcon logoSistema = new ImageIcon(
            ClassLoader.getSystemResource("Images/sistema_icon.png")
    );

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        properties.url = url;
    }

    public static ImageIcon getlogoSistema(){
        return logoSistema;
    }
}

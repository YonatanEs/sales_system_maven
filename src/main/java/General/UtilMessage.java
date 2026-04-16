package General;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class UtilMessage {
    
    public static void message(String message){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(null, message);
            }
        });
    }
    
    public static void messageAprobation(String message){
        ImageIcon checkIcon = new ImageIcon(
            UtilMessage.class.getResource("/images/bien.png"));

        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(null, message, "Message", JOptionPane.PLAIN_MESSAGE, checkIcon);
            }
        });
    }
    
    public static void messageError(String message){
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public static void messageWarning(String message){
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                JOptionPane.showMessageDialog(null, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        });
    }
}

import javax.swing.*;

public class UserGUI {
    public static void main(String [] args){
        GUI gui = new GUI();
        gui.setSize(250,220);
        gui.setResizable(false);
        gui.setLocation(500,100);
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}

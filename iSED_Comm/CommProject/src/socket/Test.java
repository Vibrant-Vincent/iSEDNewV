package socket;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {

        UIManager.put("OptionPane.minimumSize",new Dimension(500,500));
        JLabel label = new JLabel("Connection lost, Please restart server.");
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        JOptionPane.showMessageDialog(null,label,"WARNING",JOptionPane.WARNING_MESSAGE);
    }
}

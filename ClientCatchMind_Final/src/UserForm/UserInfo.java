package UserForm;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class UserInfo extends JPanel{
	
	JPanel UserInPanel(JPanel ifPanel){
	
		ifPanel = new JPanel() {
		@Override
         public void paintComponent(Graphics g) {
        		 ImageIcon img = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoom.png"));
        		 g.drawImage(img.getImage(), 0, 0,getWidth(),getHeight(), null);   
         }
	};
	
	
	return ifPanel;
	}
}

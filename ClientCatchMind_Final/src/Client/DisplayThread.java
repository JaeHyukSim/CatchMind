package Client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.net.Socket;

import javax.smartcardio.Card;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.javafx.tk.Toolkit;

import UserForm.GameRoomForm;
import UserForm.LoginFormVer1;
import UserForm.UserForm;
import UserForm.WaitingRoomForm;

public class DisplayThread extends JFrame implements Runnable{
	
	public final int WIDTH = 360;
	public final int HEIGHT = 640;
	public Cursor handCursor;
	public Cursor defaultCursor;
	
	private Socket socket;
	private volatile static DisplayThread uniqueInstance;
	private CardLayout card;
	
	private UserForm login;
	private UserForm waitingRoom;
	private UserForm game;
	//Singleton constructor(ctor)
	private DisplayThread(Socket socket) {
		
		this.socket = socket;
		//Cursor
		handCursor = new Cursor(Cursor.HAND_CURSOR);
		defaultCursor = new Cursor(Cursor.HAND_CURSOR);
		//title
		setTitle("캐치마이마인드");
		
		//position and size ( 360, 640 )
		Dimension screenSize = getToolkit().getScreenSize();
		setSize(WIDTH, HEIGHT);
		setBounds((screenSize.width - getSize().width)/2, (screenSize.height - getSize().height)/2,
				getSize().width, getSize().height);
		//layout - card
		card = new CardLayout();
		setLayout(card);
		
		
		//to construct form
		login = LoginFormVer1.getInstance(this, socket);
		/*
		 * 여기다가 아래의 주석과 같은 방법으로 추가해 주세요!! 
		 */
		waitingRoom = WaitingRoomForm.getInstance(this,socket);
		game = GameRoomForm.getInstance(this, socket);
		
		//set layout all form
		login.display();	waitingRoom.display();	game.display();
		
		//register all form to card
		getContentPane().add(login.getJPanel(), "login");
		getContentPane().add(waitingRoom.getJPanel(),"waitingRoom");
		getContentPane().add(game.getJPanel(),"gameRoom");
		
		 
		//exit
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	//Singleton Pattern
	public static DisplayThread getInstance(Socket socket) {
		if(uniqueInstance == null) {
			synchronized (DisplayThread.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new DisplayThread(socket);
				}
			}
		}
		return uniqueInstance;
	}
	
	public CardLayout getCardLayout() {
		return card;
	}
	
	public  JPanel createJPanel(String path) {
		JPanel jp = new JPanel() {
			
			@Override
	         public void paintComponent(Graphics g) {
	        	 try {
	        		 ImageIcon img = new ImageIcon(getClass().getResource(path));
	        		 g.drawImage(img.getImage(), 0, 0,getWidth(),getHeight(), null);
	                 super.paintComponent(g);
	        	 }catch(Exception e) {
	        		 System.out.println("image is not applied");
	        	 }
	         }
		};
		jp.setOpaque(false);
		return jp;
	}
	@Override
	public void run() {
		
	}
	
}

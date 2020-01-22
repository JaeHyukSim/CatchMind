
package UserForm;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;
import Client.UserServerInputThread;
import sun.rmi.runtime.Log;

public class LoginForm implements UserForm{
	
	private volatile static LoginForm uniqueInstance;
	private JPanel jpanel;
	private CardLayout card;
	
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private SignUpForm signUpForm;
	
	private final double GOLDRATE = 1.618;
	
	
	private Socket socket;
	private JPanel loginPanel;
	
	private JButton gameStartBtn;
	private JButton jb;
	private JDialog jd;
	
	private JLabel welcomeLabel;
	private JLabel lvLabel;
	private JLabel expLabel;
	private JLabel chLabel;
	
	private String id;
	private String lv;
	private String exp;
	private String ch;
	
	private UserInputThread unt;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLv() {
		return lv;
	}

	public void setLv(String lv) {
		this.lv = lv;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public JButton getJb() {
		return jb;
	}

	public void setJb(JButton jb) {
		this.jb = jb;
	}

	public JDialog getJd() {
		return jd;
	}

	public void setJd(JDialog jd) {
		this.jd = jd;
	}

	private LoginForm(DisplayThread ds, Socket socket) {
		this.socket = socket;
		unt = UserInputThread.getInstance(socket);
		userMessageProcessor = new UserMessageProcessor();
		
		this.displayThread = ds;
		//login panel operation
		String loginPanelPath = "..\\Resource\\loginPanelImage2.png";
		jpanel = displayThread.createJPanel(loginPanelPath);
		jpanel.setLayout(null);
		
		
		//gamestart panel, login input panel operation
		JPanel gamePanel = new JPanel();
		loginPanel = displayThread.createJPanel(loginPanelPath);
		
		//2 jpanel position - golden ratio
		int goldenHeight = (int)(ds.HEIGHT / GOLDRATE);
		gamePanel.setBounds(0, 0, ds.WIDTH,ds.HEIGHT-goldenHeight);
		loginPanel.setBounds(0,ds.HEIGHT-goldenHeight,ds.WIDTH,goldenHeight);
		
		//inherit - jpanel
		jpanel.add(gamePanel);
		jpanel.add(loginPanel);

		//make inherit decoration - gamePanel
		gamePanel.setLayout(null);
		gamePanel.setOpaque(false);
		try {
			ImageIcon gameStartBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_final4.png"));
			ImageIcon gameStartBtnIconRollover = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_rollover.png"));
			ImageIcon gameStartBtnIconPressed = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_pressed.png"));
			
			gameStartBtn = new JButton(gameStartBtnIcon);
			gameStartBtn.setRolloverIcon(gameStartBtnIconRollover);
			gameStartBtn.setPressedIcon(gameStartBtnIconPressed);
			gameStartBtn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent me) {
					me.getComponent().setCursor(displayThread.handCursor);
				}
				@Override
				public void mouseExited(MouseEvent me) {
					me.getComponent().setCursor(displayThread.defaultCursor);
				}
			});
			
			gameStartBtn.setBounds(gamePanel.getWidth()/12, gamePanel.getHeight()/12,
					gamePanel.getWidth() * 8 / 10, gamePanel.getHeight() * 8 / 10);
			gamePanel.add(gameStartBtn);
			
			gameStartBtn.setContentAreaFilled(false);
			gameStartBtn.setFocusPainted(false);
			gameStartBtn.setBorderPainted(false);
			
			gameStartBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					//displayThread.setBounds(300, 0, 1440,1024);
					//displayThread.getCardLayout().show(displayThread.getContentPane(), "waitingRoom");
					
					
					//JSON Data
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "2000");
					sendData += "}";
					//send to server
					unt.setInputData(sendData); //unt내부에 있는 sendData라는 변수에 값을 대입한다.
					Runnable rt = unt;
					unt.pushMessage();
				}
			});
			gameStartBtn.setEnabled(false);
		}catch(Exception e) {
			System.out.println("can't find image");
		}
		
		//make inherit decoration - loginPanel
		card = new CardLayout();
		loginPanel.setLayout(card);
		loginPanel.setOpaque(false);
		
		//input, loginOkPanel operation
		JPanel inputPanel = new JPanel();
		JPanel loginOkPanel = new JPanel();
		
		//inherit - loginPanel
		loginPanel.add(inputPanel, "inputPanel");
		loginPanel.add(loginOkPanel, "loginOkPanel");
		
		//make inherit decoration - loginPanel - inputPanel
		inputPanel.setOpaque(false);
		inputPanel.setLayout(null);
		try {
			ImageIcon loginLabelIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginFormFinal3.png"));
			
			JLabel loginLabel = new JLabel(loginLabelIcon);
			//loginLabel.setBounds(10, 10,
					//gamePanel.getWidth() * 9 / 10, gamePanel.getHeight() * 9 / 10 + 100);
			loginLabel.setBounds(12, 75, 320, 192);
			inputPanel.add(loginLabel);
			
			//textField
			JTextField textFieldId = new JTextField(15);
			JPasswordField textFieldPwd = new JPasswordField(15);
			
			textFieldId.setBounds(15, 64, 197, 33);
			textFieldPwd.setBounds(15, 104, 197, 33);
			loginLabel.add(textFieldId);
			loginLabel.add(textFieldPwd);
			
			//login btn
			ImageIcon loginBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginBtn.png"));
			ImageIcon loginBtnRollover = new ImageIcon(getClass().getResource("..\\Resource\\loginBtnRollover.png"));
			ImageIcon loginBtnPressed = new ImageIcon(getClass().getResource("..\\Resource\\loginBtnPressed.png"));
			
			JButton loginBtn = new JButton(loginBtnIcon);
			loginBtn.setRolloverIcon(loginBtnRollover);
			loginBtn.setPressedIcon(loginBtnPressed);
			
			loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			
			loginBtn.setBounds(219,64, 86, 68);
			loginLabel.add(loginBtn);
			
			loginBtn.setContentAreaFilled(false);
			loginBtn.setFocusPainted(false);
			loginBtn.setBorderPainted(false);
			
			//login btn action
			loginBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					jd = new JDialog();
					jd.setLayout(new FlowLayout());
					jd.setBounds(800, 600, 200, 100);
					jd.setModal(true);
					JLabel lb = new JLabel();
					jb = new JButton("확인");
					jd.add(lb);
					jd.add(jb);
					boolean isError = false;
					jb.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							//textFieldId.setText("");
							//textFieldPwd.setText("");
							jd.setVisible(false);
						}
					});
					for(int i = 0; i < textFieldId.getText().length(); i++) {
						char c = textFieldId.getText().charAt(i);
						if(!((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z')||(c >= '0' && c <= '9'))) {
							isError = true;
						}
					}
					if(textFieldId.getText().length() == 0) {
						lb.setText("아이디를 입력하세요!!");
						jd.setVisible(true);
					}else if(isError == true) {
						lb.setText("아이디는 대소문자와 숫자의 조합만 가능합니다!!");
						jd.setSize(300,100);
						jd.setVisible(true);
					}else if(textFieldPwd.getPassword().length == 0) {
						lb.setText("비밀번호를 입력하세요!!");
						jd.setVisible(true);
					}else {
						id = textFieldId.getText();
						//1. json data를 만든다.
						String sendData = "{";
						sendData += userMessageProcessor.getJSONData("method", "1000");
						sendData += "," + userMessageProcessor.getJSONData("id", textFieldId.getText());
						sendData += "," + userMessageProcessor.getJSONData("pwd", String.valueOf(textFieldPwd.getPassword()));
						sendData += "}";
						System.out.println(sendData);
						//2. 서버로 보낸다.
						unt.setInputData(sendData);
						Runnable userInputThread = unt;
						unt.pushMessage();
					}
				}
			});
			//sign up btn
			ImageIcon SignUpIcon = new ImageIcon(getClass().getResource("..\\Resource\\signUpBtn.png"));
			
			JButton signUpBtn = new JButton(SignUpIcon);
			
			signUpBtn.setBounds(25, 149, 60, 15);
			signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			loginLabel.add(signUpBtn);
			
			signUpBtn.setContentAreaFilled(false);
			signUpBtn.setFocusPainted(false);
			signUpBtn.setBorderPainted(false);
			
			signUpBtn.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					signUpForm = SignUpForm.getInstance(socket);
					signUpForm.setDialLogVisible();
				}
			});
			
		}catch(Exception e) {
			System.out.println("can't apply to a image");
		}
		
		//make inherit decoration - loginPanel - loginOkPanel
				loginOkPanel.setOpaque(false);
				loginOkPanel.setLayout(null);
				try {
					ImageIcon loginOkLabelIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginOkFormBack.png"));
					
					JLabel loginOkLabel = new JLabel(loginOkLabelIcon);
					loginOkLabel.setBounds(12, 75, 320, 192);
					loginOkPanel.add(loginOkLabel);
					
					welcomeLabel = new JLabel();
					JLabel lvl = new JLabel("LV");
					lvLabel = new JLabel();
					JLabel expl = new JLabel("EXP");
					expLabel = new JLabel();
					chLabel = new JLabel();
					
					welcomeLabel.setBounds(45, 46, 160, 40);
					lvl.setBounds(48, 90, 15, 10);
					lvLabel.setBounds(68, 90, 30, 10);
					expl.setBounds(110, 90, 30, 10);
					expLabel.setBounds(135, 90, 50, 10);
					chLabel.setBounds(217,34,70,100);
					
					loginOkLabel.add(welcomeLabel);
					loginOkLabel.add(lvLabel);
					loginOkLabel.add(lvl);
					loginOkLabel.add(expl);
					loginOkLabel.add(expLabel);
					loginOkLabel.add(chLabel);
					
					
					loginOkLabel.addMouseListener(new MouseListener() {
						
						@Override
						public void mouseReleased(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void mousePressed(MouseEvent e) {
							// TODO Auto-generated method stub
							System.out.println("(" + e.getX() + "," + e.getY() + ")");
						}
						
						@Override
						public void mouseExited(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
				}catch(Exception e) {
					System.out.println("LoginForm - loginOkPanel error");
				}
	}
	
	public static LoginForm getInstance(DisplayThread ds, Socket socket) {
		if(uniqueInstance == null) {
			synchronized (LoginForm.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new LoginForm(ds, socket);
				}
			}
		}
		return uniqueInstance;
	}
	
	
	@Override
	public void display() {
	}
	@Override
	public JPanel getJPanel() {
		return jpanel;
	}
	
	public void makeJDialog(String data) {
		//JDialog jd = new JDialog();
		jd.setLayout(new FlowLayout());
		jd.setBounds(800, 600, 200, 100);
		jd.setModal(true);
		JLabel lb = new JLabel(data);
		//JButton jb = new JButton("확인");
		jd.add(lb);
		jd.add(jb);
		jd.setVisible(true);
		
	}
	
	public void swapLogin() {
		card.show(loginPanel,"loginOkPanel");
		gameStartBtn.setEnabled(true);
		labelTextChange(welcomeLabel, "\"" + id + "\"" + "님 환영합니다!");
		labelTextChange(lvLabel, lv);
		labelTextChange(expLabel, exp);
		labelTextChange(chLabel, ch);
	}
	
	public void labelTextChange(JLabel l, String d) {
		l.setText(d);
	}

	@Override
	public void actionPerformMethod() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void operation(String data) {
		// TODO Auto-generated method stub
		
	}
}

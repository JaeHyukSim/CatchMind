package UserForm;

import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class LoginFormVer1 implements UserForm{
	
	///***** 모르는 것 - 질문하셔도 좋고, 제가 만든 LoginFormVer1을 참고하셔도 됩니다! - 똑같은 형태로 적용했습니다!

	//1. UserForm 인터페이스를 implements합니다!!!!!
	//2. Override 함수들을 만들어서 에러를 없애줍니다!!!!
	//3. Singleton Pattern을 만들기 위해 uniqueInstance 변수를 만들어 줍니다!!!!!
	private volatile static LoginFormVer1 uniqueInstance;
	//4. 필수 변수들을 선언해 줍니다!!!!!
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private Socket socket;
	private UserInputThread unt;
	private JSONParser jsonParser;
	
	
	//5. 사용할 JFrame container의 components들과 Thread를 선언해 줍니다!!!!
	private final double GOLDRATE = 1.618;
	
	private JPanel jpanel;
	private CardLayout card;
	private SignUpForm signUpForm;
	private JPanel loginPanel;
	
	private JButton gameStartBtn;
	private JButton jb;
	private JDialog jd;
	
	private JLabel welcomeLabel;
	private JLabel lvLabel;
	private JLabel expLabel;
	private JLabel chLabel;
	
	private JPanel gamePanel;

	private ImageIcon gameStartBtnIcon;
	private ImageIcon gameStartBtnIconRollover;
	private ImageIcon gameStartBtnIconPressed;
	
	private JPanel inputPanel;
	private JPanel loginOkPanel;
	
	private ImageIcon loginLabelIcon;
	
	private JLabel loginLabel;
	private JTextField textFieldId;
	private JPasswordField textFieldPwd;
	
	private ImageIcon loginBtnIcon;
	private ImageIcon loginBtnRollover;
	private ImageIcon loginBtnPressed;
	
	private JButton loginBtn;
	
	private ImageIcon SignUpIcon;
	
	private JButton signUpBtn;
	
	private ImageIcon loginOkLabelIcon;
	
	private JLabel loginOkLabel;
	private JLabel lvl;
	private JLabel expl;
	
	private String id;
	private String lv;
	private String exp;
	private String ch;
	
	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!
	private Runnable userRunnable;
	
	private JButton changeInfo;
	private JButton withdrawal;
	private JButton logout;
	
	private ImageIcon[] chImage;
	
	//6. 생성자를 만듭니다!!! - private인 것을 주의합시다!!!
	private LoginFormVer1(DisplayThread dt, Socket socket) {
		//new를 사용해서 Thread와 component 생성
		//7. 여기서 모든 components들을 new를 통해 생성합니다. + 필수적인 변수초기화를 진행합니다.
		this.displayThread = dt;
		this.socket = socket;
		userMessageProcessor = new UserMessageProcessor();
		unt = UserInputThread.getInstance(socket);
		jsonParser = new JSONParser();
		
		
		//components들 new 시작!!
		signUpForm = SignUpForm.getInstance(socket);
		String loginPanelPath = "..\\Resource\\loginPanelImage2.png";
		jpanel = displayThread.createJPanel(loginPanelPath);

		gamePanel = new JPanel();
		loginPanel = displayThread.createJPanel(loginPanelPath);

		gameStartBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_final4.png"));
		gameStartBtnIconRollover = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_rollover.png"));
		gameStartBtnIconPressed = new ImageIcon(getClass().getResource("..\\Resource\\gameStartButton_pressed.png"));

		gameStartBtn = new JButton(gameStartBtnIcon);
		
		card = new CardLayout();

		inputPanel = new JPanel();
		loginOkPanel = new JPanel();

		loginLabelIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginFormFinal3.png"));
		loginLabel = new JLabel(loginLabelIcon);

		textFieldId = new JTextField(15);
		textFieldPwd = new JPasswordField(15);

		loginBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginBtn.png"));
		loginBtnRollover = new ImageIcon(getClass().getResource("..\\Resource\\loginBtnRollover.png"));
		loginBtnPressed = new ImageIcon(getClass().getResource("..\\Resource\\loginBtnPressed.png"));

		loginBtn = new JButton(loginBtnIcon);

		SignUpIcon = new ImageIcon(getClass().getResource("..\\Resource\\signUpBtn.png"));
		signUpBtn = new JButton(SignUpIcon);

		loginOkLabelIcon = new ImageIcon(getClass().getResource("..\\Resource\\loginOkFormBack.png"));
		loginOkLabel = new JLabel(loginOkLabelIcon);

		welcomeLabel = new JLabel();
		lvl = new JLabel("LV");
		lvLabel = new JLabel();
		expl = new JLabel("EXP");
		expLabel = new JLabel();
		chLabel = new JLabel();
		
		changeInfo = new JButton("정보 변경");
		withdrawal = new JButton("회원 탈퇴");
		logout = new JButton("로그아웃");
		
		chImage = new ImageIcon[4];
		
		for(int i = 0; i < chImage.length; i++) {
			chImage[i] = new ImageIcon(getClass().getResource("..\\Resource\\ch" + (i+1) + ".png"));
		}
		
		//8.  다음과 같이 Thread를 만들어 줍니다
		userRunnable = unt;
		// 나중에 userThread.start(); 라는 메소드를 통해서 쓰레드를 실행해 줍니다 ---- 메시지를 서버에 보낼 ㄸ!!!!
		
		actionPerformMethod();
	}
	
	//9. Singleton pattern의 유일한 instance를 만들기 위해 getInstance()메소드를 만듭니다.
	public static LoginFormVer1 getInstance(DisplayThread dt, Socket socket) {
		if(uniqueInstance == null) {
			synchronized (LoginFormVer1.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new LoginFormVer1(dt, socket);
				}
			}
		}
		return uniqueInstance;
	}
	@Override
	public void display() {
		//10. 여기서 생성된 JFrame components들의 배치를 시작합니다! 
		jpanel.setLayout(null);

		int goldenHeight = (int)(displayThread.HEIGHT / GOLDRATE);
		gamePanel.setBounds(0, 0, displayThread.WIDTH,displayThread.HEIGHT-goldenHeight);
		loginPanel.setBounds(0,displayThread.HEIGHT-goldenHeight,displayThread.WIDTH,goldenHeight);

		gamePanel.setLayout(null);
		gamePanel.setOpaque(false);

		gameStartBtn.setRolloverIcon(gameStartBtnIconRollover);
		gameStartBtn.setPressedIcon(gameStartBtnIconPressed);

		gameStartBtn.setBounds(gamePanel.getWidth()/12, gamePanel.getHeight()/12,
		gamePanel.getWidth() * 8 / 10, gamePanel.getHeight() * 8 / 10);
		gameStartBtn.setContentAreaFilled(false);
		gameStartBtn.setFocusPainted(false);
		gameStartBtn.setBorderPainted(false);
		gameStartBtn.setEnabled(false);

		loginPanel.setLayout(card);
		loginPanel.setOpaque(false);

		loginLabel.setBounds(12, 75, 320, 192);

		inputPanel.setOpaque(false);
		inputPanel.setLayout(null);
		
		textFieldId.setBounds(15, 64, 197, 33);
		textFieldPwd.setBounds(15, 104, 197, 33);

		loginBtn.setRolloverIcon(loginBtnRollover);
		loginBtn.setPressedIcon(loginBtnPressed);

		loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		loginBtn.setBounds(219,64, 86, 68);

		loginBtn.setContentAreaFilled(false);
		loginBtn.setFocusPainted(false);
		loginBtn.setBorderPainted(false);

		signUpBtn.setBounds(25, 149, 60, 15);
		signUpBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

		signUpBtn.setContentAreaFilled(false);
		signUpBtn.setFocusPainted(false);
		signUpBtn.setBorderPainted(false);

		loginOkPanel.setOpaque(false);
		loginOkPanel.setLayout(null);

		loginOkLabel.setBounds(12, 75, 320, 192);

		welcomeLabel.setBounds(45, 46, 160, 40);
		lvl.setBounds(48, 90, 15, 10);
		lvLabel.setBounds(68, 90, 30, 10);
		expl.setBounds(110, 90, 30, 10);
		expLabel.setBounds(135, 90, 50, 10);
		chLabel.setBounds(217,34,70,100);
		
		changeInfo.setBounds(20, 120, 90, 25);
		withdrawal.setBounds(115, 120, 90, 25);
		logout.setBounds(210, 120, 90, 25);
		
		jpanel.add(gamePanel);
		jpanel.add(loginPanel);
		
		gamePanel.add(gameStartBtn);
		
		loginPanel.add(inputPanel, "inputPanel");
		loginPanel.add(loginOkPanel, "loginOkPanel");
		
		loginOkPanel.add(loginOkLabel);
		
		inputPanel.add(loginLabel);
		
		loginLabel.add(textFieldId);
		loginLabel.add(textFieldPwd);
		loginLabel.add(loginBtn);
		loginLabel.add(signUpBtn);
		
		loginOkLabel.add(welcomeLabel);
		loginOkLabel.add(lvLabel);
		loginOkLabel.add(lvl);
		loginOkLabel.add(expl);
		loginOkLabel.add(expLabel);
		loginOkLabel.add(chLabel);
		
		loginOkLabel.add(withdrawal);
		loginOkLabel.add(logout);
	}

	@Override
	public void actionPerformMethod() {
		//11. addActionListener 함수, 또는 익명의 클래스를 이곳에서 사용합니다!
		//이 메소드로 캡슐화하는 이유는 서버로 보낼 메시지를 한 메소드로 모아서 가독성을 높이기 위함입니다!!
		//익명의 클래스 사용 예시 ----------------------(강사님께서 알려주신 방법으로 사용해도 무방합니다 - implements Action...)
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
		
		gameStartBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//JSON Data
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "2000");
				sendData += "}";
				//send to server
				//13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		});
		
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
					textFieldId.requestFocus();
				}else if(isError == true) {
					lb.setText("아이디는 대소문자와 숫자의 조합만 가능합니다!!");
					jd.setSize(300,100);
					jd.setVisible(true);
					textFieldId.requestFocus();
				}else if(textFieldPwd.getPassword().length == 0) {
					lb.setText("비밀번호를 입력하세요!!");
					jd.setVisible(true);
					textFieldPwd.requestFocus();
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
					//13. 데이터를 서버로 보냅니다!
					unt.setInputData(sendData);
					unt.pushMessage();
				}
			}
		});
		
		signUpBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				signUpForm = SignUpForm.getInstance(socket);
				signUpForm.dialogClear();
				signUpForm.setDialLogVisible();
			}
		});
		
		logout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//서버로 보내봅시다. 
				//1. 메소드를 정해봅시다. 2100 -> 2102
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "1400"); // logout request 
				sendData += "}";
				
				//13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		});
		
		withdrawal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int sel = JOptionPane.showConfirmDialog(jpanel, "정보를 삭제하시겠습니까?", "삭제하기", JOptionPane.YES_NO_OPTION);
				if(sel == JOptionPane.YES_OPTION) {
					JSONObject json = new JSONObject();
					json.put("method", "1500");
					
					//13. 데이터를 서버로 보냅니다!
					unt.setInputData(String.valueOf(json));
					unt.pushMessage();
				}
			}
		});
	}

	@Override
	public void operation(String data) {
	//14. 서버로부터 받은 데이터를 처리하는 곳입니다.
	//만약 서버로부터 method가 1002, btnName이 "바뀐 버튼 이름"으로 데이터가 왔을 때!!! - 버튼의 text를 "바뀐 버튼 이름"으로 바꿔봅시다
	try {
		System.out.println("data : " + data);
		JSONObject jsonObj = (JSONObject)jsonParser.parse(data);
		switch((String)jsonObj.get("method")) {
		case "1004":	//id - error
			makeJDialog("아이디가 틀렸습니다.");
			
			jb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					jd.setVisible(false);
				}
			});
			textFieldId.requestFocus();
			break;
		case "1014": // pwd - error
			makeJDialog("비밀번호가 틀렸습니다.");
			jb.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					jd.setVisible(false);
				}
			});
			textFieldPwd.requestFocus();
			break;
		case "1002": // login okn 
			id = (String)jsonObj.get("id");
			lv = (String)jsonObj.get("lv");
			exp = (String)jsonObj.get("exp");
			ch = (String)jsonObj.get("ch");
			unt.setId(id);
			unt.setLv(lv);
			unt.setExp(exp);
			unt.setCh(ch);
			swapLogin();
			break;
		case "1104": //id check is failed
			System.out.println("1104!");
			signUpForm.setIsCheck(false);
			break;
		case "1102": // id check is verified
			signUpForm.setIsCheck(true);
			break;
		case "1202": //sign up OK
			signUpForm.getDialog("회원가입 성공!", 250, 100);
			signUpForm.setDialLogInvisible();
			signUpForm.dialogClear();
			break;
		case "1204": //회원가입 실패
			signUpForm.getDialog("회원가입에 실패했습니다.", 250, 100);
			signUpForm.dialogClear();
			break;
		case "1024": //login - already login : fail
			signUpForm.getDialog("이미 로그인 중입니다.", 250, 100);
			break;
		case "1302": // init login form
			init();
			break;
		case "1312": // card show change to login
			displayThread.setSize(displayThread.WIDTH, displayThread.HEIGHT);
			displayThread.setLocation(780, 220);
			displayThread.getCardLayout().show(displayThread.getContentPane(), "login");
			break;
		case "1402": //logout request is accepted
			card.show(loginPanel, "inputPanel");
			init();
			gameStartBtn.setEnabled(false);
			break;
		case "1502": // remove ok
			
			JOptionPane.showMessageDialog(jpanel, "삭제되었습니다. BYE~");
			card.show(loginPanel, "inputPanel");
			init();
			gameStartBtn.setEnabled(false);
			break;
		//... 수많은 case들
		}
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
		//labelTextChange(chLabel, ch);
		chLabel.setIcon(chImage[Integer.parseInt(ch)]);
	}
	
	public void labelTextChange(JLabel l, String d) {
		l.setText(d);
	}
	public void init() {
		textFieldId.setText("");
		textFieldPwd.setText("");
		textFieldId.requestFocus();
	}
}

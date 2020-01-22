package UserForm;

/*
 * hello
 */
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Client.UserInputThread;
import Client.UserMessageProcessor;

public class SignUpForm implements UserForm{

	private volatile static SignUpForm uniqueInstance;
	
	private UserMessageProcessor userMessageProcessor;
	private Socket socket;
	private UserInputThread unt;
	
	private JButton jb;
	private JLabel jl;
	private JDialog jd;
	private JDialog jdialog;
	private JLabel idLabel;
	private JLabel pwdLabel;
	private JLabel pwdOkLabel;
	private JTextField idTF;
	private JPasswordField pwdTF;
	private JPasswordField pwdOkTF;
	private JButton isExist;
	private JButton check;
	private JButton cencel;
	
	private String inputedId;
	private boolean isChecked;
	private String currentId;
	
	private JRadioButton[] character;
	private ButtonGroup chGroup;
	
	private JLabel[] chLabel;
	private ImageIcon[] chIcon;
	
	private SignUpForm(Socket socket) {
		this.socket = socket;
		unt = UserInputThread.getInstance(socket);
		userMessageProcessor = new UserMessageProcessor();
		
		jd = new JDialog();
		jl = new JLabel();
		jb = new JButton();
		jd.setLayout(new FlowLayout());
		jd.setModal(true);
		
		inputedId = "";
		isChecked = false;
		
		jdialog = new JDialog();
		idLabel = new JLabel("아이디:");
		pwdLabel = new JLabel("비밀번호:");
		pwdOkLabel = new JLabel("비번확인:");
		idTF = new JTextField();
		pwdTF = new JPasswordField();
		pwdOkTF = new JPasswordField();
		isExist = new JButton("중복체크");
		check = new JButton("확인");
		cencel = new JButton("취소");
		
		String[] chRadioName = {"빠냉이","두김이","무냉이","먹밥이"};
		character = new JRadioButton[4];
		chGroup = new ButtonGroup();
		chIcon = new ImageIcon[4];
		chLabel = new JLabel[4];
		
		jdialog.setLayout(null);
		jdialog.setModal(true);
		jdialog.setTitle("회원가입");
		
		jdialog.setSize(350,700);
		jdialog.setLocation(600, 300);
		idLabel.setBounds(30, 40, 50, 30);
		pwdLabel.setBounds(30, 110, 70, 30);
		pwdOkLabel.setBounds(30, 180, 70, 30);
		idTF.setBounds(110, 40, 100, 30);
		pwdTF.setBounds(110, 110, 100, 30);
		pwdOkTF.setBounds(110, 180, 100, 30);
		isExist.setBounds(220, 40, 100, 30);
		check.setBounds(130, 600, 80, 30);
		cencel.setBounds(220, 600, 80, 30);
		jd.setBounds(800, 700, 0, 0);
		
		for(int i = 0; i < character.length; i++) {
			character[i] = new JRadioButton(chRadioName[i]);
			chGroup.add(character[i]);
			jdialog.add(character[i]);
			
			character[i].setBounds(30, 250+i*80, 100, 60);
			
			chIcon[i] = new ImageIcon(getClass().getResource("..\\Resource\\ch"+(i+1)+".png"));
			chLabel[i] = new JLabel(chIcon[i]);
			jdialog.add(chLabel[i]);
			
			chLabel[i].setBounds(100, 250+i*80, 120, 60);
			
		}
		character[0].setSelected(true);
		
		
		jdialog.add(idLabel);
		jdialog.add(pwdLabel);
		jdialog.add(pwdOkLabel);
		jdialog.add(idTF);
		jdialog.add(pwdTF);
		jdialog.add(pwdOkTF);
		jdialog.add(isExist);
		jdialog.add(check);
		jdialog.add(cencel);
		
		jd.add(jl);
		jd.add(jb);
		
		jb.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				jd.setVisible(false);
			}
		});
		
		isExist.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean isGoodId = true;
				for(int i = 0; i < idTF.getText().length(); i++) {
					char s = idTF.getText().charAt(i);
					if(!((s >= 'a' && s <= 'z') ||(s >= 'A' && s <= 'Z') || (s >= '0' && s <= '9'))) {
						isGoodId = false; break;
					}
				}
				if(idTF.getText().equals("")) {
					//1. dialog 생성
					getDialog("아이디를 입력하세요", 150, 100);
				} else if(isGoodId == false) {
					getDialog("아이디는 대소문자와 숫자의 조합으로만 가능합니다.", 350, 100);
				}else {
					System.out.println("method : 1100");
					currentId = idTF.getText();
					//1. json data를 만든다.
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "1100");
					sendData += "," + userMessageProcessor.getJSONData("id", idTF.getText());
					sendData += "}";
					//2. 서버로 보낸다.
					unt.setInputData(sendData);
					Runnable userInputThread = unt;
					unt.pushMessage();
				}
				
			}
		});
		
		cencel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDialLogInvisible();
			}
		});
		check.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("current ID : " + idTF.getText() + ", inputedID : " + inputedId + ", isChecked : " + isChecked);
				if(idTF.getText().equals("")) {
					getDialog("아이디를 입력하세요",250,100);
					idTF.requestFocus();
				}else if(inputedId.equals("") || !(inputedId.equals(idTF.getText()) && isChecked == true)) {
					getDialog("아이디 중복체크를 하세요", 250, 100);
					idTF.requestFocus();
				}else if(String.valueOf(pwdTF.getPassword()).equals("") || String.valueOf(pwdOkTF.getPassword()).equals("")) {
					getDialog("패스워드를 입력하세요!", 250, 100);
					pwdTF.requestFocus();
				}else if(!String.valueOf(pwdTF.getPassword()).equals(String.valueOf(pwdOkTF.getPassword()))){
					getDialog("패스워드가 다릅니다", 250, 100);
					pwdTF.requestFocus();
				}else {
					String selectedCharacter = findChLabel();
					System.out.println("selectedCharacter : " + selectedCharacter);
					System.out.println("selectedCharacter : " + selectedCharacter);
					//1. json data를 만든다.
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "1200");
					sendData += "," + userMessageProcessor.getJSONData("id", idTF.getText());
					sendData += "," + userMessageProcessor.getJSONData("pwd", String.valueOf(pwdTF.getPassword()));
					sendData += "," + userMessageProcessor.getJSONData("ch", selectedCharacter);
					sendData += "}";
					//2. 서버로 보낸다.
					unt.setInputData(sendData);
					Runnable userInputThread = unt;
					unt.pushMessage();
				}
			}
		});
	}
	public static SignUpForm getInstance(Socket socket) {
		if(uniqueInstance == null) {
			synchronized (SignUpForm.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new SignUpForm(socket);
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
		// TODO Auto-generated method stub
		return null;
	}
	public void setDialLogVisible() {
		jdialog.setVisible(true);
	}
	public void setDialLogInvisible() {
		jdialog.setVisible(false);
	}
	public void getDialog(String s,int width, int height) {
		//JDialog jd = new JDialog();
		//jd.setLayout(new FlowLayout());
		//jd.setModal(true);
		
		//JLabel jl = new JLabel(s);
		//JButton jb = new JButton("확인");
		jl.setText(s);
		jb.setText("확인");
		
		jd.setSize(width, height);
		jd.setVisible(true);
	}
	public void setIsCheck(boolean value) {
		isChecked = value;
		System.out.println("inside setIsCheck");
		if(value == true) {
			inputedId = currentId;
			getDialog("사용할 수 있는 아이디 입니다!", 250, 100);
			idTF.requestFocus();
			
		} else {
			getDialog("사용할 수 없는 아이디 입니다!", 250, 100);
			idTF.requestFocus();
		}
	}
	
	/*
	 * private JTextField idTF; private JPasswordField pwdTF; private JPasswordField
	 * pwdOkTF;
	 */
	public void dialogClear() {
		inputedId = "";
		idTF.setText(""); pwdTF.setText(""); pwdOkTF.setText("");
	}
	@Override
	public void actionPerformMethod() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void operation(String data) {
		// TODO Auto-generated method stub
		
	}
	
	public String findChLabel() {
		int res = 0;
		for(int i = 0; i < character.length; i++) {
			if(character[i].isSelected()) {
				res = i;
				break;
			}
		}
		return String.valueOf(res);
	}
}
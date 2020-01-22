package UserForm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class GameRoomForm extends JPanel implements UserForm {

	/// ***** 모르는 것 - 질문하셔도 좋고, 제가 만든 LoginFormVer1을 참고하셔도 됩니다! - 똑같은 형태로 적용했습니다!

	// 1. UserForm 인터페이스를 implements합니다!!!!!
	// 2. Override 함수들을 만들어서 에러를 없애줍니다!!!!
	// 3. Singleton Pattern을 만들기 위해 uniqueInstance 변수를 만들어 줍니다!!!!!
	private volatile static GameRoomForm uniqueInstance;
	// 4. 필수 변수들을 선언해 줍니다!!!!!
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private Socket socket;
	private UserInputThread unt;
	private JSONParser jsonParser;
	// 5. 사용할 JFrame container의 components들과 Thread를 선언해 줍니다!!!!

	private Runnable userRunnable;
	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!

	private Client.Queue que;

	////////////////////////////////////
	////////////////////////////////////
	private InviteDialog inviteDialog;

	private JSONObject json;

	private TimeBar timeBar;
	// 채팅 표시할 텍스트 필드 선언
	public UserChat[] userChat = new UserChat[6];
	// 채팅 표시 위한 패널별 유저 넘버. 임시로 0.
	int userNum;
	int myUserNum;

	JPanel grSketch = new JPanel();
	// GrSketch sketchPanel = new GrSketch();
	GrSketch sketchPanel = GrSketch.getInstance(displayThread, socket);
	JPanel[] userPanel = new JPanel[6];
	JPanel timer = new JPanel();
	JPanel answer = new JPanel();
	JLabel answerLabel = new JLabel();
	JPanel round = new JPanel();
	JLabel roundLabel = new JLabel();
	JPanel turn = new JPanel();
	JButton[] b = new JButton[4];
	JPanel p = new JPanel();
	JLabel[] label = new JLabel[24];
	// 라운드표시기
	Font font_round = new Font("굴림", Font.BOLD, 60);
	// 정답표시기
	Font font_answer = new Font("굴림", Font.BOLD, 30);
	// 개인정보창
	Font font_player = new Font("굴림", Font.BOLD, 15);
	Font font_Title = new Font("굴림", Font.BOLD, 30);
	JTextField tf = new JTextField();
	JProgressBar jb;
	JPanel back = new JPanel();
	JLabel timeData = new JLabel();

	ImageIcon backgroundImg = new ImageIcon(getClass().getResource("..\\Resource\\frame.png"));

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(backgroundImg.getImage(), 0, 0, displayThread.getWidth(), displayThread.getHeight(), null);
		setOpaque(false);// 그림을 표시하게 설정,투명하게 조절
		super.paintComponent(g);
	}

	private ImageIcon[] chIcon;

	// 9. Singleton pattern의 유일한 instance를 만들기 위해 getInstance()메소드를 만듭니다.
	public static GameRoomForm getInstance(DisplayThread dt, Socket socket) {
		if (uniqueInstance == null) {
			synchronized (GameRoomForm.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new GameRoomForm(dt, socket);
				}
			}
		}
		return uniqueInstance;
	}

	public GrSketch getSketchPanel() {
		return sketchPanel;
	}

	public Client.Queue getQue() {
		return que;
	}

	private GameRoomForm(DisplayThread dt, Socket socket) {

		chIcon = new ImageIcon[4];
		for (int i = 0; i < chIcon.length; i++) {
			chIcon[i] = new ImageIcon(getClass().getResource("..\\Resource\\cch" + (i + 1) + ".png"));
		}
		json = new JSONObject();

		this.displayThread = dt;
		this.socket = socket;
		userMessageProcessor = new UserMessageProcessor();
		unt = UserInputThread.getInstance(socket);
		jsonParser = new JSONParser();

		userRunnable = unt;

		que = new Client.Queue();
		Runnable r = new QueueExecuteThread(this);
		Thread t = new Thread(r);
		t.start();

		setLayout(null);

		// 채팅 표시할 텍스트 필드 할당
		for (int i = 0; i < 6; i++) {
			userChat[i] = new UserChat(i);
			add(userChat[i]);
		}

		// 1. 스케치북
		grSketch.setBounds(280, 15, 880, 600);

		grSketch.setBackground(Color.BLACK);

		grSketch.setLayout(null);

		sketchPanel.setBounds(0, 0, 880, 600);

		grSketch.add(sketchPanel);

		add(grSketch);

		// 2. 유저패널
		for (int i = 0; i < 6; i++) {
			userPanel[i] = new JPanel();
			userPanel[i].setBackground(new Color(24, 148, 198));
		}
		for (int i = 0; i < 3; i++) {
			userPanel[i].setLayout(null);
			userPanel[i].setBounds(15, 15 + (205 * i), 250, 190);
			add(userPanel[i]);
		}
		for (int i = 3; i < 6; i++) {
			userPanel[i].setLayout(null);
			userPanel[i].setBounds(1175, 15 + (205 * (i - 3)), 250, 190);
			add(userPanel[i]);
		}

		// 라벨
		for (int i = 0; i < 6; i++) {

			label[i] = new JLabel("");
			label[i].setBounds(10, 10, 130, 170);
			label[i].setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.WHITE));
			label[i].setFont(font_player);
			label[i].setHorizontalAlignment(JLabel.CENTER);
			userPanel[i].add(label[i]);

			label[6 + i] = new JLabel("");
			label[6 + i].setBounds(145, 10, 95, 60);
			label[6 + i].setFont(font_player);
			label[6 + i].setHorizontalAlignment(JLabel.CENTER);
			label[6 + i].setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.WHITE));
			userPanel[i].add(label[6 + i]);

			label[12 + i] = new JLabel("");
			label[12 + i].setBounds(145, 75, 95, 50);
			label[12 + i].setFont(font_player);
			label[12 + i].setHorizontalAlignment(JLabel.CENTER);
			label[12 + i].setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.WHITE));
			userPanel[i].add(label[12 + i]);

			label[18 + i] = new JLabel("");
			label[18 + i].setBounds(145, 130, 95, 50);
			label[18 + i].setFont(font_player);
			label[18 + i].setHorizontalAlignment(JLabel.CENTER);
			label[18 + i].setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.WHITE));
			userPanel[i].add(label[18 + i]);

		}

		// 3.라운드표시기
		round.setLayout(null);
		round.setBounds(280, 630, 250, 105);
		round.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE, 4), "라운드",
				TitledBorder.CENTER, TitledBorder.TOP, font_Title, Color.white));
		roundLabel.setBounds(10, 20, 230, 85);
		roundLabel.setFont(font_round);
		roundLabel.setHorizontalAlignment(JLabel.CENTER);
		round.setBackground(new Color(24, 148, 198));
		round.add(roundLabel);
		add(round);

		// 5.정답표시기
		answer.setLayout(null);
		answer.setBounds(540, 630, 360, 105);
		answer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE, 4), "문제",
				TitledBorder.CENTER, TitledBorder.TOP, font_Title, Color.white));
		answerLabel.setBounds(10, 10, 340, 85);
		answerLabel.setFont(font_answer);
		answerLabel.setHorizontalAlignment(JLabel.CENTER);
		answer.setBackground(new Color(24, 148, 198));
		answer.add(answerLabel);
		add(answer);

		// 6.채팅
		tf.setBounds(280, 750, 880, 30);
		tf.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() == tf) {
					String message = tf.getText();
					if (message.length() > 10) {
						message = message.substring(0, 10);
					}
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "3309");
					sendData += "," + userMessageProcessor.getJSONData("message", message);
					sendData += "}";
					unt.setInputData(sendData);
					unt.pushMessage();
					tf.setText("");
				}
			}
		});
		add(tf);

		// 7.타이머
		
		  // timeBar = new TimeBar(120); // timeBar.setValue(0); //
		  timer.setBounds(910, 630, 250, 105); // round.setLayout(null); //
		  timer.setBackground(new Color(24, 148, 198)); // round.add(timeBar); // // //
		  //timer.setBounds(15, 630, 250, 150); //
		  //roundLabel.setBounds(10,315,230,100); // timer.add(roundLabel); // //
		  //timeBar.setEnabled(true); // Border four =
		  timer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE, 4), "타이머",
					TitledBorder.CENTER, TitledBorder.TOP, font_Title, Color.white));//
		  timer.setFont(font_Title);
		  timer.add("Center", timeData);
		  //(four).setTitleColor(colorout); // round.setBorder(four); // add(round);
		  add(timer);
		 
		// 9.버튼
		p.setLayout(new GridLayout(3, 1, 0, 15));
		p.setBounds(1175, 630, 250, 150);
		p.setBackground(new Color(255, 0, 0, 0));
		add(p);

		for (int i = 0; i < 1; i++) {
			b[i] = new JButton(new ImageIcon(
					getImageSizeChange(new ImageIcon(getClass().getResource("..\\Resource\\ready.png")), 250, 40)));
			b[i + 1] = new JButton(new ImageIcon(
					getImageSizeChange(new ImageIcon(getClass().getResource("..\\Resource\\invit.png")), 250, 40)));
			b[i + 2] = new JButton(new ImageIcon(
					getImageSizeChange(new ImageIcon(getClass().getResource("..\\Resource\\quit.png")), 250, 40)));
		}

		// 테스트 위해 임시로 만든 버튼 액션
		b[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				/*
				 * Thread t = new Thread(timeBar); t.start();
				 */
				json.clear();
				json.put("method", "3900");
				String sendData = String.valueOf(json);
				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		});

		////////////////////////////////////////////////
		////////////////////////////////////////////////
		b[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == b[1]) {
					// 서버로 보내봅시다.
					// 1. 메소드를 정해봅시다. //
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "3910");
					sendData += "}";
					// 13. 데이터를 서버로 보냅니다!
					unt.setInputData(sendData);
					unt.pushMessage();
				}
			}
		});

		////////////////////////////////////////////////

		for (int i = 0; i < 1; i++) {
			p.add(b[i]);
			b[i].setBorderPainted(false);
			b[i].setFocusPainted(false);

			p.add(b[i + 1]);
			b[i + 1].setBorderPainted(false);
			b[i + 1].setFocusPainted(false);

			p.add(b[i + 2]);
			b[i + 2].setBorderPainted(false);
			b[i + 2].setFocusPainted(false);

		}

		actionPerformMethod();

		inviteDialog = new InviteDialog(this); //// 생성한 이유는?
	}

	private Image getImageSizeChange(ImageIcon icon, int width, int height) {
// TODO Auto-generated method stub
		Image img = icon.getImage();
		Image change = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return change;
	}

	private Object String(String[] answer2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformMethod() {
		// TODO Auto-generated method stub
		(b[2]).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// get out from room. 3600
				// 1. request -> 2. remove from room(find room and remove usr)
				// 3. register to wait room 4. init user's wait room 5. update other usr
				// 서버로 보내봅시다.

				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3600");
				sendData += "}";
				System.out.println("나가기 클릭! : " + sendData);
				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		});

	}

	@Override
	public void operation(java.lang.String data) {
		int index = 0;
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObj = ((JSONObject) jsonParser.parse(data));
			switch ((String) (jsonObj.get("method"))) {
			case "3002":
				displayThread.setBounds(300, 0, 1446, 900);
				displayThread.getCardLayout().show(displayThread.getContentPane(), "gameRoom");
				for (int i = 0; i < 6; i++) {
					userPanel[i].setBackground(new Color(24, 148, 198));
				}
				b[0].setDisabledIcon(new ImageIcon(
						getImageSizeChange(new ImageIcon(getClass().getResource("..\\Resource\\start.png")), 250, 40)));
				// init
				init();
				break;
			case "3402":
				init();
				// 1. init all thing
				for (int i = 0; i < 6; i++) {
					label[i].setIcon(null);
					userPanel[i].setBackground(new Color(24, 148, 198));
				}
				//b[0].setText("게임준비");
				System.out.println("get method 3402");
				JSONArray usr = (JSONArray) jsonObj.get("userList");
				for (int i = 0; i < usr.size(); i++) {
					// label[i].setText(String.valueOf(((JSONObject) usr.get(i)).get("ch")));
					index = Integer.parseInt(String.valueOf(((JSONObject) usr.get(i)).get("ch")));
					System.out.println("icon index : " + index);
					label[i].setIcon(chIcon[index]);
					label[i + 6].setText(String.valueOf(((JSONObject) usr.get(i)).get("id")));
					label[i + 12].setText(String.valueOf(((JSONObject) usr.get(i)).get("lv")));
					label[i + 18].setText(String.valueOf(((JSONObject) usr.get(i)).get("cnt"))); // score
				}
				break;
			case "3014": // rejected to enter the room
				System.out.println("room enter is rejected");
				break;
			case "3702": // get draw coordiate data
				int x = Integer.parseInt(String.valueOf(jsonObj.get("x")));
				int y = Integer.parseInt(String.valueOf(jsonObj.get("y")));
				sketchPanel.setColor(String.valueOf(jsonObj.get("color")));
				sketchPanel.setBrushSize(Integer.parseInt(String.valueOf(jsonObj.get("brushSize"))));
				que.push(x, y);
				System.out.println("que size : " + que.getSize());
				break;
			case "3712": // clear all
				sketchPanel.setColor(String.valueOf(jsonObj.get("color")));
				break;
			case "3722": // init
				que.push(10000, 0);
				// sketchPanel.initSlidingWindow();
				break;
			case "3300":
				int userNum = Integer.parseInt(String.valueOf(jsonObj.get("userNum")));
				System.out.println("3300 userNum : " + userNum);
				String message = String.valueOf(jsonObj.get("message"));
				userChat[userNum].printChat(message);
				break;
			case "3902": // we know who is master
				// 2. mark the master
				int num = Integer.parseInt(String.valueOf(jsonObj.get("master")));
				userPanel[num].setBackground(Color.PINK);
				if (String.valueOf(jsonObj.get("isMaster")).equals("yes")) {
					//b[0].setText("게임시작");
				}
				break;
			case "3903":
				// init all ready state!
				//b[0].setText("게임준비");
				for (int i = 0; i < 6; i++) {
					// userPanel[i].setBackground(colorout);
				}
				// roundLabel.setText("");
				// answerLabel.setText("");
				break;
			case "3904":
				// ready off with id
				index = Integer.parseInt(String.valueOf(jsonObj.get("id")));
				userPanel[index].setBackground(Color.RED);
				break;
			case "3905":
				// ready on with id
				index = Integer.parseInt(String.valueOf(jsonObj.get("id")));
				userPanel[index].setBackground(Color.GREEN);
				break;
			case "3906":
				// notice all ready with id
				JSONArray arr = (JSONArray) jsonObj.get("readyUserList");
				for (int i = 0; i < arr.size(); i++) {
					// userPanel[i].setBackground(color.GREEN);
					index = Integer.parseInt(String.valueOf(((JSONObject) (arr.get(i))).get("id")));
					userPanel[index].setBackground(Color.GREEN);
				}
				break;
			case "3914":
				JOptionPane.showMessageDialog(this, "적어도 두명 이상의 사람이 모두 레디해야 시작할 수 있습니다.");
				break;
			case "3912":
				break;
			case "3922": // Game Start
				que.push(10000, 0);
				sketchPanel.setColor("whiteAll");
				// 1. init timer
				// 2. init paint
				que.push(10000, 0);
				// 3. all user - black
				for (int i = 0; i < 6; i++) {
					userPanel[i].setBackground(Color.BLACK);
				}
				break;
			case "3932": // Round Start
				que.push(10000, 0);
				sketchPanel.setColor("whiteAll");
				for (int i = 0; i < 6; i++) {
					userPanel[i].setBackground(new Color(24, 148, 198));
				}
				index = Integer.parseInt(String.valueOf(jsonObj.get("examiner")));
				userPanel[index].setBackground(new Color(60, 179, 113));
				index = Integer.parseInt(String.valueOf(jsonObj.get("round")));
				System.out.println("3932 index : " + index);
				roundLabel.setText(String.valueOf(index + " / 10"));
				answerLabel.setText(String.valueOf(jsonObj.get("ans")));
				break;
			case "3942": // Round End
				que.push(10000, 0);
				sketchPanel.setColor("whiteAll");
				// init timer
				for (int i = 0; i < 6; i++) {
					userPanel[i].setBackground(Color.YELLOW);
				}
				answerLabel.setText(String.valueOf(jsonObj.get("ans")));
				break;
			case "3920":

				JSONArray inviteUsers = (JSONArray) (jsonObj.get("waitList"));

				inviteDialog.initUserList();
				for (int i = 0; i < inviteUsers.size(); i++) {
					String id = String.valueOf(((JSONObject) inviteUsers.get(i)).get("id"));
					String lv = String.valueOf(((JSONObject) inviteUsers.get(i)).get("lv"));
					System.out.println("3920 : " + id + "," + lv);
					inviteDialog.invitedUsers(id, lv);
				}

				inviteDialog.getDialog().setVisible(true);
				break;

			case "3970":

				String inviteId = String.valueOf(jsonObj.get("id"));
				// int sel = JOptionPane.showConfirmDialog(this,inviteId + "님께서 초대를 거절하셨습니다.","
				// ",JOptionPane.OK_OPTION);
				JOptionPane.showMessageDialog(this, inviteId + "님이 초대를 거절하셨습니다.");
				break;
			case "3930":
				inviteId = String.valueOf(jsonObj.get("id"));
				JOptionPane.showMessageDialog(this, inviteId + "님이 대기실에 없습니다.");

				// 1. 목록을 다시 띄운다 -> 서버한테 다시 목록을 달라 한다
				// inviteDialog.setInviteDialogInvisible();
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3910");
				sendData += "}";
				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
				break;
			case "3TIMER": // timer - on
				String tick = String.valueOf(jsonObj.get("tick"));
				System.out.println("tick : " + tick);
				int time = Integer.parseInt(tick);
				timeData.setText((int)(time/60)+" : "+time%60);
				break;
			case "3TIMEOUT": // timer - timeout
				timeData.setText("");
				break;
			}
		} catch (Exception e) {

		}

	}

	@Override
	public JPanel getJPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	public void init() {
		for (int i = 0; i < 24; i++) {
			label[i].setText("");
		}
		roundLabel.setText("");
		answerLabel.setText("");
		timeData.setText("");
	}

	public UserMessageProcessor getUserMessageProcessor() {
		return userMessageProcessor;
	}

	public UserInputThread getUnt() {
		return unt;
	}
}
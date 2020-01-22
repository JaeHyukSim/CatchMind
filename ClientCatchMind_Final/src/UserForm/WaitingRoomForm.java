package UserForm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class WaitingRoomForm extends JPanel implements UserForm, ActionListener, MouseListener {

	private volatile static WaitingRoomForm uniqueInstance;

	private PasswordDialog pwdDialog;
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private Socket socket;
	private UserInputThread unt;
	private JSONParser jsonParser;

	private Runnable userRunnable;
	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!

////////////////////////////////////////////////////////////
	private DefaultTableModel model1, model2;
	private JTable table1, table2;
	private JScrollPane js1, js2, js3;
	private JTextArea ta;
	private JTextField tf;

	private JButton b1, b2, b3, b4;

	private Image back;
////////////////////////////////////////////////////////////
	JLabel greet, myLv, myID, myExp;
	private MakeRoom_Dialog makeroom_dialog;

	private int roomFocus;
	private int waitFocus;

	private JPanel userIfPanel;

	private WaitingRoomForm(DisplayThread dt, Socket socket) {

		roomFocus = -1;
		waitFocus = -1;

		this.displayThread = dt;
		this.socket = socket;
		userMessageProcessor = new UserMessageProcessor();
		unt = UserInputThread.getInstance(socket);
		jsonParser = new JSONParser();
		userRunnable = unt;

		userIfPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				ImageIcon img = new ImageIcon(getClass().getResource("..\\Resource\\UserInfo.png"));
				g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};

		JPanel userChPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				ImageIcon img = new ImageIcon(
						getClass().getResource("..\\Resource\\ch" + (Integer.parseInt(unt.getCh()) + 1) + ".png"));
				g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
				System.out.println("내정보 캐릭터 번호 :" + unt.getCh());
			}
		};
		userIfPanel.setBounds(975, 578, 377, 152);
		userIfPanel.setLayout(null);

		myExp = new JLabel();
		myID = new JLabel();
		myLv = new JLabel();
		greet = new JLabel();

		myExp.setText("0");
		myID.setText("0");
		myLv.setText("0");
		greet.setText("반갑습니다");

		greet.setBounds(220, 25, 100, 30);
		myID.setBounds(220, 53, 100, 30);
		myLv.setBounds(220, 81, 100, 30);
		myExp.setBounds(220, 109, 100, 30);
		userChPanel.setBounds(55, 30, 113, 95);

		userIfPanel.add(myExp);
		userIfPanel.add(myID);
		userIfPanel.add(myLv);

		userIfPanel.add(greet);
		userIfPanel.add(userChPanel);

		this.add(userIfPanel);

		back = this.getToolkit().getImage(getClass().getResource("..\\Resource\\WaitingRoom.jpg"));

////////////////////////////////////////////////////////////
		pwdDialog = new PasswordDialog(this);
		makeroom_dialog = new MakeRoom_Dialog(this);

		// table1에 타이틀 맨 위줄
		// table1에 내용으로 들어갈 내용
		String[] col1 = { "방번호", "방제목", "비밀번호여부", "최대인원", "현재인원", "비고" };
		String[][] row1 = new String[0][col1.length];

		// table1에 DefaultTableModel(기본모양)으로 틀을 만들어 둠
		// table1에 만들어두었던 모델일 넣어줌
		model1 = new DefaultTableModel(row1, col1) {

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}

		};
		table1 = new JTable(model1);
		// 스크롤이 있는 팬을 생성하면서 table1을 추가 시킴
		js1 = new JScrollPane(table1);

		// 위와 동일하게 두번째 JTable생성
		String[] col2 = { "ID", "레벨" };
		String[][] row2 = new String[0][col2.length];

		model2 = new DefaultTableModel(row2, col2) {

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}

		};
		table2 = new JTable(model2);
		js2 = new JScrollPane(table2);

		// WaitRoom Panel에 레이아웃을 없애 줌 안 없애면 setBound가 안됨
		this.setLayout(null);

		// table을 넣어둔 JScrollPane들의 위치를 수정 함
		js1.setBounds(72, 137, 827, 398);
		js2.setBounds(980, 135, 377, 362);

		// 채팅창을 쓸 채팅창과 출력창을 생성
		ta = new JTextArea();
		tf = new JTextField();

		// 채팅창에 내용이 많아지면 스크롤이 생길 수 있도록 JScrollPane에 추가시켜줌
		js3 = new JScrollPane(ta);

		// 채팅창이랑 출력창 위치 배치
		js3.setBounds(72, 590, 827, 187);
		tf.setBounds(72, 778, 827, 30);
		// 채팅창을 WaitRoomPanel에 추가시켜줌
		this.add(tf);
		tf.addActionListener(this);

		ImageIcon joinBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\join1.png"));
		ImageIcon joinBtnIconPressed = new ImageIcon(getClass().getResource("..\\Resource\\join2.png"));
		b1 = new JButton(joinBtnIcon);
		b1.setRolloverIcon(joinBtnIconPressed);
		b1.setPressedIcon(joinBtnIconPressed);
		b1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				me.getComponent().setCursor(displayThread.handCursor);
			}

			@Override
			public void mouseExited(MouseEvent me) {
				me.getComponent().setCursor(displayThread.defaultCursor);
			}
		});
		b1.setBorderPainted(false);

		ImageIcon makeroomBtnIcon = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomB1.png"));
		ImageIcon makeroomIconPressed = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomB2.png"));
		b2 = new JButton(makeroomBtnIcon);
		b2.setRolloverIcon(makeroomIconPressed);
		b2.setPressedIcon(makeroomIconPressed);
		b2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent me) {
				me.getComponent().setCursor(displayThread.handCursor);
			}

			@Override
			public void mouseExited(MouseEvent me) {
				me.getComponent().setCursor(displayThread.defaultCursor);
			}
		});
		b2.setBorderPainted(false);

		b3 = new JButton("나가기");
		b4 = new JButton("정보보기");

		b1.addActionListener(this);
		b2.addActionListener(this);
		b3.addActionListener(this);
		b4.addActionListener(this);

		b1.setBounds(63, 33, 117, 41);
		b2.setBounds(183, 33, 117, 41);
		b3.setBounds(1028, 747, 121, 33);
		b4.setBounds(1175, 747, 121, 33);

		this.add(b1);
		this.add(b2);
		this.add(b3);
		this.add(b4);

		// JScrollPane을 3개다 WaitRoom Pane에 추가 시켜줌
		this.add(js1);
		this.add(js2);
		this.add(js3);

		table1.setColumnSelectionAllowed(false);
		table1.setRowSelectionAllowed(true);
		table2.setColumnSelectionAllowed(false);
		table2.setRowSelectionAllowed(true);

		table1.addMouseListener(this);
		table2.addMouseListener(this);
////////////////////////////////////////////////////////////
	}

	public static WaitingRoomForm getInstance(DisplayThread dt, Socket socket) {
		if (uniqueInstance == null) {
			synchronized (WaitingRoomForm.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new WaitingRoomForm(dt, socket);
				}
			}
		}
		return uniqueInstance;
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub

	}

	/////////////////////////////////////////////// => actionPerformed 사용할 것
	@Override
	public void actionPerformMethod() {
	}
	///////////////////////////////////////////////

	@Override
	public void operation(String data) {
		try {
			JSONObject jsonObj = ((JSONObject) jsonParser.parse(data));

			switch ((String) (jsonObj.get("method"))) {
			case "2002": // card show method (waiting room to game room)
				displayThread.setBounds(300, 0, 1446, 900);
				displayThread.getCardLayout().show(displayThread.getContentPane(), "waitingRoom");
				ta.setText("");
				tf.setText("");
				// init 함수 구현해서 1. 텍스트에어리어 초기화 2. 텍스트필드초기화

				break;
			case "2102": // chat method
				ta.setText(ta.getText() + "\n" + (String) jsonObj.get("chat"));
				//////////////////////////////////////////////////////
				ta.setCaretPosition(ta.getDocument().getLength());
				//////////////////////////////////////////////////////
				break;
			// 대기방에서 클라이언트 전체화면이 전환되는 상황
			case "2012":
				break;
			case "2112": // 입력한 비밀번호가 맞기때문에, 방에 입장
				pwdDialog.pf.setText("");
				pwdDialog.pf.requestFocus();
				pwdDialog.setVisible(false);

				break;
			case "2113": // 입력한 비밀번호가 맞지않기때문에, 다시 입력을 요망
				JOptionPane.showMessageDialog(this, "다시 입력해주세요.");
				pwdDialog.pf.setText("");
				pwdDialog.pf.requestFocus();
				break;
			case "2224":
				// room is full - so you can't make new room
				// 추천 :
				break;
			case "2052": // set false the dialog form
				makeroom_dialog.setVisible(false);
				break;
			case "2062": // for just 'one' user
				// data : wait -> id, lv, state - 여러개
				// : room -> roomId, roomName, roomPassState, roomPass, roomMaxUser,
				// roomCurUser, roomState - 여러개
				JSONArray roomList = (JSONArray) jsonObj.get("roomUserList");
				String passState = "";
				model1.setNumRows(0);
				model2.setNumRows(0);
				for (int i = 0; i < roomList.size(); i++) {
					JSONObject obj = (JSONObject) roomList.get(i);
					passState = "";
					if (((String) obj.get("roomPassState")).equals("0")) {
						passState = "비번 없음";
					} else {
						passState = "비번있음";
					}
					String[] tmpData = { (String) obj.get("roomId"),
							// (String)unt.getId(),
							(String) obj.get("roomName"), passState, (String) obj.get("roomMaxUser"),
							(String) obj.get("roomCurUser"), (String) obj.get("roomState") };

					model1.addRow(tmpData);
				}
				JSONArray userList = (JSONArray) jsonObj.get("waitingUserList");
				for (int i = 0; i < userList.size(); i++) {
					JSONObject obj = (JSONObject) userList.get(i);
					String[] tmpData = { (String) obj.get("id"), (String) obj.get("lv"), (String) obj.get("state") };
					model2.addRow(tmpData);
				}
				if (!(roomFocus < 0 || roomFocus >= table1.getRowCount())) {
					table1.setRowSelectionInterval(roomFocus, roomFocus);
				}
				if (!(waitFocus < 0 || waitFocus >= table2.getRowCount())) {
					table2.setRowSelectionInterval(waitFocus, waitFocus);
				}
				break;
			case "2072": // for other usr -> get one class
			case "2904": // do not allow to enter the room
				JOptionPane.showMessageDialog(this, "이미 게임중입니다.");
				break;
			case "2940": // 대기실에 있을 경우에 - 그 사람한테 jop띄워야 합니다 " ~님이 ~방으로 초대했습니다. 수락하시겠습니까?"
				String inviteId = String.valueOf(jsonObj.get("id")); // 초대한 ID
				String roomId = String.valueOf(jsonObj.get("roomId")); // 초대한 ID있는 방정보

				String[] bname = { "수락", "거절" };
				// int sel = JOptionPane.showConfirmDialog(this,inviteId + "님께서 " + roomId +
				// "번방으로 초대하셨습니다.","초대메시지",JOptionPane.YES_NO_OPTION);
				int sel = JOptionPane.showOptionDialog(this, inviteId + "님께서 " + roomId + "번방으로 초대하셨습니다.", "초대메시지",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, bname, bname[0]);

				if (sel == JOptionPane.YES_OPTION) {
					// 방으로 들어가고 싶다
					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "3090");
					sendData += "," + userMessageProcessor.getJSONData("room", roomId); //
					sendData += "}";
					// 13. 데이터를 서버로 보냅니다!

					unt.setInputData(sendData);

					unt.pushMessage();
				} else {
					// 싫다

					// JOP이기때문에 수락의 경우에만 이벤트를 주고 그 외의 경우(거절 , X버튼)에는 이벤트를 줄 필요없이 자동으로 팬이 없어진다.
					/*
					 * 1.초대한 사람이 초대거절메시지 받기 "~~~님이 초대를 거절 하였습니다." JOPTIONPANE!으로 나타날 예정!
					 * 
					 * 서버로 보내야할 데이터 ==> 거절한 유저 ID,
					 * 
					 */

					String sendData = "{";
					sendData += userMessageProcessor.getJSONData("method", "3960"); // 메소드보내기
					sendData += "," + userMessageProcessor.getJSONData("id", String.valueOf(jsonObj.get("id"))); // A한테
																													// 메시지를
																													// 보내는데,
					sendData += "," + userMessageProcessor.getJSONData("roomId", String.valueOf(jsonObj.get("roomId"))); // A가
																															// 있는
																															// 방의
																															// 정보도
																															// 보낸다
					sendData += "}";

					// 초대거절한 id를 따로 보내지 않아도 된다. 자기 자신의 정보이기 때문에.
					// 메소드만 보내고 서버에서 초대 거절한 id를 보내줘야함.
					//

					// 13. 데이터를 서버로 보냅니다!

					unt.setInputData(sendData);

					unt.pushMessage();

				}
				break;
			case "2870":
				JOptionPane.showMessageDialog(this, "해당 방이 존재하지 않습니다. 다른 방을 선택해 주세요.");
				break;
			case "2500":
				MyIfPanel((String) jsonObj.get("EXP"), (String) jsonObj.get("LV"), (String) jsonObj.get("ID"));
				break;

			case "2602":
				CreateWaitUserInfoDialog((String) jsonObj.get("ID"), (String) jsonObj.get("Exp"),
						(String) jsonObj.get("Lv"), (String) jsonObj.get("Ch"));
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public JPanel getJPanel() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 엔터를 쳤을 때 함수로 들어오게되고 그게 tf라면
		if (e.getSource() == tf) {

			// 서버로 보내봅시다.
			// 1. 메소드를 정해봅시다. 2100 -> 2102
			String sendData = "{";
			sendData += userMessageProcessor.getJSONData("method", "2100");
			sendData += "," + userMessageProcessor.getJSONData("chat", tf.getText());
			sendData += "}";

			tf.setText("");

			System.out.println("method = 2100, sendData : " + sendData);

			// 13. 데이터를 서버로 보냅니다!
			unt.setInputData(sendData);
			unt.pushMessage();

//			// 임시 String을 만든다
//			String temp = null;
//			// temp에 tf에 있던 텍스르틀 넘겨준다
//			temp = /*ScreenVO.getS_VO()+*/ tf.getText();
//			if(temp == null)
//				return;
//			else
//				System.out.println();
//			// tf에 남아있던 텍스트를 지우고 커서를 다시 돌려줌
//			tf.setText("");
//			tf.requestFocus();
//			// ta에 저장했던 text를 뿌려줌
//			ta.append(temp);

		}
		if (e.getSource() == b2) {
			makeroom_dialog.setVisible(true);

		}
		if (e.getSource() == b1) {
			int data = table1.getSelectedRow();// index
			if (data != -1) {

				System.out.println("data : " + data);

				String passstate = table1.getValueAt(roomFocus, 2).toString();
				System.out.println("passstate: " + passstate);
				String havePassword = "비번있음";

				if (passstate.equals(havePassword)) {
					pwdDialog.setRoomId(String.valueOf(roomFocus));
					System.out.println("pwdDialog pushed!! : " + roomFocus);
					pwdDialog.setVisible(true);

					return;
				}
				////////////////////
				// 서버로 보내봅시다.
				// 1. 메소드를 정해봅시다. 2100 -> 2102
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3010");
				sendData += "," + userMessageProcessor.getJSONData("room", String.valueOf(data));
				sendData += "}";

				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		}
		if (e.getSource() == b3) {
			// 서버로 보내봅시다.
			// 1. 메소드를 정해봅시다. 2100 -> 2102
			String sendData = "{";
			sendData += userMessageProcessor.getJSONData("method", "2400");
			sendData += "}";

			// 13. 데이터를 서버로 보냅니다!
			unt.setInputData(sendData);
			unt.pushMessage();
		}
		if (e.getSource() == b4 && waitFocus != -1) {
			String sendData = "{";
			sendData += userMessageProcessor.getJSONData("method", "2600");
			sendData += "," + userMessageProcessor.getJSONData("ID", table2.getValueAt(waitFocus, 0).toString());
			sendData += "}";
			unt.setInputData(sendData);
			unt.pushMessage();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == table1) {
			roomFocus = table1.getSelectedRow();
			System.out.println("roomFocus : " + roomFocus);

			if (e.getClickCount() == 2) {
				int data = table1.getSelectedRow();// index
				System.out.println("data : " + data);

				String passstate = table1.getValueAt(roomFocus, 2).toString();
				System.out.println("passstate: " + passstate);
				String havePassword = "비번있음";

				if (passstate.equals(havePassword)) {
					pwdDialog.setRoomId(String.valueOf(roomFocus));
					System.out.println("pwdDialog pushed!! : " + roomFocus);
					pwdDialog.setVisible(true);

					return;
				}
				////////////////////
				// 서버로 보내봅시다.
				// 1. 메소드를 정해봅시다. 2100 -> 2102
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3010");
				sendData += "," + userMessageProcessor.getJSONData("room", String.valueOf(data));
				sendData += "}";

				// 13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				unt.pushMessage();
			}
		} else if (e.getSource() == table2) {
			waitFocus = table2.getSelectedRow();
			System.out.println("waitFocus : " + waitFocus);
			if (e.getClickCount() == 2) {
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "2600");
				sendData += "," + userMessageProcessor.getJSONData("ID", table2.getValueAt(waitFocus, 0).toString());
				sendData += "}";

				unt.setInputData(sendData);
				unt.pushMessage();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public UserInputThread getUnt() {
		return unt;
	}

	public void setUnt(UserInputThread unt) {
		this.unt = unt;
	}

	public UserMessageProcessor getUserMessageProcessor() {
		return userMessageProcessor;
	}

	public void setUserMessageProcessor(UserMessageProcessor userMessageProcessor) {
		this.userMessageProcessor = userMessageProcessor;
	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		g.drawImage(back, 0, 0, getWidth(), getHeight(), this);
	}

	//////////////////////////////////////////////////
	public void CreateWaitUserInfoDialog(String id, String exp, String lv, String ch) {
		JDialog waitUserDaialog = new JDialog();
		JPanel userIfPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				ImageIcon img = new ImageIcon(getClass().getResource("..\\Resource\\UserInfo.png"));
				g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};
		JPanel userChPanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				ImageIcon img = new ImageIcon(
						getClass().getResource("..\\Resource\\ch" + (Integer.parseInt(ch) + 1) + ".png"));
				g.drawImage(img.getImage(), 0, 0, getWidth(), getHeight(), null);
			}
		};

		// waitUserDaialog.setLayout(null);
		userIfPanel.setLayout(null);
		// userChPanel.setLayout(null);

		myExp = new JLabel();
		myID = new JLabel();
		myLv = new JLabel();
		greet = new JLabel();

		greet.setText("반갑습니다");
		myID.setText("ID  : " + id);
		myLv.setText("LV  : " + lv);
		myExp.setText("EXP : " + exp);

		greet.setBounds(220, 25, 100, 30);
		myID.setBounds(220, 53, 100, 30);
		myLv.setBounds(220, 81, 100, 30);
		myExp.setBounds(220, 109, 100, 30);
		userChPanel.setBounds(55, 30, 113, 95);

		userIfPanel.add(myExp);
		userIfPanel.add(myID);
		userIfPanel.add(myLv);

		userIfPanel.add(greet);
		userIfPanel.add(userChPanel);

		waitUserDaialog.add(userIfPanel);
		waitUserDaialog.setVisible(true);
		waitUserDaialog.setBounds(975, 378, 377, 192);
		waitUserDaialog.setModal(true);
		System.out.println("상대정보 캐릭터 번호 :" + ch);
	}

	public void MyIfPanel(String Exp, String Lv, String Id) {
		myID.setText("ID  : " + Id);
		myLv.setText("LV  : " + Lv);
		myExp.setText("EXP : " + Exp);

	}
}
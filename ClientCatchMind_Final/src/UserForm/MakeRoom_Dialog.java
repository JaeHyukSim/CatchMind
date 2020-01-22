package UserForm;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import Client.UserMessageProcessor;
import javafx.scene.layout.GridPane;

public class MakeRoom_Dialog extends JDialog implements ActionListener {

	private UserMessageProcessor userMessageProcessor;
	public JTextField tf;
	public JPasswordField pf;
	public JButton b1, b2;
	///////////////////////
	JComboBox box;
	JRadioButton rb[];
	private String roomPassState; // 방 공개, 비공개여부
	///////////////////////

	///////////////////////
	private JPanel backP;
	private JPanel jButtonP;
	private JPanel jRadioButtonP;
	private JPanel jBoxP;

	///////////////////////

	private WaitingRoomForm waitingRoomForm;

	public MakeRoom_Dialog(WaitingRoomForm wr) {


		jButtonP = new JPanel();
		jRadioButtonP = new JPanel();
		jBoxP = new JPanel();
	
		backP = new JPanel() {
			@Override
	         public void paintComponent(Graphics g) {
	        		 ImageIcon img = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoom.png"));
	        		 g.drawImage(img.getImage(), 0, 0,getWidth(),getHeight(), null);   
	         }
		};
		
		ImageIcon MakeRoomD_B1_1 = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomD_B1_1.png"));		
		ImageIcon MakeRoomD_B1_2 = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomD_B1_2.png"));
		ImageIcon MakeRoomD_B2_1 = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomD_B2_1.png"));		
		ImageIcon MakeRoomD_B2_2 = new ImageIcon(getClass().getResource("..\\Resource\\MakeRoomD_B2_2.png"));
		
		this.add(backP);	
		backP.setLayout(null);
		
		///////////////////////

		this.waitingRoomForm = wr;

		this.setBounds(700, 300, 300, 400);

		userMessageProcessor = wr.getUserMessageProcessor();

		tf = new JTextField();
		pf = new JPasswordField();
		
		b1 = new JButton(MakeRoomD_B1_1);		
		b1.setRolloverIcon(MakeRoomD_B1_2);
		b1.setPressedIcon(MakeRoomD_B1_2);

		b2 = new JButton(MakeRoomD_B2_1);
		b2.setRolloverIcon(MakeRoomD_B2_2);
		b2.setPressedIcon(MakeRoomD_B2_2);		
		
		rb = new JRadioButton[2];
		rb[0] = new JRadioButton("공개");
		rb[1] = new JRadioButton("비공개");
		
		box = new JComboBox();
		
		
		jRadioButtonP.add(rb[0]);
		jRadioButtonP.add(rb[1]);

		b1.addActionListener(this);
		b2.addActionListener(this);
		
		jButtonP.add(b1);
		jButtonP.add(b2);
	
		backP.add(jButtonP);
		
		jBoxP.add(box);
		
		backP.add(jBoxP);		

		backP.add(jRadioButtonP);
		backP.add(tf);
		backP.add(pf);
		

		for (int i = 3; i <= 6; i++) {
			box.addItem(i + "명");
		}

		tf.setBounds(80,85,202,35);
		jRadioButtonP.setBounds(80,121,202,35);	
		pf.setBounds(80,159,202,35);
		jBoxP.setBounds(80,194,198,35);
		jButtonP.setBounds(88,285,164,45);
		jButtonP.setLayout(new GridLayout(1,2,5,1));
		
//		jRadioButtonP.setBackground(new Color(255,255,219));
		jBoxP.setOpaque(false);
		jButtonP.setOpaque(false);
		
		
		

		
		this.setModal(true);
		this.setVisible(false);

		rb[0].setSelected(true);
		roomPassState = "0";
		pf.setEnabled(false);
		// countOfMaximumUser=3;

		rb[0].addActionListener(this);
		rb[1].addActionListener(this);

		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==b1)
		{
			/*
			int sel = JOptionPane.showConfirmDialog(this, "수락하시겠습니까?[Y/N]", "수학하기", JOptionPane.YES_NO_OPTION);
			if(sel ==JOptionPane.YES_OPTION) {
				System.out.println("^^^^^^^^^^^^^^^^");
			}else if(sel == JOptionPane.NO_OPTION) {
				System.out.println("ㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜㅜ");
			}
			*/
			// method : 3000 -> 3002 -> 1. 방 Arraylist에 추가해줘야대 2. 대기실 인원에서 자기를 제외해야돼
			String sendData = "{";
			sendData += (String)userMessageProcessor.getJSONData("method", "3000");
			sendData += "," + (String)userMessageProcessor.getJSONData("roomCreate", tf.getText());
			sendData += "," + (String)userMessageProcessor.getJSONData("roomPwd", String.valueOf(pf.getPassword()));
			sendData += "," + (String)userMessageProcessor.getJSONData("roomPassState", roomPassState);
			sendData += "," + (String)userMessageProcessor.getJSONData("countOfMaximumUser", String.valueOf(box.getSelectedIndex()+3));
			sendData += "}";
		
			System.out.println("method = 3000, sendData : " + sendData);
		
			//13. 데이터를 서버로 보냅니다!
			waitingRoomForm.getUnt().setInputData(sendData);
			waitingRoomForm.getUnt().pushMessage();
			
			tf.setText("");
			pf.setText("");
			pf.setEnabled(false);
			rb[0].setSelected(true);
			rb[1].setSelected(false);
			roomPassState="0";
			tf.requestFocus();
			box.setSelectedIndex(0);
			/*
			sendData = "{";
			sendData += (String)userMessageProcessor.getJSONData("method", "3400");
			sendData += "}";
			//13. 데이터를 서버로 보냅니다!
			waitingRoomForm.getUnt().setInputData(sendData);
			Runnable r2 = waitingRoomForm.getUnt();
			Thread t1 = new Thread(r2);
			t1.start();
			*/
		}
		else if(e.getSource()==b2)
		{
			tf.setText("");
			pf.setText("");
			pf.setEnabled(false);
			rb[0].setSelected(true);
			rb[1].setSelected(false);
			roomPassState="0";
			tf.requestFocus();
			box.setSelectedIndex(0);
			this.setVisible(false);
		}
		else if(e.getSource()==rb[0]) // 공개 버튼
		{
			if(roomPassState.equals("0"))
			{
				rb[0].setSelected(true);
				return;
			}
			rb[1].setSelected(false);
			roomPassState = "0";
			pf.setText("");
			pf.setEnabled(false);
		}
		else if(e.getSource()==rb[1]) // 비공개 버튼
		{
			if(roomPassState.equals("1"))
			{
				rb[1].setSelected(true);
				return;
			}
			rb[0].setSelected(false);
			roomPassState = "1";
			pf.setText("");
			pf.setEnabled(true);
		}
	}
}
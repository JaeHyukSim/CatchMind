package UserForm;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.simple.parser.JSONParser;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class InviteDialog extends JDialog implements ActionListener, MouseListener {

	private UserMessageProcessor userMessageProcessor;

	private DisplayThread displayThread;

	private Socket socket;

	private UserInputThread unt;

	private JSONParser jsonParser;

	private Runnable userRunnable;

	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!

	private GameRoomForm gameRoomForm;

	private DefaultTableModel model1;

	private JTable table1;

	private JButton b1;

	private JLabel la1;

	public InviteDialog(GameRoomForm grf) // 이렇게 넣어준 이유는? (GameRoomForm grf)??
	{

		this.gameRoomForm = grf;
		setBounds(700, 300, 300, 300); // 크기와 위치를 잡아줌

		userMessageProcessor = grf.getUserMessageProcessor();

		la1 = new JLabel("친구 추가", JLabel.CENTER);
		la1.setFont(new Font("Black Han Sans", Font.BOLD, 15));

		b1 = new JButton("확인");

		String[] col1 = { "ID", "Level" };
		String[][] row1 = new String[0][col1.length]; // col1.length=> 2

		model1 = new DefaultTableModel(row1, col1) {

			@Override
			public boolean isCellEditable(int row, int column) { // 더블클릭시 수정방지
				// TODO Auto-generated method stub
				return false;
			}

		};
		// dialog에 table1을 붙여야 합니다

		table1 = new JTable(model1); // table1에 model1을 올림
		table1.addMouseListener(this);
		JScrollPane js = new JScrollPane(table1); // table1에 스크롤기능 추가해줌
		table1.setBounds(50, 50, 50, 50);
		this.add(js);

		add("North", la1);

		add("Center", js);

		add("South", b1);

		js.setVisible(true);
		
		
		b1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				setInviteDialogInvisible();
				
			}
		});
		
		
		

	}// 생성자 끝

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == table1) {

			if (e.getClickCount() == 2)

			{

				int data = table1.getSelectedRow();// index
				String inviteId = String.valueOf(table1.getValueAt(data, 0));
				System.out.println("Invite data : " + data);
				System.out.println("Invite Id : " + inviteId);
				
				int sel = JOptionPane.showConfirmDialog(this, "초대 메시지를 보내시겠습니까?", " " , JOptionPane.YES_NO_OPTION);
				if(sel == JOptionPane.YES_OPTION) {
					// 1. 메소드를 정해봅시다. //

					String sendData = "{";

					sendData += userMessageProcessor.getJSONData("method", "3990");   // 메소드보내기
					sendData += ","+ userMessageProcessor.getJSONData("id", String.valueOf(inviteId)); // 해당 줄에 있는 정보를 서버로 보내기
					sendData += "}";

					// 13. 데이터를 서버로 보냅니다!
					System.out.println("3990 sendData : " + sendData);
					
					gameRoomForm.getUnt().setInputData(sendData);
					gameRoomForm.getUnt().pushMessage();

				}

				// 서버로 보내봅시다.
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
	
	//이 메소드는 테이블에 데이터를 뿌리는 메소드
	public void initUserList() {
		model1.setNumRows(0);
	}
	public void invitedUsers(String id, String lv) {
		String[] data = new String[2];
		data[0] = id; data[1] = lv;
		model1.addRow(data);
	}

	public JDialog getDialog() {
		return this;
	}
	
	public void setInviteDialogInvisible()
	{
	
		this.setVisible(false);
	}
	
	
}
package UserForm;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import Client.UserMessageProcessor;

public class PasswordDialog extends JDialog implements ActionListener{
	
		private UserMessageProcessor userMessageProcessor;
		private WaitingRoomForm waitingRoomForm;

		
		private String roomId;

		
		JButton b1,b2;
		JLabel la1;
		JPasswordField pf;
		public PasswordDialog(WaitingRoomForm wr)
		{
			waitingRoomForm = wr;
			userMessageProcessor = wr.getUserMessageProcessor();
			
			la1=new JLabel("비밀번호",JLabel.LEFT);
			pf=new JPasswordField();
			
			b1=new JButton("확인");
			b2=new JButton("취소");
			
			
			//배치
			setLayout(null);
			la1.setBounds(20,15,60,30);
			pf.setBounds(85, 15, 150, 30);
			
			add(la1);add(pf);
			
			JPanel p=new JPanel();
			p.add(b1);p.add(b2);
			p.setBounds(75, 60, 150, 30);
			add(p);
			setBounds(723, 450, 300, 150);
			setModal(true);
			
			b1.addActionListener(this);
			b2.addActionListener(this);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()==b1)
			{
				
				
				
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "3010");
				sendData += "," + userMessageProcessor.getJSONData("room", roomId); // 방의 인덱스 번호
				sendData += "," + userMessageProcessor.getJSONData("pass", String.valueOf(pf.getPassword()));
				sendData += "}";
				
				//13. 데이터를 서버로 보냅니다!
				waitingRoomForm.getUnt().setInputData(sendData);
				waitingRoomForm.getUnt().pushMessage();
				
				
				
				
				
				
				/*
				String sendData="{";
				sendData += (String)userMessageProcessor.getJSONData("method", "2110");
				sendData += "," + (String)userMessageProcessor.getJSONData("roomId", roomId);
				sendData += "," + (String)userMessageProcessor.getJSONData("roomPwd", String.valueOf(pf.getPassword()));
				sendData += "}";
				System.out.println("before send message from passsword : " + roomId);
				waitingRoomForm.getUnt().setInputData(sendData);
				waitingRoomForm.getUnt().pushMessage();
				*/
			}
			else if(e.getSource()==b2)
			{
				setVisible(false);
			}
		}
		
		public void setRoomId(String id) {
			roomId = id;
		}
}
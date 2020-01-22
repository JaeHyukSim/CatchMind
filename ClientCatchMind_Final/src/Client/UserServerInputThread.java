package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import UserForm.GameRoomForm;
import UserForm.LoginFormVer1;
import UserForm.SignUpForm;
import UserForm.UserForm;
import UserForm.WaitingRoomForm;
import jdk.nashorn.internal.scripts.JD;

public class UserServerInputThread implements Runnable{
	
	private volatile static UserServerInputThread uniqueInstance;
	
	private JSONParser jsonParser;
	
	private BufferedReader inFromServer;
	private String inputData;
	
	private UserForm loginForm;
	private UserForm waitingRoomForm;
	private UserForm gameRoomForm;
	
	private DisplayThread displayThread;
	
	private UserMessageProcessor userMessageProcessor;
	
	private UserServerInputThread(Socket socket) {
		try {
			
			displayThread = DisplayThread.getInstance(socket);
			
			loginForm = LoginFormVer1.getInstance(displayThread, socket);
			waitingRoomForm = WaitingRoomForm.getInstance(displayThread, socket);
			gameRoomForm = GameRoomForm.getInstance(displayThread, socket);
			//jpanel = loginForm.getJPanel();
			jsonParser = new JSONParser();
			
			
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			userMessageProcessor = new UserMessageProcessor();
		} catch (IOException e) {
			System.out.println("in UserServerInputThread - inFromServer error");
			e.printStackTrace();
		}
	}
	
	public static UserServerInputThread getInstance(Socket socket) {
		if(uniqueInstance == null) {
			synchronized (UserServerInputThread.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new UserServerInputThread(socket);
				}
			}
		}
		return uniqueInstance;
	}
	
	@Override
	public void run() {
		try {
			while(true) {
				inputData = inFromServer.readLine();
				try {
					JSONObject jsonObj = (JSONObject)jsonParser.parse(inputData);
					
					char op = ((String)jsonObj.get("method")).charAt(0);
					switch(op) {
					case '1':
						loginForm.operation(inputData);	break;
					case '2':
						waitingRoomForm.operation(inputData); break;
					case '3':
						gameRoomForm.operation(inputData); break;
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}catch(IOException e) {
			System.out.println("in UserServerInputThread - readLine error");
		}
	}
}

package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import Message.JSONMessage;
import Message.MessageForm;

public class UserInputThread implements Runnable{

	private BufferedReader inFromUser;
	private OutputStream outToServer;
	private String inputData;
	private MessageForm messageForm;
	
	private String id;
	private String state;
	private String lv;
	private String exp;
	private String ch;
	
	private volatile static UserInputThread uniqueInstance;
	
	public MessageForm getMessageForm() {
		return messageForm;
	}

	public void setMessageForm(MessageForm messageForm) {
		this.messageForm = messageForm;
	}

	private UserInputThread(Socket socket) {
		try {
			//버퍼 두개를 할당한다
			this.outToServer = new DataOutputStream(socket.getOutputStream());
			this.messageForm = new JSONMessage();
		} catch (UnsupportedEncodingException e) {
			System.out.println("inFromUser buffer UnsupportedEncodingException error");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("inFromUser IOException error");
			e.printStackTrace();
		}
	}
	
	public static UserInputThread getInstance(Socket socket) {
		if(uniqueInstance == null) {
			synchronized (UserInputThread.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new UserInputThread(socket);
				}
			}
			return uniqueInstance;
		}
		return uniqueInstance;
	}
	
	@Override
	public void run() {
		/*
		try {
			inputData += "\n";
			outToServer.write(inputData.getBytes("UTF-8"));
		}catch(IOException e) {
			System.out.println("UserInputThread IOException error");
		}
		*/
	}
	public void pushMessage() {
		try {
			inputData += "\n";
			outToServer.write(inputData.getBytes("UTF-8"));
		}catch(IOException e) {
			System.out.println("UserInputThread IOException error");
		}
	}
	
	public String getInputData() {
		return inputData;
	}

	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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
	
	
}

package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ServerFromUser implements Runnable, Observer{
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;
	private MegaStation station;
	private ServerMessageProcessor serverMessageProcessor;
	private String userMessage;
	private Socket socket;
	
	private String id;
	private String lv;
	private String exp;
	private String ch;
	
	private String state;
	private String roomId;
	private String readyState;

	private String cnt;
	
	private ArrayList<Observer> friends;
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public ServerFromUser(MegaStation station, Socket socket) {
		id = "#";
		lv = "1";
		state = "1";
		roomId = "0";
		readyState = "0";
		exp = "123";
		lv = "2";
		ch = "2";
		friends = new ArrayList<Observer>();
		cnt = "0";
		
		this.station = station;
		this.socket = socket;
		serverMessageProcessor = ServerMessageProcessor.getInstMessageProcessor();
		try {
			inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
			station.registerObserver(this);
		} catch (IOException e) {
			System.out.println("in ServerFromUser - inFromClient error");
			e.printStackTrace();
		}
	}
	
	public String getReadyState() {
		return readyState;
	}

	public void setReadyState(String readyState) {
		this.readyState = readyState;
	}

	@Override
	public void run() {
		try {
			while(true) {
				userMessage = inFromClient.readLine();
				System.out.println("userMessage : " + userMessage);
				userMessage = serverMessageProcessor.processingServerMessage(userMessage, this);
			}
		}catch(IOException e) {
			System.out.println("in ServerFromUser - userMessage error");
			
			String tmp;
			
			System.out.println("delete operation : room id : " + roomId);
			if(roomId.equals("0") == false) {
				System.out.println("delete operation ok! remove room target!");
				
				tmp = "{\"method\":\"3601\"}";
				tmp = serverMessageProcessor.processingServerMessage(tmp, this);
			}
			station.removeObserver(this);
			station.removeWaitObserver(this);
			tmp = "{\"method\":\"2070\"}";
			tmp = serverMessageProcessor.processingServerMessage(tmp, this);
		}
	}
	@Override
	public void dataSend(String data) {
		
		try {
			data += "\n";
			outToClient.write(data.getBytes("UTF-8"));
			//outToClient.write(data.getBytes("MS949"));
		} catch (IOException e) {
			System.out.println("in ServerFromUser - outToClient write encoding error");
			e.printStackTrace();
		}
	}
	@Override
	public ArrayList<RoomData> getRoomList(){
		return station.getRoomUserList();
	}
	@Override
	public ArrayList<Observer> getUserList() {
		return station.getUserList();
	}
	@Override
	public void setRegisterStation() {
		station.registerObserver(this);
	}
	@Override
	public void setWaitingList() {
		station.registerWaitObserver(this);
	}
	@Override
	public ArrayList<Observer> getWaitingList(){
		return station.getWaitUserList();
	}

	public String getLv() {
		return lv;
	}

	public void setLv(String lv) {
		this.lv = lv;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	public MegaObserverble getStation() {
		return station;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getCh() {
		return ch;
	}

	public void setCh(String ch) {
		this.ch = ch;
	}

	public String getCnt() {
		return cnt;
	}

	public void setCnt(String cnt) {
		this.cnt = cnt;
	}

	public String getExp() {
		return exp;
	}

	public void setExp(String exp) {
		this.exp = exp;
	}

	public ArrayList<Observer> getFriends() {
		return friends;
	}

	public void setFriends(ArrayList<Observer> friends) {
		this.friends = friends;
	}
	
	
}

package Server;

import java.util.ArrayList;

public class Station implements Observable{
	private ArrayList<Observer> userList;
	private ArrayList<Observer> waitingUserList;
	private ArrayList<RoomData> roomUserList;
	
	
	public Station() {
		
		userList = new ArrayList<Observer>();
		waitingUserList = new ArrayList<Observer>();
		roomUserList = new ArrayList<RoomData>();
		
	}
	public void registerObserver(Observer o) {
		userList.add(o);
	}
	public void removeObserver(Observer o) {
		int i = userList.indexOf(o);
		if(i != -1) {
			userList.remove(i);
		}
		System.out.println("removement operating ...");
		System.out.println("userCount : " + userList.size());
	}
	
	
	public void broadcastObserver(String data) {
		Observer o;
		for(int i = 0; i < userList.size(); i++) {
			o = (Observer)userList.get(i);
			o.dataSend(data);
		}
		System.out.println("broadcasting...");
		System.out.println(data);
	}
	public void unicastObserver(String data, Observer o) {
		int i = userList.indexOf(o);
		if(i != -1) {
			o.dataSend(data);
		}
	}
	public void broadcastWaitingObserber(String data) {
		Observer o;
		for(int i = 0; i < waitingUserList.size(); i++) {
			o = (Observer)waitingUserList.get(i);
			o.dataSend(data);
		}
	}
	
	
	public ArrayList<Observer> getUserList(){
		return userList;
	}
	public ArrayList<Observer> getWaitingList(){
		return waitingUserList;
	}
	public ArrayList<RoomData> getRoomList(){
		return roomUserList;
	}
	public void registerWaitingUser(Observer o) {
		waitingUserList.add(o);
	}
	public void removeWaitingUser(Observer o) {
		int i = waitingUserList.indexOf(o);
		if(i != -1) {
			waitingUserList.remove(i);
		}
	}
	public void registerRoomUserList(RoomData rd) {
		roomUserList.add(rd);
	}
	public void removeRoomUserList(RoomData rd) {
		int i = roomUserList.indexOf(rd);
		if(i != -1) {
			roomUserList.remove(i);
		}
	}
	public RoomData findUserListById(String id) {
		for(int i = 0; i < roomUserList.size(); i++) {
			if(roomUserList.get(i).getNumberOfRoom().equals(id)) {
				return roomUserList.get(i);
			}
		}
		return null;
	}
	
}

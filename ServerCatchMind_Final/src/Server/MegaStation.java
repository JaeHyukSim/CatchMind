package Server;

import java.util.ArrayList;

public class MegaStation implements MegaObserverble{

	private ArrayList<Observer> userList;
	private ArrayList<Observer> waitingUserList;
	private ArrayList<RoomData> roomUserList;
	
	
	public MegaStation() {
		
		userList = new ArrayList<Observer>();
		waitingUserList = new ArrayList<Observer>();
		roomUserList = new ArrayList<RoomData>();
		
	}
	
	@Override
	public void registerObserver(Observer o) {
		userList.add(o);
	}

	@Override
	public void registerWaitObserver(Observer o) {
		waitingUserList.add(o);
	}

	@Override
	public void registerRoomObserver(RoomData rd) {
		roomUserList.add(rd);
	}

	@Override
	public void removeObserver(Observer o) {
		int i = userList.indexOf(o);
		if(i != -1) {
			userList.remove(i);
		}
	}

	@Override
	public void removeWaitObserver(Observer o) {
		int i = waitingUserList.indexOf(o);
		if(i != -1) {
			waitingUserList.remove(i);
		}
	}

	@Override
	public void removeRoomObserver(String id) {
		
		int index = findRoomObserver(id);
		if(index != -1) {
			roomUserList.remove(index);
		}
	}

	@Override
	
	public void broadcastObserver(String data) {
		Observer o;
		for(int i = 0; i < userList.size(); i++) {
			o = (Observer)userList.get(i);
			o.dataSend(data);
		}
	}

	@Override
	public void broadcastWaitObserver(String data) {
		Observer o;
		for(int i = 0; i < waitingUserList.size(); i++) {
			o = (Observer)waitingUserList.get(i);
			o.dataSend(data);
		}
	}

	@Override
	public void broadcastRoomObserver(String data, String id) {
		int index = findRoomObserver(id);
		if(index != -1) {
			RoomData rd = roomUserList.get(index);
			ArrayList<Observer> roomUser = rd.getUserList();
			
			Observer o;
			for(int i = 0; i < roomUser.size(); i++) {
				o = (Observer)roomUser.get(i);
				o.dataSend(data);
			}
		}else {
			System.out.println("RoomData is empty!");
		}
	}

	@Override
	public void unicastObserver(String data,Observer o) {
		int i = userList.indexOf(o);
		if(i != -1) {
			o.dataSend(data);
		}
	}

	@Override
	public int findRoomObserver(String id) { 
		int index = -1;
		for(int i = 0; i < roomUserList.size(); i++) {
			if(roomUserList.get(i).getNumberOfRoom().equals(id)) {
				index = i; break;
			}
		}
		return index;
	}

	@Override
	public void removeRoomObserverTarget(String id, Observer o) {
		int index = findRoomObserver(id);
		if(index != -1) {
			roomUserList.get(index).removeUserList(o);
			if(o.getReadyState().equals("1")) {
				//roomUserList.get(index).setCountOfReadyUser(String.valueOf(Integer.parseInt(roomUserList.get(index).getCountOfReadyUser()) - 1));
				roomUserList.get(index).someoneReadyOff();
			}
			if(roomUserList.get(index).getCountOfCurrentUser().equals("0")) {
				removeRoomObserver(id);
			}
		}
	}

	public ArrayList<Observer> getUserList(){
		return userList;
		
	}
	public ArrayList<Observer> getWaitUserList(){
		return waitingUserList;
		
	}
	public ArrayList<RoomData> getRoomUserList(){
		return roomUserList;
		
	}

	@Override
	public RoomData findRoomObserver_RoomData(String id) {
		int index = findRoomObserver(id);
		RoomData rd = roomUserList.get(index);
		return rd;
	}
}

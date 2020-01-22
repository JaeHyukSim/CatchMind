package Server;

import java.util.ArrayList;

public interface MegaObserverble {
	
	public void registerObserver(Observer o);
	public void registerWaitObserver(Observer o);
	public void registerRoomObserver(RoomData rd);
	
	public void removeObserver(Observer o);
	public void removeWaitObserver(Observer o);
	public void removeRoomObserver(String id);
	
	public void removeRoomObserverTarget(String id, Observer o);
	
	public void broadcastObserver(String data);
	public void broadcastWaitObserver(String data);
	public void broadcastRoomObserver(String data, String id);
	
	public void unicastObserver(String data,Observer o);
	
	public int findRoomObserver(String id);
	public RoomData findRoomObserver_RoomData(String id);
	
	public ArrayList<Observer> getUserList();
	public ArrayList<Observer> getWaitUserList();
	public ArrayList<RoomData> getRoomUserList();
	
}

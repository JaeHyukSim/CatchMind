package Server;

public interface Observable {
	public void registerObserver(Observer o);
	public void removeObserver(Observer o);
	public void broadcastObserver(String data);
	public void unicastObserver(String data, Observer o);
}

package Server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	public static void main(String[] args) {
		Runnable runnableConnect = new ServerConnectThread();
		Thread threadConnect = new Thread(runnableConnect);
		threadConnect.start();
	}

}

package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectThread implements Runnable{
	private ServerSocket serverSocket;
	private Socket socket;
	private MegaStation station;
	private final int portNum = 16789;
	
	public ServerConnectThread() {
		try {
			System.out.println("server is connected...");
			
			station = new MegaStation();
			serverSocket = new ServerSocket(portNum);
			
			Runnable serverManager = new ServerFromManager(station);
			Thread threadManager = new Thread(serverManager);
			threadManager.start();
				
			}catch (IOException e) {
			System.out.println("in ServerConnectThread - ServerSocket error");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				socket = serverSocket.accept();
				Runnable serverUser = new ServerFromUser(station, socket);
				Thread threadUser = new Thread(serverUser);
				threadUser.start();
			} catch (IOException e) {
				System.out.println("in ServerConnectThread - serverSocket accept error");
				e.printStackTrace();
			}
		}
	}
}

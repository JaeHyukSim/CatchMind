package Server;

public class Timer implements Runnable{

	private boolean stop;
	private ServerMessageProcessor serverMessageProcessor;
	private RoomData rd;
	private int count;
	
	public Timer(ServerMessageProcessor smp,RoomData rd, int count) {
		System.out.println("Timer constructor count : " + count);
		stop = true;
		this.serverMessageProcessor = smp;
		this.rd = rd;
		this.count = count;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int curCount = count;
		String sendData = "";
		while(curCount > 0) {
			if(stop == false) {
				sendData = "{";
				sendData += ("\"method\":\"TIMEOUT\"");
				sendData += "}";
				if(rd != null) {
					serverMessageProcessor.processingTimer(sendData, rd);
				}
				Thread.interrupted();
				return;
			}
			//틱을 1초마다 준다
			try {
				//틱을 줘야 한다
				Thread.sleep(1000);
				curCount--;
				System.out.println("timer curCount : " + curCount);
				//서버한테 줘야한다
				sendData = "{";
				sendData += "\"method\":\"TIMER\"";
				sendData += ("\"tick\":" + String.valueOf(curCount));
				sendData += "}";
				if(rd == null) {
					Thread.interrupted();
				}else {
					serverMessageProcessor.processingTimer(sendData, rd);
				}
			}catch(Exception e) {
				Thread.interrupted();
			}
		}
		//타이머를 없애버리는 것 -> setValue -> 0
		
		sendData = "{";
		sendData += ("\"method\":\"TIMEEND\"");
		sendData += "}";
		if(rd != null) {
			serverMessageProcessor.processingTimer(sendData, rd);
		}
		Thread.interrupted();
		return;
	}
	public boolean isStop() {
		return stop;
	}
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	
}

package UserForm;

public class QueueExecuteThread implements Runnable{

	private GameRoomForm grf;
	
	public QueueExecuteThread(GameRoomForm grf) {
		this.grf = grf;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(grf.getQue().getSize() != 0) {
				int[] arr = grf.getQue().pop();
				System.out.println("input arr : (" + arr[0] + "," + arr[1] + ")");
				if(arr[0] == 10000) {
					grf.getSketchPanel().initSlidingWindow();
				}else {
					grf.getSketchPanel().draw(arr[0], arr[1]);
				}
			}else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	
}

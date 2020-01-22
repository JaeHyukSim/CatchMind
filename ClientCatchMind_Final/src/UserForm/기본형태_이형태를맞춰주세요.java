package UserForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Client.DisplayThread;
import Client.UserInputThread;
import Client.UserMessageProcessor;

public class 기본형태_이형태를맞춰주세요 implements UserForm{

	///***** 모르는 것 - 질문하셔도 좋고, 제가 만든 LoginFormVer1을 참고하셔도 됩니다! - 똑같은 형태로 적용했습니다!
	
	//1. UserForm 인터페이스를 implements합니다!!!!!
	//2. Override 함수들을 만들어서 에러를 없애줍니다!!!!
	//3. Singleton Pattern을 만들기 위해 uniqueInstance 변수를 만들어 줍니다!!!!!
	private volatile static 기본형태_이형태를맞춰주세요 uniqueInstance;
	//4. 필수 변수들을 선언해 줍니다!!!!!
	private UserMessageProcessor userMessageProcessor;
	private DisplayThread displayThread;
	private Socket socket;
	private UserInputThread unt;
	private JSONParser jsonParser;
	//5. 사용할 JFrame container의 components들과 Thread를 선언해 줍니다!!!!
	private JPanel jpanel;
	private JButton jbutton; // ... Form에 필요한 components객체들을 여기서 선언합니다 - new something을 통한 구현 = 생성자 내부에서
	// ... 
	private Runnable userRunnable;
	private Thread userThread; // ... 데이터 전송을 위한 thread를 선언해 줍니다!!!
	
	//6. 생성자를 만듭니다!!! - private인 것을 주의합시다!!!
	private 기본형태_이형태를맞춰주세요(DisplayThread dt, Socket socket) {
		//7. 여기서 모든 components들을 new를 통해 생성합니다. + 필수적인 변수초기화를 진행합니다.
		this.displayThread = dt;
		this.socket = socket;
		userMessageProcessor = new UserMessageProcessor();
		unt = UserInputThread.getInstance(socket);
		jsonParser = new JSONParser();
		//components들 new 시작!!
		jpanel = new JPanel();
		jbutton = new JButton("테스트 버튼");
		// ... ... 
		//8.  다음과 같이 Thread를 만들어 줍니다
		userRunnable = unt;
		// 나중에 userThread.start(); 라는 메소드를 통해서 쓰레드를 실행해 줍니다 ---- 메시지를 서버에 보낼 ㄸ!!!!
	}
	//9. Singleton pattern의 유일한 instance를 만들기 위해 getInstance()메소드를 만듭니다.
	public static 기본형태_이형태를맞춰주세요 getInstance(DisplayThread dt, Socket socket) {
		if(uniqueInstance == null) {
			synchronized (기본형태_이형태를맞춰주세요.class) {
				if(uniqueInstance == null) {
					uniqueInstance = new 기본형태_이형태를맞춰주세요(dt, socket);
				}
			}
		}
		return uniqueInstance;
	}
	@Override
	public void display() {
		//10. 여기서 생성된 JFrame components들의 배치를 시작합니다! 
		jpanel.setBounds(0, 0, 0, 0);
		jbutton.setBounds(0, 0, 0, 0);
		jpanel.add(jbutton); // ... ...
		// ... ...
		
	}

	@Override
	public void actionPerformMethod() {
		//11. addActionListener 함수, 또는 익명의 클래스를 이곳에서 사용합니다!
		//이 메소드로 캡슐화하는 이유는 서버로 보낼 메시지를 한 메소드로 모아서 가독성을 높이기 위함입니다!!
		//익명의 클래스 사용 예시 ----------------------(강사님께서 알려주신 방법으로 사용해도 무방합니다 - implements Action...)
		jbutton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//12. 이곳에서 JSON형태의 메소드를 제작합니다. (서버로 보내기 전 데이터 포멧을 맞춘다)
				String sendData = "{";
				sendData += userMessageProcessor.getJSONData("method", "1000");	
				sendData += userMessageProcessor.getJSONData("data", "메소드 1000으로 data를 서버에게 보냅니다");
				sendData += "}";
				//13. 데이터를 서버로 보냅니다!
				unt.setInputData(sendData);
				userThread = new Thread(userRunnable);
				userThread.start();
			}
		});
	}

	@Override
	public void operation(String data) {
		//14. 서버로부터 받은 데이터를 처리하는 곳입니다.
		//만약 서버로부터 method가 1002, btnName이 "바뀐 버튼 이름"으로 데이터가 왔을 때!!! - 버튼의 text를 "바뀐 버튼 이름"으로 바꿔봅시다
		try {
			JSONObject jsonObj = (JSONObject)jsonParser.parse(data);
			switch((String)jsonObj.get("method")) {
			case "1002":
				jbutton.setText((String)jsonObj.get("btnName"));
				break;
			//... 수많은 case들
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public JPanel getJPanel() {
		//15. 만든 form의 root - parent JFrame객체의 card layout에 등록할 panel을 리턴하는 메소드입니다
		return jpanel;
	}
	
	//16. 모르는 것 - 질문하셔도 좋고, 제가 만든 LoginFormVer1을 참고하셔도 됩니다! - 똑같은 형태로 적용했습니다!
}

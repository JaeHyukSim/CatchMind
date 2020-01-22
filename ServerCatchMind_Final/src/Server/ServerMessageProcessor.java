package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerMessageProcessor {

	private static int tmp = 1;

	private static volatile ServerMessageProcessor uniqueInstance;

	private Station station;

	private String dataStream;
	private String head;
	private String body;
	private String tail;
	private JSONObject userData;
	private JSONParser jsonParser;

	private RoomData roomData;

	private ArrayList<Observer> waitingUserList;
	private ArrayList<RoomData> roomUserList;

	private String filePath;

	// Room total info
	private final int ROOMSIZE;

	/* graphic algorithm */
	int[][] slidingWindow;
	int slidingWindowPointer;

	private JSONObject json;

	private ServerMessageProcessor() {

		json = new JSONObject();
		slidingWindow = new int[2][2];

		ROOMSIZE = 30;

		jsonParser = new JSONParser();
		filePath = ServerMessageProcessor.class.getResource("").getPath();
		System.out.println("FILEPATH : " + filePath);
		dataStream = "";
		head = "{ \"USER_DATA\" : [";
		tail = "]}";
		getFileData();
	}

	public static ServerMessageProcessor getInstMessageProcessor() {
		if (uniqueInstance == null) {
			synchronized (ServerMessageProcessor.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new ServerMessageProcessor();
				}
			}
		}
		return uniqueInstance;
	}

	public String getDataStream() {
		return dataStream;
	}

	public void setDataStream(String dataStream) {
		this.dataStream = dataStream;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTail() {
		return tail;
	}

	public void setTail(String tail) {
		this.tail = tail;
	}

	// put to variable 'userData' from File
	public void getFileData() {
		File file = new File(filePath + "..\\..\\src\\Resource\\ServerResource\\userData.json");

		try {
			if (isOkFile(file)) {
				BufferedReader br = new BufferedReader(new FileReader(file));

				String str = "";
				String tmp = br.readLine();
				while (tmp != null) {
					str += tmp + '\n';
					tmp = br.readLine();
				}

				dataStream = head + body + tail;
				try {
					userData = (JSONObject) jsonParser.parse(str);
				} catch (ParseException e) {
					System.out.println("ServerMessageProcessor - JSON error : file reading");
					e.printStackTrace();
				}
				br.close();
			} else {
				System.out.println("can't approach file");
			}
		} catch (FileNotFoundException e) {
			System.out.println("ServerMessageProcessor - file not found");
		} catch (IOException e) {
			System.out.println("ServerMessageProcessor - IOException");
		} finally {
		}
	}

	// Save to file
	public void setFileData() {
		File file = new File(filePath + "..\\..\\src\\Resource\\ServerResource\\userData.json");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(userData.toString());
			bw.close();
		} catch (IOException e) {
			System.out.println("ServerMessageProcessor - IOException");
		}
	}

	public boolean isOkFile(File file) {
		if (file.exists()) {
			if (file.isFile() && file.canRead()) {
				return true;
			}
		}
		return false;
	}

	// make json data
	public String getJSONData(String key, String value) {
		String res = "";
		res += "\"" + key + "\"" + ":";
		res += "\"" + value + "\"";
		return res;
	}

	
	public void processingTimer(String data, RoomData rd) {
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(data);
			String sendData = "";
			
			ArrayList<Observer> users;
			Observer user;
			 
			switch(String.valueOf(jsonObj.get("method"))) {
			 case "TIMER": // tick을 받는다
				 users = rd.getUserList();
				 user = rd.getUserList().get(0);
				 for(int i = 0; i < users.size(); i++) {
					 sendData = "{";
					 sendData += getJSONData("method", "3TIMER");
					 sendData += getJSONData("tick", String.valueOf(jsonObj.get("tick")));
					 sendData += "}";
					 user.getStation().unicastObserver(sendData, users.get(i));
				 }
				 return;
			 case "TIMEOUT": // timer 종료
				 users = rd.getUserList();
				 user = rd.getUserList().get(0);
				 for(int i = 0; i < users.size(); i++) {
					 sendData = "{";
					 sendData += getJSONData("method", "3TIMEOUT");
					 sendData += "}";
					 user.getStation().unicastObserver(sendData, users.get(i));
				 }
				 return;
			 case "TIMEEND":	 // timer 끝
				 users = rd.getUserList();
				 user = rd.getUserList().get(0);
				 sendData = messageRoundEnd((ServerFromUser)user);
				 for(int i = 0; i < users.size(); i++) {
					 user.getStation().unicastObserver(sendData, users.get(i));
				 }
				 return;
			}
		}catch(Exception e) {
			System.out.println("ServerMessageProcessor - processingTimer Exception");
		}
	}
	// very important factor : JSON Message Processing
	public String processingServerMessage(String data, ServerFromUser sfu) {
		
		// 필요한것
		// 1. 유저가 속한 방을 알고 싶을 때 ->
		// sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		// 2. *** RoomData rd =
		// sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		// 3. 모든 사람이 레디했는지 알고 싶을 때 : rd.isAllReady() -> boolean을 return
		RoomData rd;
		int index;

		String res = "";
		// 1. data json parsing
		try {
			String sendData = "{";

			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(data);
			JSONArray originalArray = (JSONArray) userData.get("USER_DATA");

			// 2. look the method of data - cope with each method differently
			switch ((String) jsonObj.get("method")) {
			case "1000": // login message
				for (int i = 0; i < originalArray.size(); i++) {
					if (((JSONObject) originalArray.get(i)).get("id").equals(jsonObj.get("id"))) {
						if (((JSONObject) originalArray.get(i)).get("pwd").equals(jsonObj.get("pwd"))) {

							// 1. find id
							if (DoesIDExist(sfu, (String) jsonObj.get("id"))) {
								// 2. already log in
								sendData += getJSONData("method", "1024");
								sendData += "}";
								System.out.println("method:1024" + ",sendData:" + sendData);
								sfu.getStation().unicastObserver(sendData, sfu);
								return sendData;
							}
							// 3. setId
							sfu.setId((String) jsonObj.get("id"));
							// sfu.setLv((String)jsonObj.get("lv"));
							sfu.setState("waiting");

							// 1. both id and password match - login success
							sendData += getJSONData("method", "1002");
							sendData += "," + getJSONData("id", (String) ((JSONObject) originalArray.get(i)).get("id"));
							sendData += "," + getJSONData("lv", (String) ((JSONObject) originalArray.get(i)).get("lv"));
							sendData += ","
									+ getJSONData("exp", (String) ((JSONObject) originalArray.get(i)).get("exp"));
							sendData += "," + getJSONData("ch", (String) ((JSONObject) originalArray.get(i)).get("ch"));
							sendData += "}";

							sfu.setId((String) ((JSONObject) originalArray.get(i)).get("id"));
							sfu.setLv((String) ((JSONObject) originalArray.get(i)).get("lv"));
							sfu.setExp((String) ((JSONObject) originalArray.get(i)).get("exp"));
							sfu.setCh((String) ((JSONObject) originalArray.get(i)).get("ch"));
							System.out.println("method:1002" + ",sendData:" + sendData);
							sfu.getStation().unicastObserver(sendData, sfu);
							return sendData;
						} else {
							// 2. password is wrong - login pwd fail
							sendData += getJSONData("method", "1014");
							sendData += "}";
							System.out.println("method:1014" + ",sendData:" + sendData);
							sfu.getStation().unicastObserver(sendData, sfu);
							return sendData;
						}
					}
				}
				sendData += getJSONData("method", "1004");
				sendData += "}";
				System.out.println("method:1004" + ",sendData:" + sendData);
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "1100": // Check for duplicates
				for (int i = 0; i < originalArray.size(); i++) {
					String dataStreamId = (String) ((JSONObject) originalArray.get(i)).get("id");
					if (dataStreamId.equals(jsonObj.get("id"))) {
						sendData += getJSONData("method", "1104");
						sendData += "}";
						System.out.println("method to : 1104 , sendData : " + sendData);
						sfu.getStation().unicastObserver(sendData, sfu);
						return sendData;
					}
				}
				sendData += getJSONData("method", "1102");
				sendData += "}";
				System.out.println("method to : 1102 , sendData : " + sendData);
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "1200": // ID creation request
				for (int i = 0; i < originalArray.size(); i++) {
					String dataStreamId = (String) ((JSONObject) originalArray.get(i)).get("id");
					if (dataStreamId.equals(jsonObj.get("id"))) {
						sendData += getJSONData("method", "1204");
						sendData += "}";
						System.out.println("method to : 1204 , sendData : " + sendData);
						sfu.getStation().unicastObserver(sendData, sfu);
						return sendData;
					}
				}
				body = "";
				body += "{";
				body += getJSONData("id", (String) jsonObj.get("id"));
				body += "," + getJSONData("pwd", (String) jsonObj.get("pwd"));
				body += "," + getJSONData("lv", "1");
				body += "," + getJSONData("exp", "0");
				body += "," + getJSONData("ch", String.valueOf(jsonObj.get("ch")));
				body += "," + getJSONData("friendList", "[]");
				body += "}";
				dataStream = head + body + tail;
				System.out.println("body : " + body);
				JSONObject addedJSON = (JSONObject) jsonParser.parse(body);
				originalArray.add(addedJSON);
				setFileData();
				sendData += getJSONData("method", "1202");
				sendData += "}";
				System.out.println("method to : 1202, sendData ok");
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "1400": // logout request
				sendData = "{";
				sendData += getJSONData("method", "1402");
				sendData += "}";
				sfu.setId("#");
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "1500": // withdrawal
				json.clear();
				json.put("method", "1502");
				sendData = String.valueOf(json);
				sfu.getStation().unicastObserver(sendData, sfu);
				removeMember(sfu);

				return sendData;
			case "2000":
				////////////////////////////////////////////////////
				sendData = "";
				
				sendData = "{";
				sendData += getJSONData("method", "2500");
				sendData += "," + getJSONData("ID", sfu.getId());
				sendData += "," + getJSONData("EXP", sfu.getExp());
				sendData += "," + getJSONData("LV", sfu.getLv());	
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				
				
				
				System.out.println("id: "+sfu.getId()+", exp: "+sfu.getExp()+", lv:"+sfu.getLv());
	////////////////////////////////////////////////////
				// 들어오니까 1. 대기실 유저 목록에 이 유저를 추가한다.

				sfu.setWaitingList();

				// 1. 클라이언트한테 메시지를 보내야 한다 - 메시지 안에는 1. 대기실 유저 정보 list가 포함된다
				// 2. 방 정보도 보내야돼 - 비어있더라도 보내야대
				// 3. 메소드는 2002 보내면 된다! method, watingUserList[], roomUserList[]

				sendData = "{";
				sendData += getJSONData("method", "2002");
				sendData += "}";

				sfu.getStation().unicastObserver(sendData, sfu);

				// get other user's data
				sendData = getAllListData(sfu);
				// sfu.getStation().unicastObserver(sendData, sfu);

				// and spread my data
				// sfu.setWaitingList();
				// sendData = getOneListDataForWait(sfu);
				sfu.getStation().broadcastObserver(sendData);

				return sendData;
			case "2100":
				System.out.println("get method : 2100");
				sendData += getJSONData("method", "2102");
				sendData += "," + getJSONData("chat", sfu.getId() + " -> " + (String) jsonObj.get("chat"));
				sendData += "}";
				sfu.getStation().broadcastWaitObserver(sendData);
				return sendData;
			case "2070": // remove - find index
				sendData = getAllListData(sfu);
				sfu.getStation().broadcastWaitObserver(sendData);
				return sendData;
			case "2400": // out from waiting room
				sfu.getStation().removeWaitObserver(sfu); // remove user from waiting room
				sendData = getAllListData(sfu); // update all user
				sfu.getStation().broadcastObserver(sendData);

				// next change the card show to login form and init login form
				// first. 1302 -> init login form
				sendData = "{";
				sendData += getJSONData("method", "1302");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				// second. 1312 -> card show
				sendData = "{";
				sendData += getJSONData("method", "1312");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "2600" : 
				String waitUserName;
				waitUserName = (String)jsonObj.get("ID");
	
				ArrayList<Observer> users = sfu.getStation().getWaitUserList();
				
				for(int i = 0 ; i < users.size(); i++) {
						if(waitUserName.equals(users.get(i).getId()))
						{
							sendData = "{";
							sendData += getJSONData("method", "2602");
							sendData += "," + getJSONData("ID", users.get(i).getId());
							sendData += "," + getJSONData("Exp", users.get(i).getExp());
							sendData += "," + getJSONData("Lv", users.get(i).getLv());
							sendData += "," + getJSONData("Ch", users.get(i).getCh());
							sendData += "}";
							System.out.println("sendData in 2602: "+sendData);
							break;
						}
				}
				sfu.getStation().unicastObserver(sendData, sfu);
				return sendData;
			case "3000": // create room
				System.out.println("get method : 3000");

				// full room
				if (sfu.getStation().getRoomUserList().size() >= ROOMSIZE) {
					sendData += getJSONData("method", "2224");
					sendData += "}";
					System.out.println("room is full - method to 2224");
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
				}

				//create room
				roomData = new RoomData();
				roomData.setNameOfRoom((String)jsonObj.get("roomCreate"));
				roomData.setRoomPass((String)jsonObj.get("roomPwd"));
				roomData.setRoomPassState((String)jsonObj.get("roomPassState"));
				roomData.setCountOfMaximumUser((String)jsonObj.get("countOfMaximumUser"));
				roomData.setIdOfMasterUser(sfu.getId());
				
				if(roomData.getRoomPass().equals("")) {
					roomData.setRoomPassState("0");
				}else {
					roomData.setRoomPassState("1");
				}
	
				roomData.addUserList(sfu);
				

				// make room ID
				for (int i = 1; i <= ROOMSIZE; i++) {
					index = sfu.getStation().findRoomObserver(String.valueOf(i));
					if (index == -1) {
						roomData.setNumberOfRoom(String.valueOf(i));
						sfu.setRoomId(String.valueOf(i));
						System.out.println("room number : " + i);
						break;
					}
				}

				sfu.getStation().registerRoomObserver(roomData);
				sfu.getStation().removeWaitObserver(sfu);
				// 방 만들면,
				// 1. dialog 제거
				sendData += getJSONData("method", "2052");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);

				sendData = "{";
				sendData += getJSONData("method", "3002");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);

				// 3. wait room data init - for other user
				sendData = getAllListData(sfu);
				sfu.getStation().broadcastWaitObserver(sendData);

				// 4. room register!
				sfu.setCnt("0");
				sendData = getAllGameData(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

				// 5. Notice who is master
				sendData = messageInitReady(sfu);
				sfu.getStation().unicastObserver(sendData, sfu);
				sendData = messageForMaster(sfu);
				sfu.getStation().unicastObserver(sendData, sfu); // have to set text
				sendData = messageIAmMaster(sfu);
				index = 0;
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				for (int i = 0; i < rd.getUserList().size(); i++) {
					if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
						index = i;
						break;
					}
				}

				sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
				return sendData;

			case "3010": // get room enter!
				index = Integer.parseInt((String) jsonObj.get("room"));
				if (index == -1) {
					return sendData;
				}
				rd = sfu.getStation().getRoomUserList().get(index);
				// now game room state : gaming - can't approach this room
				if (rd.getRoomState().equals("gaming")) {
					json.clear();
					json.put("method", "2904");
					sendData = String.valueOf(json);
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
				}
				System.out.println("room id : " + rd.getNumberOfRoom());
				if (Integer.parseInt(rd.getCountOfCurrentUser()) >= Integer.parseInt(rd.getCountOfMaximumUser())) {
					// room is full
					sendData = "{";
					sendData += getJSONData("method", "3014"); // reject enter to room
					sendData += "}";
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
				}
				// if passward state is existed ...
				if (rd.getRoomPassState().equals("0") == false) {
					// 비밀번호를 검사해야겠죠?
					if (!(rd.getRoomPass().equals((String) jsonObj.get("pass")))) // 입력한 비밀번호가 맞다면,
					{
						sendData = "{";
						sendData += getJSONData("method", "2113");
						sendData += "}";
						sfu.getStation().unicastObserver(sendData, sfu);
						System.out.println("wrongPwd: " + sendData);
						return sendData;
					}

				}
				// 1. set Visible off
				sendData = "{";
				sendData += getJSONData("method", "2112");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				System.out.println("equalPwd" + sendData);
				// 1. waiting room set
				sfu.getStation().removeWaitObserver(sfu);
				// 2. go into room
				sendData = "{";
				sendData += getJSONData("method", "3002");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				// 3. find room and register
				rd.addUserList(sfu);
				sfu.setRoomId(rd.getNumberOfRoom());
				sfu.setReadyState("0");
				// 4. init all thing
				sendData = getAllListData(sfu);
				sfu.getStation().broadcastWaitObserver(sendData);
				sfu.setCnt("0");
				sendData = getAllGameData(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, rd.getNumberOfRoom());

				// 5. Notice room state
				sendData = messageForMaster(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

				sendData = messageIAmMaster(sfu);
				index = 0;
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				for (int i = 0; i < rd.getUserList().size(); i++) {
					if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
						index = i;
						break;
					}
				}

				sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
				sendData = messageAllReady(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				return sendData;
			
			case "3090": // get room enter!
				int id = Integer.parseInt((String) jsonObj.get("room"));
				index = sfu.getStation().findRoomObserver(String.valueOf(id));
				
				if(index== -1)
				{
					// : 찾는 방이 없을때
					sendData = "{";
					sendData += getJSONData("method", "2870");
					sendData += "}";
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
					
				}
				
				rd = sfu.getStation().getRoomUserList().get(index);
				System.out.println("room id : " + rd.getNumberOfRoom());
				if (Integer.parseInt(rd.getCountOfCurrentUser()) >= Integer.parseInt(rd.getCountOfMaximumUser())) {
					// room is full
					sendData = "{";
					sendData += getJSONData("method", "3014"); // reject enter to room
					sendData += "}";
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
				}
				// if passward state is existed ...
				if (rd.getRoomPassState().equals("0") == false) {
					// 비밀번호를 검사해야겠죠?
					if (!(rd.getRoomPass().equals((String) jsonObj.get("pass")))) // 입력한 비밀번호가 맞다면,
					{
						sendData = "{";
						sendData += getJSONData("method", "2113");
						sendData += "}";
						sfu.getStation().unicastObserver(sendData, sfu);
						System.out.println("wrongPwd: " + sendData);
						return sendData;
					}

				}
				// 1. set Visible off
				sendData = "{";
				sendData += getJSONData("method", "2112");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				System.out.println("equalPwd" + sendData);
				// 1. waiting room set
				sfu.getStation().removeWaitObserver(sfu);
				// 2. go into room
				sendData = "{";
				sendData += getJSONData("method", "3002");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);
				// 3. find room and register
				rd.addUserList(sfu);
				sfu.setRoomId(rd.getNumberOfRoom());
				sfu.setReadyState("0");
				// 4. init all thing
				sendData = getAllListData(sfu);
				sfu.getStation().broadcastWaitObserver(sendData);
				sfu.setCnt("0");
				sendData = getAllGameData(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, rd.getNumberOfRoom());

				// 5. Notice room state
				sendData = messageForMaster(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

				sendData = messageIAmMaster(sfu);
				index = 0;
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				for (int i = 0; i < rd.getUserList().size(); i++) {
					if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
						index = i;
						break;
					}
				}

				sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
				sendData = messageAllReady(sfu);
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				return sendData;
			case "3400": // init room
				// 해당 방은 sfu.getStation().getRoom
				ArrayList<RoomData> al = sfu.getStation().getRoomUserList();
				System.out.println("al size : " + al.size());
				for (int i = 0; i < al.size(); i++) {
					System.out.println("sfu.getRoomId() : " + sfu.getRoomId());
					System.out.println("al.get(i).getNumberOfRoom() : " + al.get(i).getNumberOfRoom());
					if (sfu.getRoomId().equals(al.get(i).getNumberOfRoom())) {
						// 이 데이터를 전송해야 한다. -> RoomData를 가지고 있다 (유저목록만 가져오자)
						ArrayList<Observer> usr = al.get(i).getUserList();

						sendData += getJSONData("method", "3402");
						sendData += ",";
						sendData += "\"userList\":[";
						for (int j = 0; j < usr.size(); j++) {
							sendData += "{";
							sendData += getJSONData("id", usr.get(i).getId());
							sendData += "," + getJSONData("lv", usr.get(i).getLv());
							sendData += "," + getJSONData("ch", usr.get(i).getCh());
							sendData += "," + getJSONData("exp", usr.get(i).getExp());
							sendData += "}";
							if (j != usr.size() - 1) {
								sendData += ",";
							}
						}
						sendData += "]}";
						break;

					}
				}
				sfu.getStation().unicastObserver(sendData, sfu);
				sendData += getJSONData("method", "3402 : data : " + sendData);
				return sendData;
			case "3600": // get out from room
			/////////////////////////////////////////////////////////////////
			sendData = "{";
			sendData += getJSONData("method", "2500");
			sendData += "," + getJSONData("ID", sfu.getId());
			sendData += "," + getJSONData("EXP", sfu.getExp());
			sendData += "," + getJSONData("LV", sfu.getLv());	
			sendData += "}";
			sfu.getStation().unicastObserver(sendData, sfu);
			sendData = "";
			
			
			/////////////////////////////////////////////////////////////////
				boolean isExistRoom = true;
				// 1. request -> 2. remove from room(find room and remove usr)
				// 3. register to waiting room 4. init user's waiting room 5. update other usr

				index = sfu.getStation().findRoomObserver(sfu.getRoomId());
				rd = sfu.getStation().getRoomUserList().get(index);
				if (sfu.getStation().getRoomUserList().get(index).getCountOfCurrentUser().equals("1")) {
					isExistRoom = false;
				}
				sfu.getStation().registerWaitObserver(sfu); // register
				sfu.getStation().removeRoomObserverTarget(sfu.getRoomId(), sfu);

				// first. card change and init chat
				sendData = "{";
				sendData += getJSONData("method", "2002");
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, sfu);

				// init waitroom for me -> chat clear
				// broadcast data to all (wait)
				sendData = getAllListData(sfu);
				sfu.getStation().broadcastWaitObserver(sendData);

				// broadcast data to all (room) 1. find room 2. broadcast
				if (isExistRoom == true) {

					sendData = getAllGameData(sfu);
					sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

					if (rd.getRoomState().equals("gaming")) {
						// 1. the people getting out is a master

						if (rd.getIdOfMasterUser().equals(sfu.getId())) {
							rd.setIdOfMasterUser(rd.getUserList().get(0).getId());
						}
						////////////////////////////////////////////////////
						ServerFromUser man = (ServerFromUser) rd.getUserList().get(0);
						////////////////////////////////////////////////////
						// 3. rest people count == 1
						if (rd.getCountOfCurrentUser().equals("1")) {
							// game over
							messageGameEnd(man);
						} else {
							sendData = messageRoundEnd(man);
							sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						}
					}
					// if the man is a master - waiting state
					if (rd.getRoomState().equals("waiting")) {
						if (rd.getIdOfMasterUser().equals(sfu.getId())) {
							System.out.println("out master");
							rd.setIdOfMasterUser(rd.getUserList().get(0).getId());
							// sendData who is master? + init all ready state
							sendData = messageInitReady(sfu);
							sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						}
						sendData = messageForMaster(sfu);
						sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						sendData = messageIAmMaster(sfu);
						index = 0;
						for (int i = 0; i < rd.getUserList().size(); i++) {
							if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
								index = i;
								break;
							}
						}
						sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
						sendData = messageAllReady(sfu);
						sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
					}
				}
				sfu.setRoomId("0");
				if (sfu.getReadyState().equals("1")) {
					sfu.setReadyState("0");
				}
				return sendData;
				
			case "3601": // get out from room - compelled
					isExistRoom = true;
					// 1. request -> 2. remove from room(find room and remove usr)
					// 3. register to waiting room 4. init user's waiting room 5. update other usr

					index = sfu.getStation().findRoomObserver(sfu.getRoomId());
					rd = sfu.getStation().getRoomUserList().get(index);
					if (sfu.getStation().getRoomUserList().get(index).getCountOfCurrentUser().equals("1")) {
						isExistRoom = false;
					}
					sfu.getStation().removeRoomObserverTarget(sfu.getRoomId(), sfu);

					// init waitroom for me -> chat clear
					// broadcast data to all (wait)
					sendData = getAllListData(sfu);
					sfu.getStation().broadcastWaitObserver(sendData);

					// broadcast data to all (room) 1. find room 2. broadcast
					if (isExistRoom == true) {

						sendData = getAllGameData(sfu);
						sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

						if (rd.getRoomState().equals("gaming")) {
							// 1. the people getting out is a master

							if (rd.getIdOfMasterUser().equals(sfu.getId())) {
								rd.setIdOfMasterUser(rd.getUserList().get(0).getId());
							}
							////////////////////////////////////////////////////
							ServerFromUser man = (ServerFromUser) rd.getUserList().get(0);
							////////////////////////////////////////////////////
							// 3. rest people count == 1
							if (rd.getCountOfCurrentUser().equals("1")) {
								// game over
								messageGameEnd(man);
							} else {
								sendData = messageRoundEnd(man);
								sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
							}
						}
						// if the man is a master - waiting state
						if (rd.getRoomState().equals("waiting")) {
							if (rd.getIdOfMasterUser().equals(sfu.getId())) {
								System.out.println("out master");
								rd.setIdOfMasterUser(rd.getUserList().get(0).getId());
								// sendData who is master? + init all ready state
								sendData = messageInitReady(sfu);
								sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
							}
							sendData = messageForMaster(sfu);
							sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
							sendData = messageIAmMaster(sfu);
							index = 0;
							for (int i = 0; i < rd.getUserList().size(); i++) {
								if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
									index = i;
									break;
								}
							}
							sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
							sendData = messageAllReady(sfu);
							sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						}
					}
					return sendData;
			case "3700": // DRAW - get draw coordinate - x, y
				// 1. don't use graphic algorithm

				// Gaming && not examiner -> can't draw
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				if (rd.getRoomState().equals("gaming")) {
					if (!sfu.getId().equals(rd.getUserList().get(Integer.parseInt(rd.getExaminer())).getId())) {
						return sendData;
					}
				}

				sendData = "{";
				sendData += getJSONData("method", "3702");
				sendData += "," + getJSONData("x", (String) jsonObj.get("x"));
				sendData += "," + getJSONData("y", (String) jsonObj.get("y"));
				sendData += "," + getJSONData("color", (String) jsonObj.get("color"));
				sendData += "," + getJSONData("brushSize", (String) jsonObj.get("brushSize"));
				sendData += "}";
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				return sendData;
			case "3710": // DRAW - clear all

				// Gaming && not examiner -> can't draw
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				if (rd.getRoomState().equals("gaming")) {
					if (!sfu.getId().equals(rd.getUserList().get(Integer.parseInt(rd.getExaminer())).getId())) {
						return sendData;
					}
				}

				sendData = "{";
				sendData += getJSONData("method", "3712");
				sendData += "," + getJSONData("color", (String) jsonObj.get("color"));
				sendData += "}";
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				return sendData;
			case "3720": // init sliding window
				sendData = "{";
				sendData += getJSONData("method", "3722");
				sendData += "}";
				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				return sendData;
			case "3309": // game room chat

				// Gaming && not examiner -> can't draw
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				System.out.println("3309 : examiner index : " + rd.getExaminer());
				if (rd.getRoomState().equals("gaming")) {
					if (sfu.getId().equals(rd.getUserList().get(Integer.parseInt(rd.getExaminer())).getId())) {
						return sendData;
					}
					if (String.valueOf(jsonObj.get("message"))
							.equals(rd.getAnswerList()[Integer.parseInt(rd.getCurrentRound()) - 1])) {
						if (rd.getIsAnswer().equals("true")) {
							System.out.println("answer ok!");
							sfu.setCnt(String.valueOf(Integer.parseInt(sfu.getCnt()) + 1));
							sendData = getAllGameData(sfu);
							sfu.getStation().broadcastRoomObserver(sendData, rd.getNumberOfRoom());
							sendData = messageRoundEnd(sfu);
							sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						}
					}
				}

				// rd =
				// sfu.getStation().getRoomUserList().get(sfu.getStation().findRoomObserver(sfu.getRoomId()));
				index = -1;
				for (int i = 0; i < rd.getUserList().size(); i++) {
					if (rd.getUserList().get(i).equals(sfu)) {
						index = i;
					}
				}
				if (index == -1) {
					return sendData;
				}
				sendData = "{";
				sendData += getJSONData("method", "3300");
				sendData += "," + getJSONData("message", String.valueOf(jsonObj.get("message")));
				sendData += "," + getJSONData("userNum", String.valueOf(index));
				sendData += "}";

				sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

				return sendData;
			case "3900": // ready
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				if (rd.getRoomState().equals("gaming")) {
					return sendData;
				}

				if (sfu.getId().equals(rd.getIdOfMasterUser())) {
					if (rd.isAllReady() == true) {
						// 1. init timer 2. generate 10 answers 3. change room state
						// 1. init timer method
						// 2. generate 10 answers method
						// 3. change room state method
						// 4. set method : 3922
						System.out.println("GameStart");
						sendData = messageGameStart(sfu);
						sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
						// 5. round start method
						sendData = messageRoundStart(sfu); // broadcast ok...
						sendData = getAllListData(sfu);
						sfu.getStation().broadcastWaitObserver(sendData);

					} else {
						json.clear();
						json.put("method", "3914");
						sendData = String.valueOf(json);
						sfu.getStation().unicastObserver(sendData, sfu);
					}
				} else {
					sendData = messageReady(sfu);
					sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
				}
				System.out.println("currentUser : " + rd.getCountOfCurrentUser());
				System.out.println("readyUser : " + rd.getCountOfReadyUser());
				return sendData;

			/////////////////////////
			case "3910": // invite Gameroom!

				// Observer invitedUser;
				rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
				if(rd.getRoomState().equals("gaming")) {
					return sendData;
				}
				sendData = messageWaitroomList(sfu); // 대기실에 있는 대기자

				sfu.getStation().unicastObserver(sendData, sfu); // 데이터 , 서버한테 요청한 자신한테 데이터 보냄
				System.out.println("3910 sendData : " + sendData);
				return sendData;
			case "3960":
				// 초대메시지가 거절되었을경우 초대자에게 거절 확인 메시지 보내기위함!

				Observer man = null;
				index = sfu.getStation().findRoomObserver(String.valueOf(jsonObj.get("roomId")));
				if (index == -1) { // 방 자체가 없을 경우{
					return sendData;
				}
				rd = sfu.getStation().getRoomUserList().get(index); // 방이 있는 경우, 해당 방 자체를 rd에 저장하겠다.

				//////////////////////// index 새로 시작
				index = -1;
				for (int i = 0; i < rd.getUserList().size(); i++) {
					if (rd.getUserList().get(i).getId().equals(String.valueOf(jsonObj.get("id")))) {
						// 찾은겁니다.
						index = i;

						man = rd.getUserList().get(index);
						// 데이터를 보내겠습니다.man = rd.getUserList().get(index);
						sendData = "{";
						sendData += getJSONData("method", "3970");
						sendData += "," + getJSONData("id", String.valueOf(jsonObj.get("id")));
						sendData += "}";
						// 클라이언트로 전달하면 됩니다
						System.out.println("3960 sendData : " + sendData);
						sfu.getStation().unicastObserver(sendData, man);
						break;
					}
				}
				// 방에서 A를 찾죠 id로 -> 없으면 , 이 사람은 해당 방에서 나간겁니다.
				return sendData;

			case "3990":
				// 1. 서버로 보내진 메시지 : 1. 웨이팅 룸의 한 id
				// 2. 해야될 일 : 찾아야죠!? - waiting room과 비교 일치하면 보내고, 못찾으면 다시 메시지를 돌려보내는데 (3930)
				// 3. 먼저, 저희 리스트를 구해야 for문을 돌리겠죠 - waitUserList : sfu.getStaion().getWait...
				String inviteId = String.valueOf(jsonObj.get("id"));
				index = -1;
				ArrayList<Observer> user = sfu.getStation().getWaitUserList();
				for (int i = 0; i < user.size(); i++) {
					if (inviteId.equals(user.get(i).getId())) {
						// 찾은거죠
						index = i;
						break;
					}
				}
				// 못찾으면 : index = -1;
				if (index == -1) {
					sendData = "{";
					sendData += getJSONData("method", "3930");
					sendData += "," + getJSONData("id", inviteId);
					sendData += "}";
					sfu.getStation().unicastObserver(sendData, sfu);
					return sendData;
						
				}
				// 찾았을 경우 : - B한테 메시지가 가야겠죠? 3940

				sendData = "{";
				sendData += getJSONData("method", "2940");
				sendData += "," + getJSONData("id", sfu.getId());
				sendData += "," + getJSONData("roomId", sfu.getRoomId());
				sendData += "}";
				sfu.getStation().unicastObserver(sendData, user.get(index));
				return sendData;
				
			case "TIMER": // tick인 숫자르 받았을 경우 예를 들어 118
				
				String tick = String.valueOf(jsonObj.get("tick"));
				String roomId = String.valueOf(jsonObj.get("roomId"));
				
				sendData = "{";
				sendData += getJSONData("method", "3TIMERSTART");
				
			}

			// 3. Make data in the form of transmission
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return res;
	}

	public boolean DoesIDExist(ServerFromUser sfu, String id) {
		ArrayList<Observer> userList = sfu.getUserList();

		System.out.println("userList size : " + userList.size());

		for (int i = 0; i < userList.size(); i++) {

			System.out.println("userList inside");

			if (userList.get(i).getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	public int getRemoveUserDataForWait(ServerFromUser sfu) {
		// 1. sfu의 index를 찾고, 제거
		ArrayList<Observer> wl = sfu.getStation().getWaitUserList();
		int index = -1;

		for (int i = 0; i < wl.size(); i++) {
			if (sfu.getId().equals(wl.get(i).getId())) {
				index = i;
			}
		}
		sfu.getStation().removeWaitObserver(sfu);
		return index;
	}

	public String getOneListDataForWait(ServerFromUser sfu) {

		int index = sfu.getStation().findRoomObserver(sfu.getId());
		RoomData rd = sfu.getStation().getRoomUserList().get(index);
		String sendData = "{";
		sendData += getJSONData("method", "2072");

		sendData += "," + "\"waitingUserList\":";
		sendData += "{";
		sendData += getJSONData("id", sfu.getId());
		sendData += "," + getJSONData("lv", sfu.getLv());
		sendData += "," + getJSONData("state", sfu.getState());
		sendData += "}";

		sendData += ",\"roomUserList\":";
		sendData += "{";
		sendData += getJSONData("roomId", rd.getNumberOfRoom());
		sendData += "," + getJSONData("roomName", rd.getNameOfRoom());
		sendData += "," + getJSONData("roomPassState", rd.getRoomPassState());
//		sendData += "," + getJSONData("roomPass", rd.getRoomPass());
		sendData += "," + getJSONData("roomMaxUser", rd.getCountOfMaximumUser());
		sendData += "," + getJSONData("roomCurUser", rd.getCountOfCurrentUser());
		sendData += "," + getJSONData("roomState", rd.getRoomState());
		sendData += "}}";
		return sendData;
	}

	// make message for all user
	public String getWaitMessage(ServerFromUser sfu) {
		String sendData = "{";
		sendData += getJSONData("method", "2072");

		sendData += "," + "\"waitingUserList\":";
		sendData += "{";
		sendData += getJSONData("id", sfu.getId());
		sendData += "," + getJSONData("lv", sfu.getLv());
		sendData += "," + getJSONData("state", sfu.getState());
		sendData += "}";

		return sendData;
	}

	// make message for one user
	public String getWaitListMessage(ServerFromUser sfu) {
		waitingUserList = sfu.getWaitingList();
		String sendData = "{";
		sendData += getJSONData("method", "2062");
		sendData += "," + "\"waitingUserList\":[";

		for (int i = 0; i < waitingUserList.size(); i++) {
			sendData += "{";
			sendData += getJSONData("id", waitingUserList.get(i).getId());
			sendData += "," + getJSONData("lv", waitingUserList.get(i).getLv());
			sendData += "," + getJSONData("state", waitingUserList.get(i).getState());
			sendData += "}";
			if (i != waitingUserList.size() - 1) {
				sendData += ",";
			}
		}
		return sendData;
	}

	public String getAllListData(ServerFromUser sfu) {
		waitingUserList = sfu.getWaitingList();
		String sendData = "{";
		sendData += getJSONData("method", "2062");
		sendData += "," + "\"waitingUserList\":[";

		for (int i = 0; i < waitingUserList.size(); i++) {
			sendData += "{";
			sendData += getJSONData("id", waitingUserList.get(i).getId());
			sendData += "," + getJSONData("lv", waitingUserList.get(i).getLv());
			sendData += "," + getJSONData("state", waitingUserList.get(i).getState());
			sendData += "}";
			if (i != waitingUserList.size() - 1) {
				sendData += ",";
			}
		}
		// 방정보 : 1. 방 제목, 2. 방 id. 3. 비밀번호 여부, 4. 비밀번호, 5. 총 인원, 6. 현재 인원, 7. 게임 상태
		roomUserList = sfu.getRoomList();
		sendData += "],\"roomUserList\":[";
		for (int i = 0; i < roomUserList.size(); i++) {
			sendData += "{";
			sendData += getJSONData("roomId", roomUserList.get(i).getNumberOfRoom());
			sendData += "," + getJSONData("roomName", roomUserList.get(i).getNameOfRoom());
			sendData += "," + getJSONData("roomPassState", roomUserList.get(i).getRoomPassState());
	//		sendData += "," + getJSONData("roomPass", roomUserList.get(i).getRoomPass());
			sendData += "," + getJSONData("roomMaxUser", roomUserList.get(i).getCountOfMaximumUser());
			sendData += "," + getJSONData("roomCurUser", roomUserList.get(i).getCountOfCurrentUser());
			sendData += "," + getJSONData("roomState", roomUserList.get(i).getRoomState());
			sendData += "}";
			if (i != waitingUserList.size() - 1) {
				sendData += ",";
			}
		}
		sendData += "]}";
		return sendData;
	}

	// 3402
	public String getAllGameData(ServerFromUser sfu) {
		String sendData = "{";
		int index = sfu.getStation().findRoomObserver(sfu.getRoomId());
		if (index == -1) {
			return "NO";
		}
		RoomData rd = sfu.getStation().getRoomUserList().get(index);
		ArrayList<Observer> usr = rd.getUserList();

		sendData += getJSONData("method", "3402");
		sendData += ",";
		sendData += "\"userList\":[";
		for (int i = 0; i < usr.size(); i++) {
			sendData += "{";
			sendData += getJSONData("id", usr.get(i).getId());
			sendData += "," + getJSONData("lv", usr.get(i).getLv());
			sendData += "," + getJSONData("ch", usr.get(i).getCh());
			sendData += "," + getJSONData("exp", usr.get(i).getExp());
			sendData += "," + getJSONData("cnt", usr.get(i).getCnt());
			sendData += "}";
			if (i != usr.size() - 1) {
				sendData += ",";
			}
		}
		sendData += "]}";
		return sendData;
	}

	// who is master?
	public String messageForMaster(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		int index = 0;
		for (int i = 0; i < rd.getUserList().size(); i++) {
			if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
				index = i;
				break;
			}
		}
		String sendData = "{";
		sendData += getJSONData("method", "3902");
		sendData += "," + getJSONData("master", String.valueOf(index));
		sendData += "}";
		return sendData;

	}

	// I am a master
	public String messageIAmMaster(ServerFromUser sfu) {
		String sendData = "{";
		sendData += getJSONData("method", "3912");
		sendData += "}";
		return sendData;
	}

	// init all ready state
	public String messageInitReady(ServerFromUser sfu) {
		// 1. init ready
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		for (int i = 0; i < rd.getUserList().size(); i++) {
			rd.getUserList().get(i).setReadyState("0");
		}
		rd.setCountOfReadyUser("0");

		json.clear();
		json.put("method", "3903");
		return String.valueOf(json);
	}

	// ready now - me
	public String messageReady(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		int index = 0;
		for (int i = 0; i < rd.getUserList().size(); i++) {
			if (sfu.getId().equals(rd.getUserList().get(i).getId())) {
				index = i;
				break;
			}
		}
		json.clear();
		json.put("id", String.valueOf(index));
		// 1. my ready state init
		// 1.1 i was ready
		if (sfu.getReadyState().equals("1")) {
			sfu.setReadyState("0");
			rd.setCountOfReadyUser(String.valueOf(Integer.parseInt(rd.getCountOfReadyUser()) - 1));
			json.put("method", "3904");
		} else {
			sfu.setReadyState("1");
			rd.setCountOfReadyUser(String.valueOf(Integer.parseInt(rd.getCountOfReadyUser()) + 1));
			json.put("method", "3905");
		}
		return String.valueOf(json);
	}

	// Notice who are ready
	public String messageAllReady(ServerFromUser sfu) {
		boolean isOperation = false;
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		String sendData = "{";
		sendData += getJSONData("method", "3906");
		sendData += "," + "\"readyUserList\":[";
		for (int i = 0; i < rd.getUserList().size(); i++) {
			if (rd.getUserList().get(i).getReadyState().equals("1")) {
				if (isOperation == true) {
					sendData += ",";
				}
				isOperation = true;
				sendData += "{";
				sendData += getJSONData("id", String.valueOf(i));
				sendData += "}";
			}
		}
		sendData += "]}";
		return sendData;
	}

	// remove member method
	public void removeMember(ServerFromUser sfu) {
		// 1. remove from file
		JSONArray jsonArr = (JSONArray) (userData.get("USER_DATA"));
		for (int i = 0; i < jsonArr.size(); i++) {
			if (((JSONObject) jsonArr.get(i)).get("id").equals(sfu.getId())) {
				jsonArr.remove(i);
				break;
			}
		}
		setFileData();

		// 2. remove from userList
		sfu.setId("#");
	}

	public void setRandomAnswers(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		boolean isOk = false;
		String pickedAns = "";

		// 1. DB에서 가져온다

		String[] ans = { "사람", "하늘", "코끼리", "바다사자", "연필", "남준우", "지우개", "개새", "메가커피", "울트라바이올렛", "두꺼비", "다람쥐", "제주도",
				"심재혁", "캐치마인드", "도끼", "소련", "소련여자", "코피", "찻집", "학원", "낫", "물", "갓", "해", "지하철", "경찰서", "파파라치", "광개토태왕",
				"기상캐스터", "바르셀로나", "레알마드리드", "스포츠", "자전거", "야구", "축구", "핸드폰", "돈까스", "라면", "우동", "막국수", "피겨스케이팅",
				"꾸러기수비대", "그것이알고싶다", "로미오와줄리엣", "갈릴레오갈릴레이", "레오나르도다빈치" };

		/*
		 * String[] ans = {"일이삼사오륙칠구","일삼삼사오륙칠팔","일이이사오륙칠팔","일이육사오륙칠팔","이이삼사오륙칠팔",
		 * "일이삼갑오륙칠팔","일이을사오륙칠팔","일이삼병오륙칠팔","일이삼사정륙칠팔","일욱삼사오륙칠팔" };
		 */
		// 2. 랜덤으로 숫자를 10개 뽑는다.
		for (int i = 0; i < 10; i++) {
			isOk = true;
			while (isOk) {
				isOk = false;
				pickedAns = ans[(int) (Math.random() * ans.length)];
				for (int j = 0; j < i; j++) {
					if (rd.getAnswerList()[j].equals(pickedAns)) {
						isOk = true;
						break;
					}
				}
			}
			rd.getAnswerList()[i] = pickedAns;
		}
	}

	public void changeRoomState(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		if (rd.getRoomState().equals("waiting")) {
			rd.setRoomState("gaming");
		} else {
			rd.setRoomState("waiting");
		}
	}

	public void initRoomRount(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setCurrentRound("0");
	}

	public void nextRoomRound(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setCurrentRound(String.valueOf(Integer.parseInt(rd.getCurrentRound()) + 1));
	}

	public void initExaminer(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setExaminer("0");
	}

	public String setExaminer(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setExaminer(String
				.valueOf((Integer.parseInt(rd.getExaminer()) + 1) % Integer.parseInt(rd.getCountOfCurrentUser())));
		String index = "";
		index = rd.getExaminer();
		return index;
	}

	// 1. Game Start Method
	public String messageGameStart(ServerFromUser sfu) {
		json.clear();
		// 1. init timer
		// 2. init answer
		setRandomAnswers(sfu);
		// 3. init room state
		changeRoomState(sfu);
		json.put("method", "3922");

		initRoomRount(sfu);
		initExaminer(sfu);
		return String.valueOf(json);
	}

	// 2. Round Start Method
	public String messageRoundStart(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setIsAnswer("true");
		json.clear();
		String index = setExaminer(sfu);
		json.put("method", "3932");
		json.put("examiner", index);
		nextRoomRound(sfu);
		json.put("round", rd.getCurrentRound());

		String tmpAns = "";
		for (int i = 0; i < rd.getAnswerList()[Integer.parseInt(rd.getCurrentRound()) - 1].length(); i++) {
			tmpAns += "●";
		}
		ArrayList<Observer> user = rd.getUserList();
		for (int i = 0; i < user.size(); i++) {
			if (user.get(i).getId().equals(user.get(Integer.parseInt(rd.getExaminer())).getId())) {
				json.put("ans", rd.getAnswerList()[Integer.parseInt(rd.getCurrentRound()) - 1]);
				json.put("you", "yes");
				sfu.getStation().unicastObserver(String.valueOf(json), user.get(i));
			} else {
				json.put("ans", tmpAns);
				json.put("you", "no");
				sfu.getStation().unicastObserver(String.valueOf(json), user.get(i));
			}
		}
		
		rd.setTimer(new Timer(this,rd, 100));
		Thread t = new Thread(rd.getTimer());
		t.start();
		
		return String.valueOf(json);
	}

	// 3. Round End Method - when : 1. when out examiner 2. catch answer 3. timer
	// over
	public String messageRoundEnd(ServerFromUser sfu) {
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		rd.setIsAnswer("false");
		// 1. init timer
		json.clear();
		json.put("ans", rd.getAnswerList()[Integer.parseInt(rd.getCurrentRound()) - 1]);
		json.put("method", "3942");

		Runnable r = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (rd.getCurrentRound().equals(rd.getMaxRound())) {
						System.out.println("game end!");
						messageGameEnd(sfu);
						Thread.interrupted();
					} else {
						messageRoundStart(sfu);
						Thread.interrupted();
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.start();

		if(rd.getTimer() != null) {
			rd.getTimer().setStop(false);
		}
		return String.valueOf(json);
	}

	// Game End Method
	public String messageGameEnd(ServerFromUser sfu) {
		// 1. change room state
		RoomData rd = sfu.getStation().findRoomObserver_RoomData(sfu.getRoomId());
		changeRoomState(sfu);
		String sendData = getAllListData(sfu);
		sfu.getStation().broadcastWaitObserver(sendData);
		// round, answer, cnt 초기화
		ArrayList<Observer> users = rd.getUserList();
		for (int i = 0; i < users.size(); i++) {
			users.get(i).setCnt("0");
		}
		sendData = getAllGameData(sfu);
		sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());

		sendData = messageInitReady(sfu);
		sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
		sendData = messageForMaster(sfu);
		sfu.getStation().broadcastRoomObserver(sendData, sfu.getRoomId());
		sendData = messageIAmMaster(sfu);
		int index = 0;
		for (int i = 0; i < rd.getUserList().size(); i++) {
			if (rd.getIdOfMasterUser().equals(rd.getUserList().get(i).getId())) {
				index = i;
				break;
			}
		}
		sfu.getStation().unicastObserver(sendData, rd.getUserList().get(index));
		
		if(rd.getTimer() != null) {
			rd.getTimer().setStop(false);
		}
		return sendData;
	}

	// 메소드의 목적 -> 저희가 대기실에 있는 사람들을 메시지로 만드는 메소드
	public String messageWaitroomList(ServerFromUser sfu) {
		String sendData = "";

		ArrayList<Observer> user = sfu.getStation().getWaitUserList(); // 배열로 대기자 보내줌
		// 1. 제이슨 형태기 때문에 {
		sendData = "{"; // 시작

		// 2. 모든 유저 데이터를 불러와야 되는데,
		sendData += getJSONData("method", "3920"); // 서버에서 보내는 메소드 이름( 메소드키:키값
		sendData += ",";
		sendData += "\"waitList\" : ["; // 키 waitList

		for (int i = 0; i < user.size(); i++) {
			sendData += "{";
			sendData += getJSONData("id", user.get(i).getId());
			sendData += "," + getJSONData("lv", user.get(i).getLv());
			sendData += "}";

			if (i != user.size() - 1) {
				sendData += ",";
			}
		}
		sendData += "]";
		sendData += "}"; // 끝!
		return sendData;
	}
}
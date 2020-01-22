package Client;

public class UserMessageProcessor {
	
	//make jsonData 
	public String getJSONData(String key, String value) {
		String res = "";
		res += "\"" + key +"\"" + ":";
		res += "\"" + value +"\"";
		return res;
	}
	public void processingUserMessage(String data) {
		
	}
}

/**
 * 
 */
package stompclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Alon Segal
 *
 */
public class StompFrame {
	
	//members
	private String type;
	private HashMap<String, String> headers;
	private String body;
	
	/**
	 * Default constructor
	 */
	public StompFrame() {
		this.type = "";
		this.headers = new HashMap<String, String>();
		this.body = "";
	}
	
	/**
	 * Constructor which converts STOMP String into StompFrame Object.
	 * @param message Message to convert into StompFrame
	 */
	public StompFrame(String message) {
		this.headers = new HashMap<String, String>();
		ArrayList<String> tmpList = new ArrayList<String>(Arrays.asList(message.split("\n")));
		this.type = tmpList.get(0);
		tmpList.remove(0);
		Iterator<String> i = tmpList.iterator();
		String tmp = i.next();
		while(tmp=="" | tmp=="\r")
			i.next();
		
		while(tmp.length()!=0) {
			ArrayList<String> res = new ArrayList<String>(Arrays.asList(tmp.split(":")));
			if(res.size()==2) {
				String key = res.get(0);
				String val = res.get(1);
				headers.put(key, val);
			}
			i.remove();
			tmp = i.next();
		}
		i.remove();
		this.body = i.next();
	}
	
	public void setType(String type_) {
		this.type = type_;
	}
	
	public void setHeader(String header_, String value_) {
		this.headers.put(header_, value_);
	}
	
	public void setBody(String body_) {
		this.body = body_;
	}
	
	public String toString() {
		String line = "";
		line += this.type;
		line += "\n";
		Iterator<String> it = headers.keySet().iterator(); 
		while(it.hasNext()) { 
			String key = it.next(); 
			String val = headers.get(key);
			line += key+":"+val+"\n";
		} 
		line += "\n";
		line += this.body;
		line += "\0";
		return line;
	}
	
	public String getType() {
		return this.type;
	}
	
	public String getHeader(String header_) {
		return headers.get(header_);
	}
	
	public String getBody() {
		return this.body;
	}
	
	public boolean isHeaderExists(String header) {
		return headers.containsKey(header);
	}
}

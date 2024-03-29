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
 * A STOMP object containing all the stomp features. Use toString() to get an instant stomp
 * String that can be sent (including the '\0' in the end).
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
		Iterator<String> i = tmpList.iterator();
		String tmp = i.next();
		while(tmp.length()==0)
			tmp = i.next();
		
		this.type = tmp;
		while(tmp.length()!=0) {
			ArrayList<String> res = new ArrayList<String>(Arrays.asList(tmp.split(":")));
			if(res.size()==2) {
				String key = res.get(0);
				String val = res.get(1);
				this.headers.put(key, val);
			}
			i.remove();
			tmp = i.next();
		}
		i.remove();
		String bod = i.next();
		bod = bod.substring(0, bod.length()-1);
		
		this.body = bod;
	}
	
	/**
	 * Set the type of the message
	 * @param type_ type of the frame
	 */
	public void setType(String type_) {
		this.type = type_;
	}
	
	/**
	 * Add a new header
	 * @param header_ The header name
	 * @param value_ The header's value
	 */
	public void setHeader(String header_, String value_) {
		this.headers.put(header_, value_);
	}
	
	/**
	 * Set the body message
	 * @param body_ Message to set
	 */
	public void setBody(String body_) {
		this.body = body_;
	}
	
	/**
	 * @return Return the appropriate STOMP string
	 */
	public String toString() {
		String line = "";
		line += this.type;
		line += "\n";
		Iterator<String> it = this.headers.keySet().iterator(); 
		while(it.hasNext()) { 
			String key = it.next(); 
			String val = this.headers.get(key);
			line += key+":"+val+"\n";
		} 
		line += "\n";
		line += this.body;
		line += "\0";
		return line;
	}
	
	/**
	 * 
	 * @return the type of the frame
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Return the value of the given header
	 * @param header_ name of header
	 * @return The value of the given header
	 */
	public String getHeader(String header_) {
		return this.headers.get(header_);
	}
	
	/**
	 * 
	 * @return Return the body message
	 */
	public String getBody() {
		return this.body;
	}
	
	/**
	 * Check if the header exists
	 * @param header Header to check if exists
	 * @return True if exists, else otherwise
	 */
	public boolean isHeaderExists(String header) {
		return this.headers.containsKey(header);
	}
}

/**
 * 
 */
package stompclient;

import java.io.InputStreamReader;

/**
 * @author Alon Segal
 *
 */
public interface Listener {
	//Pass
	public void setStompClient(StompClient sc);
	
	public void setStream(InputStreamReader stream);
	
	public void close();
}

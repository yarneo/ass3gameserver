/**
 * 
 */
package stompclient;

import java.io.InputStreamReader;

/**
 * @author Alon Segal
 *
 * Every object that implements the Listener should be Runnable as well.
 * When data arrives, should arise the method onData() in the StompClient instance. 
 */
public interface Listener {
	/**
	 * Pass the STOMP client instance
	 * @param sc StompClient instance to inform when data arrives.
	 */
	public void setStompClient(StompClient sc);
	
	/**
	 * Set the stream to listen to
	 * @param stream Stream to listen to
	 */
	public void setStream(InputStreamReader stream);
	
	/**
	 * Handles the data that arrived from the server, and calling stompClient.onData()
	 */
	public void handleDataSent();
	
	/**
	 * Close the Listener
	 */
	public void close();
}

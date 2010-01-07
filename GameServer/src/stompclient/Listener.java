/**
 * 
 */
package stompclient;

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
	 * Handles the data that arrived from the server, and calling stompClient.onData()
	 */
	public void handleDataSent(String line);
	
	/**
	 * Close the Listener
	 */
	public void close();
}

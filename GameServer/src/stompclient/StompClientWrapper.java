/**
 * 
 */
package stompclient;

/**
 * @author Alon Segal
 *
 */
public interface StompClientWrapper {
	
	/**
	 * When data arrives from server, the stomp frame will be sent through here.
	 */
	public void onData(StompFrame _data);
	
	/**
	 * Invoked when the STOMP client has connected to the TCP
	 */
	public void connectedToTCP();

}

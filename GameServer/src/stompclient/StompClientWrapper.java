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

}

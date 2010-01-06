/**
 * 
 */
package stompclient;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * @author Alon Segal
 *
 */
public class StompClient {

	Socket clientSocket; 
    OutputStreamWriter out; 
    BufferedReader in; 
    
    StompClientWrapper wrap;
	
    /**
     * The constructor, need to be given host and port
     * @param host The host of the server
     * @param port The port which the server is running on
     */
	public StompClient(String host, String port) {
		//TODO: Connect to server
	}
	
	/**
	 * Sets the Object that wraps the StompClient (such as GamesServer)
	 * @param _wrap StompClientWrapper object
	 */
	public void setWrap(StompClientWrapper _wrap) {
		this.wrap = _wrap;
	}
	
	/**
	 * Sends a CONNECT StompFrame to the server
	 * @param _name Name of the client
	 * @return True if sent, false otherwise
	 */
	public boolean connect(String _name) {
		return true;
	}
	
	/**
	 * Sends a SEND frame to the server
	 * @param destination Queue to send to
	 * @param message Message body
	 * @return True if sent, false otherwise
	 */
	public boolean send(String destination, String message) {
		return true;
	}
	
	/**
	 * Sends a SUBSCRIBE frame to the server
	 * @param destination Queue to send to
	 * @return True if sent, false otherwise
	 */
	public boolean subscribe(String destination) {
		return true;
	}
	
	/**
	 * Sends an UNSUBSCRIBE frame to the server
	 * @param destination Queue to send to
	 * @return True if sent, false otherwise
	 */
	public boolean unsubscribe(String destination) {
		return true;
	}
	
	/**
	 * The method which the listener object is invoking.
	 * @param _data The data Frame got from the server.
	 */
	public void onData(StompFrame _data) {
		
	}
	
	/**
	 * 
	 * @return True if logged into the STOMP server, false otherwise.
	 */
	public boolean isLoggedIn() {
		return true;
	}
	
	/**
	 * 
	 * @return True if the socket is still open, false otherwise.
	 */
	public boolean isConnected() {
		return true;
	}
	
}

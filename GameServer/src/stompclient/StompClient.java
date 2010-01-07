/**
 * 
 */
package stompclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
	public StompClient(String host, int port) {
		connectToTcp(host, port);
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
		this.wrap.onData(_data);
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
	
	/**
	 * Used by the listener to take the socket to listen to
	 * @return The current socket
	 */
	public Socket getSocket() {
		return this.clientSocket;
	}

	/******* Private functions *********/
	
	private void connectToTcp(String host, int port) {
		try { 
            clientSocket = new Socket(host, port); // host and port 
             
          //translate each character according to UTF-8. 
          out = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"); 
        } catch (UnknownHostException e) { 
              System.out.println("Unknown host: " + host); 
              System.exit(1); 
        } catch (IOException e) { 
            System.out.println("Couldn't get output to " + host + " connection"); 
            System.exit(1); 
        } 
         
        System.out.println("Connected to TCP!"); 
        //TODO: Remove that function
        //onConnected();
	}
	
	private void onConnected() {
		StompFrame sf = new StompFrame();
		sf.setType("CONNECT");
		sf.setHeader("login" ,"alon");
		sf.setHeader("passcode" ,"");
		
		try {
			out.write(sf.toString()); 
		  
			out.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}

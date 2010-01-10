/**
 * 
 */
package stompclient;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Alon Segal
 *
 */
public class StompClient {

	private Socket clientSocket; 
    private OutputStreamWriter out; 
    
    private StompClientWrapper wrap;
    
    private String host;
    private int port;
	
    /**
     * The constructor, need to be given host and port
     * @param _host The host of the server
     * @param _port The port which the server is running on
     * @param _wrap The object that created the StompClient instance
     */
	public StompClient(String _host, int _port, StompClientWrapper _wrap) {
		this.wrap = _wrap;
		this.host = _host;
		this.port = _port;
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
		StompFrame sf = new StompFrame();
		sf.setType("CONNECT");
		sf.setHeader("login" ,_name);
		sf.setHeader("passcode" ,"");
		
		if (this.sendData(sf))
			return true;
		else
			return false;
	}
	
	/**
	 * Sends a SEND frame to the server
	 * @param destination Queue to send to
	 * @param message Message body
	 * @return True if sent, false otherwise
	 */
	public boolean send(String destination, String message) {
		StompFrame sf = new StompFrame();
		sf.setType("SEND");
		sf.setHeader("destination" ,destination);
		sf.setBody(message);
		
		if (this.sendData(sf))
			return true;
		else
			return false;
	}
	
	/**
	 * Sends a SUBSCRIBE frame to the server
	 * @param destination Queue to send to
	 * @return True if sent, false otherwise
	 */
	public boolean subscribe(String destination) {
		StompFrame sf = new StompFrame();
		sf.setType("SUBSCRIBE");
		sf.setHeader("destination" ,destination);
		sf.setHeader("ack" ,"auto");
		
		if (this.sendData(sf))
			return true;
		else
			return false;
	}
	
	/**
	 * Sends an UNSUBSCRIBE frame to the server
	 * @param destination Queue to send to
	 * @return True if sent, false otherwise
	 */
	public boolean unsubscribe(String destination) {
		StompFrame sf = new StompFrame();
		sf.setType("UNSUBSCRIBE");
		sf.setHeader("destination" ,destination);
		
		if (this.sendData(sf))
			return true;
		else
			return false;
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
	
	/**
	 * Connecting to the TCP server
	 */
	public void connectToTcp() {
		try { 
            this.clientSocket = new Socket(this.host, this.port); // host and port 
             
          //translate each character according to UTF-8. 
            this.out = new OutputStreamWriter(this.clientSocket.getOutputStream(), "UTF-8"); 
        } catch (UnknownHostException e) { 
              System.out.println("Unknown host: " + this.host); 
              System.exit(1); 
        } catch (IOException e) { 
            System.out.println("Couldn't get output to " + this.host + " connection"); 
            System.exit(1); 
        } 
         
        System.out.println("Connected to TCP!"); 
        this.wrap.connectedToTCP();
        
        //TODO: feed that function
        this.onConnected();
	}

	/******* Private functions *********/
	
	private boolean sendData(StompFrame sf) {
		try {
			this.out.write(sf.toString()); 
			this.out.flush();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			
			return false;
		}
		return true;
	}
	
	private void onConnected() {
	}
}

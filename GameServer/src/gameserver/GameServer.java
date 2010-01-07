/**
 * 
 */
package gameserver;

import stompclient.GameListener;
import stompclient.Listener;
import stompclient.StompClient;
import stompclient.StompClientWrapper;
import stompclient.StompFrame;

/**
 * @author Alon Segal
 * 
 * Wraps the StompClient object.
 * All the communication is performed through the StompClient methods.
 */
public class GameServer implements StompClientWrapper {

	private StompClient stompClient;
	private Listener listener;
	private Thread listenerThread;
	
	/**
	 * Constructor
	 * @param host The host of the STOMP server
	 * @param port The port of the STOMP server
	 */
	public GameServer(String host, int port) {
		stompClient = new StompClient(host, port, this);
		
		stompClient.connectToTcp();
		listener = new GameListener();
		listener.setStompClient(stompClient);
		listenerThread = new Thread((Runnable)listener);
		listenerThread.start();
	}
	
	/**
	 * Invoked by the StompClient when connected to the TCP
	 */
	public void connectedToTCP() {
		stompClient.connect("gserver");
	}
	
	/**
	 * Invoked by the StompClient when data arrives from server
	 * @param _data The STOMP frame that arrived
	 */
	public void onData(StompFrame _data) {
		if (_data.getType().equals("CONNECTED")) {
			this.stompClient.subscribe("/queue/gserver");
		}
		if (_data.getType().equals("MESSAGE")) {
			//System.out.println(_data.toString());
			//TODO: Check what kind of message arrived (etc. play, quit, choose)
		}
	}
	
	/***********************************************************************/
	/*                            GAME METHODS                             */
	/***********************************************************************/
	
	/**
	 * All these methods should be invoked by the onData() method above. 
	 */
	
	/**
	 * Invoked by the GameServer object when a user has choosed a letter
	 * @param playerName The name of the calling user
	 * @param letter The letter he chosed
	 */
	public void letterChoosed(String playerName, String letter) {
		
	}
	
	/**
	 * Invoked by the GameServer object when a user pressed "play"
	 * @param playerName The name of the calling user 
	 */
	public void playerPlay(String playerName) {
		
	}
	
	/**
	 * Invoked by the GameServer object when a user pressed "quit"
	 * @param playerName The name of the calling user 
	 */
	public void playerQuit(String playerName) {
		
	}
	
	/**
	 * Invoked by the GameServer object when a user pressed "exit"
	 * @param playerName The name of the calling user 
	 */
	public void playerExit(String playerName) {
		
	}
}

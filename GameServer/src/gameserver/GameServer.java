/**
 * 
 */
package gameserver;

import stompclient.StompClient;
import stompclient.StompClientWrapper;
import stompclient.StompFrame;

/**
 * @author Alon Segal
 * 
 * Wraps the StompClient object.
 * This class should contain the whole logic of the hangman game.
 * All the communication is performed through the StompClient methods.
 */
public class GameServer implements StompClientWrapper {

	private StompClient stompClient;
	
	public GameServer(String host, String port) {
		stompClient = new StompClient(host, port);
		stompClient.setWrap(this);
	}
	
	public void onData(StompFrame _data) {
		//TODO: handle data recieved
	}
}

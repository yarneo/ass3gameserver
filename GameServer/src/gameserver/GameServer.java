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
 * This class should contain the whole logic of the hangman game.
 * All the communication is performed through the StompClient methods.
 */
public class GameServer implements StompClientWrapper {

	private StompClient stompClient;
	private Listener listener;
	private Thread listenerThread;
	
	public GameServer(String host, int port) {
		stompClient = new StompClient(host, port);
		stompClient.setWrap(this);
		listener = new GameListener();
		listener.setStompClient(stompClient);
		
		listenerThread = new Thread((Runnable)listener);
		listenerThread.start();
	}
	
	public void onData(StompFrame _data) {
		//TODO: handle data recieved
	}
}

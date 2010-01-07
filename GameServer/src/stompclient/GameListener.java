/**
 * 
 */
package stompclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Alon Segal
 *
 */
public class GameListener implements Listener, Runnable {

	private BufferedReader br;
	private StompClient stompClient;
	
	private boolean isWorking;
	
	public GameListener() {
		this.isWorking = true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(isWorking) {
			char ch = '0';
			String line = "";
			while(ch != '\0') {
				try {
					ch = (char)br.read();
					line += ch;
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
			handleDataSent(line);
		}
	}
	
	/* (non-Javadoc)
	 * @see stompclient.Listener#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see stompclient.Listener#handleDataSent()
	 */
	@Override
	public void handleDataSent(String line) {
		StompFrame sf = new StompFrame(line);
		stompClient.onData(sf);

	}

	/* (non-Javadoc)
	 * @see stompclient.Listener#setStompClient(stompclient.StompClient)
	 */
	@Override
	public void setStompClient(StompClient sc) {
		this.stompClient = sc;
		try {
			Socket s = sc.getSocket();
			this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1); 
		}
	}
}

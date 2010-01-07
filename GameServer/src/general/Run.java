package general;

import gameserver.GameServer;
import stompclient.StompFrame;

/**
 * 
 */

/**
 * @author Alon Segal
 *
 */
public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length!= 2) {
			System.out.println("Usage: GameServer <host> <port>");
			System.exit(0);
		}
		
		String host = args[0];
		int port = Integer.decode(args[1]).intValue();
		
		GameServer gs = new GameServer(host, port);
	}

}

package general;

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
		
		String test = "CONNECTED\nsession:4324278\n\ntry sample sentence\0";
		StompFrame sf = new StompFrame(test);
		System.out.println(sf.toString());
	}

}

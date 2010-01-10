package gameserver;

import java.io.*;
import java.util.Vector;


public class WordBank {
	private static WordBank bank = new WordBank();
	private Vector<String> container = new Vector<String>(); 	
    private boolean init=false;   
	// private constructor in order to create singleton
	private WordBank(){
	
	}
	
	/**
	 *  Reads the words file, adds the words to vector 
	 *  and creates a new instance of wordBank with the vector of words.  
	 * @param fileName the full path of the words/ expressions file 
	 * @return instance of wordBank;
	 */
  public static WordBank getInstance(String fileName){
	  if (!WordBank.bank.init){
		  WordBank.bank.init=true;
	  BufferedReader in;
	  try {
			in = new BufferedReader(new FileReader(fileName));
			String str;
			while((str = in.readLine())!= null){
				WordBank.bank.container.add(str);
			}
		} 
	  
	  catch (FileNotFoundException e) {
			// TODO Handle file not found exception 
			e.printStackTrace();
		} 
		
		catch (IOException e) {
			// TODO Handle IOEexception
			e.printStackTrace();
		}
	  }
	  return WordBank.bank;
  }
  /**
   * returns a random word or a sentence 
   * @return string representing the word/sentence
   */
  public String getRandomString(){
	  
	  return this.container.elementAt((int) (Math.random()*this.container.size()));
  }
}

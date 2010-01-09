/**
 * 
 */
package gameserver;

import java.util.ArrayList;
import java.util.UUID;

import stompclient.StompClient;

/**
 * @author Yarneo
 *
 */
public class SessionManager {
	private String gameID;
	private ArrayList<Player> players;
	private String playerTurn;
	private String currentWord;
	private String finalWord;
	private int numOfGuesses;
	private WordBank wordbank;
	private boolean startedGame;
	
	
	public SessionManager() {
		WordBank.getInstance("words.txt");
		UUID ID = UUID.randomUUID();
		gameID = ID.toString();
		currentWord = "";
		finalWord = "";
		numOfGuesses = 9;
		startedGame = false;
		players = new ArrayList<Player>();

	}
	public void newGame() {
		finalWord = wordbank.getRandomString();
		for(int i=0;i<finalWord.length();i++) {	
			currentWord = currentWord + " _";
		}
		//playerTurn = players.get(0).getName();
		numOfGuesses = 9;
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getPlaying() != 0)
			players.get(i).setPlaying(2);
		}
		startedGame = false;
		nextTurn();
	}
	
	public void sendToAll(String msg, StompClient stompy) {
		for(int i=0;i<players.size();i++) {
			stompy.send("/queue/" + players.get(i).getName(), msg);
		}
	}
	
	public int numberOfPlayers() {
		return players.size();
	}
	
	public void addPlayer(String name) {
		if(isStartedGame())
		players.add(new Player(name,1));
		else
			players.add(new Player(name,2));	
	}
	
	public void removePlayer(String name) {
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getName().equals(name))
				players.remove(i);
		}
	}
	
	public void updateLetter(String letter,int index) {
		String letterr = letter.toLowerCase();
		char[] letterArr = letterr.toCharArray();
		char theLetter = letterArr[0];
		String temp=finalWord.toLowerCase();
		char[] currentWordArr = currentWord.toCharArray();
		for(int i=0;i<finalWord.length();i++) {
			if(temp.charAt(i) == theLetter) {
				currentWordArr[(i*2)+1] = finalWord.charAt(i);
				if(index!=-1) {
					updateScorePlusOne(index);
				}
			}
		}
	currentWord = currentWordArr.toString();	
	}
	
	public void wrongGuess() {
		numOfGuesses = numOfGuesses-1;
	}
	
	public boolean haveGuessesLeft() {
		return (numOfGuesses>0);
	}
	
	public String scores() {
		String scores = "The scores are:";
		for(int i=0;i<players.size();i++) {
			scores = scores + " " + players.get(i).getName() + ": " + players.get(i).getScore();
			//The scores are: <username1>: <score1> ...\n
		}
		scores += scores + ".\n";
		
		
		return scores;
	}
	private void updateScorePlusOne(int i) {
		players.get(i).setScore(players.get(i).getScore()+1);
	}
	public int updatePlayerScore(String name,String state) {
		int index = -1;
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getName().equals(name)) {
				if(state.equals("Good")) {
				updateScorePlusOne(i);
				index = i;
				}
				else if(state.equals("Wrong")){
					players.get(i).setScore(players.get(i).getScore()-1);
				}
			}
		}
		if(!state.equals("Good") & !state.equals("Wrong")) {
			for(int i=0;i<state.length();i++) {
				char myLetter = state.charAt(i);
				String str1 = Character.toString(myLetter);
				updateLetter(str1,index);
			}
			updateScorePlusOne(index);
		}
		return players.get(index).getScore();
	}
	
	
	public void nextTurn() {
		boolean exists = false;
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getName().equals(playerTurn)) {
				exists=true;
				if(i == players.size()-1) {
					playerTurn = players.get(0).getName();
				}
				else {
					playerTurn = players.get(i+1).getName();
				}
			}
		}
		if(!exists) {
			playerTurn = players.get(0).getName();
		}
		//if the next in turn isnt playing and only observing then do this:
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getName().equals(playerTurn) && (players.get(i).getPlaying() != 2)) {
				nextTurn();
			}
			}
	}
	
	public boolean endGame() {
		if(numOfGuesses == 0) {
			return true;
		}
		if(!currentWord.contains("_")) {
			return true;
		}
		return false;
	}
	
	
	public String getID() {
		return gameID;
	}
	/**
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	/**
	 * @param players the players to set
	 */
	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	/**
	 * @return the gameID
	 */
	public String getGameID() {
		return gameID;
	}
	/**
	 * @param gameID the gameID to set
	 */
	public void setGameID(String gameID) {
		this.gameID = gameID;
	}
	/**
	 * @return the playerTurn
	 */
	public String getPlayerTurn() {
		return playerTurn;
	}
	/**
	 * @param playerTurn the playerTurn to set
	 */
	public void setPlayerTurn(String playerTurn) {
		this.playerTurn = playerTurn;
	}
	/**
	 * @return the currentWord
	 */
	public String getCurrentWord() {
		return currentWord;
	}
	/**
	 * @param currentWord the currentWord to set
	 */
	public void setCurrentWord(String currentWord) {
		this.currentWord = currentWord;
	}
	/**
	 * @return the finalWord
	 */
	public String getFinalWord() {
		return finalWord;
	}
	/**
	 * @param finalWord the finalWord to set
	 */
	public void setFinalWord(String finalWord) {
		this.finalWord = finalWord;
	}
	/**
	 * @return the numOfGuesses
	 */
	public int getNumOfGuesses() {
		return numOfGuesses;
	}
	/**
	 * @param numOfGuesses the numOfGuesses to set
	 */
	public void setNumOfGuesses(int numOfGuesses) {
		this.numOfGuesses = numOfGuesses;
	}
	/**
	 * @return the startedGame
	 */
	public boolean isStartedGame() {
		return startedGame;
	}
	/**
	 * @param startedGame the startedGame to set
	 */
	public void setStartedGame(boolean startedGame) {
		this.startedGame = startedGame;
	}
	
	
}

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
public class SessionManagerImpl implements SessionManager {
	private String gameID;
	private ArrayList<Player> players;
	private String playerTurn;
	private String currentWord;
	private String finalWord;
	private int numOfGuesses;
	private WordBank wordbank;
	private boolean startedGame;
	
	private final int const9 = 9;
	
	/**
	 * Default constructor
	 */
	public SessionManagerImpl() {
		this.wordbank = WordBank.getInstance("words.txt");
		UUID iD = UUID.randomUUID();
		this.gameID = iD.toString();
		this.currentWord = "";
		this.finalWord = "";
		this.numOfGuesses = this.const9;
		this.startedGame = false;
		this.players = new ArrayList<Player>();

	}
	
	/**
	 * creates new Game
	 */
	public void newGame() {
		this.finalWord = this.wordbank.getRandomString();
		this.currentWord = "";
		for(int i=0;i<this.finalWord.length();i++) {	
			if(this.finalWord.charAt(i) == ' ') 
				this.currentWord = this.currentWord + "  ";
			else
			this.currentWord = this.currentWord + " _";
		}
		//playerTurn = players.get(0).getName();
		this.numOfGuesses = this.const9;
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getPlaying() != 0)
				this.players.get(i).setPlaying(2);
		}
		this.startedGame = false;
		this.nextTurn();
	}
	
	/**
	 * Sends a message to all the players in the current session
	 * @param msg The message to send
	 * @param stompy The stopm client instance
	 */
	public void sendToAll(String msg, StompClient stompy) {
		for(int i=0;i<this.players.size();i++) {
			stompy.send("/queue/" + this.players.get(i).getName(), msg);
		}
	}
	
	/**
	 * Returns the number of players
	 * @return The number of players
	 */
	public int numberOfPlayers() {
		return this.players.size();
	}
	
	/**
	 * Adds a player to the session
	 * @param name The name of the player to add
	 * @param score The player's score
	 */
	public void addPlayer(String name, int score) {
		if(this.isStartedGame())
			this.players.add(new Player(name,1,score));
		else
			this.players.add(new Player(name,2,score));	
	}
	
	/**
	 * Removes a player from the session
	 * @param name The name of the player to remove
	 */
	public void removePlayer(String name) {
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name))
				this.players.remove(i);
		}
	}
	
	/**
	 * Update the correct letter in the current word and adds 1 point to the 
	 * player in the index place.
	 * @param letter The letter to update
	 * @param index The index of the player in the array that suppose to get 1 point
	 */
	public void updateLetter(String letter,int index) {
		String letterr = letter.toLowerCase();
		char[] letterArr = letterr.toCharArray();
		char theLetter = letterArr[0];
		String temp = this.finalWord.toLowerCase();
		char[] currentWordArr = this.currentWord.toCharArray();
		for(int i=0;i<this.finalWord.length();i++) {
			if(temp.charAt(i) == theLetter) {
				currentWordArr[(i*2)+1] = this.finalWord.charAt(i);
				if(index!=-1) {
					this.updateScorePlusOne(index);
				}
			}
		}
		String cmon = "";
		for(int n=0;n<currentWordArr.length;n++) {
			cmon += currentWordArr[n];
		}
		this.currentWord = cmon;	
	}
	
	/**
	 * Decrease 1 point from the number of guesses in the game
	 */
	public void wrongGuess() {
		this.numOfGuesses = this.numOfGuesses-1;
	}
	
	/**
	 * 
	 * @return True if there are still games left in the current running game false otherwise
	 */
	public boolean haveGuessesLeft() {
		return (this.numOfGuesses>0);
	}
	
	/**
	 * 
	 * @return A string containing the current score to print
	 */
	public String scores() {
		String scores = "The scores are:";
		for(int i=0;i<this.players.size();i++) {
			scores = scores + " " + this.players.get(i).getName() + ": " + this.players.get(i).getScore();
			//The scores are: <username1>: <score1> ...\n
		}
		scores = scores + ".\n";
		
		
		return scores;
	}
	
	/**
	 * Update the score of the given player according to the state.
	 * @param name The name of the player
	 * @param state "Good" if the guess is correct, "Wrong" otherwise
	 * @return The score of the player.
	 */
	private void updateScorePlusOne(int i) {
		this.players.get(i).setScore(this.players.get(i).getScore()+1);
	}
	
	/**
	 * Update the score of the given player according to the state.
	 * @param name The name of the player
	 * @param state "Good" if the guess is correct, "Wrong" otherwise
	 * @return The score of the player.
	 */
	public int updatePlayerScore(String name,String state) {
		int index = -1;
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name)) {
				index = i;
				if(state.equals("Good")) {
					this.updateScorePlusOne(i);
				index = i;
				}
				else if(state.equals("Wrong")){
					this.players.get(i).setScore(this.players.get(i).getScore()-1);
					index = i;
				}
			}
		}
		if(!state.equals("Good") & !state.equals("Wrong")) {
			for(int i=0;i<state.length();i++) {
				char myLetter = state.charAt(i);
				String str1 = Character.toString(myLetter);
				if(!str1.equals(" ") & !this.currentWord.toLowerCase().contains(str1.toLowerCase()))
				this.updateLetter(str1,index);
			}
			this.updateScorePlusOne(index);
		}
		return this.players.get(index).getScore();
	}
	
	/**
	 * Passing the turn to the next player
	 */
	public void nextTurn() {
		boolean exists = false;
		int index = -1;
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(this.playerTurn)) {
				exists=true;
				index = i;
			}
		}
				if(index == this.players.size()-1) {
					this.playerTurn = this.players.get(0).getName();
				}
				else {
					this.playerTurn = this.players.get(index+1).getName();
				}
			
		
		if(!exists) {
			this.playerTurn = this.players.get(0).getName();
		}
		//if the next in turn isnt playing and only observing then do this:
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(this.playerTurn) && (this.players.get(i).getPlaying() != 2)) {
				this.nextTurn();
			}
			}
	}
	
	/**
	 * Check if the end of the game is reached
	 * @return True if game has ended, false otherwise
	 */
	public boolean endGame() {
		if(this.numOfGuesses == 0) {
			return true;
		}
		if(!this.currentWord.contains("_")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return The ID of the current game
	 */
	public String getID() {
		return this.gameID;
	}
	/**
	 * @return the players
	 */
	public ArrayList<Player> getPlayers() {
		return this.players;
	}
	/**
	 * @param _players the players to set
	 */
	public void setPlayers(ArrayList<Player> _players) {
		this.players = _players;
	}
	/**
	 * @return the gameID
	 */
	public String getGameID() {
		return this.gameID;
	}
	/**
	 * @param _gameID the gameID to set
	 */
	public void setGameID(String _gameID) {
		this.gameID = _gameID;
	}
	/**
	 * @return the playerTurn
	 */
	public String getPlayerTurn() {
		return this.playerTurn;
	}
	/**
	 * @param _playerTurn the playerTurn to set
	 */
	public void setPlayerTurn(String _playerTurn) {
		this.playerTurn = _playerTurn;
	}
	/**
	 * @return the currentWord
	 */
	public String getCurrentWord() {
		return this.currentWord;
	}
	/**
	 * @param _currentWord the currentWord to set
	 */
	public void setCurrentWord(String _currentWord) {
		this.currentWord = _currentWord;
	}
	/**
	 * @return the finalWord
	 */
	public String getFinalWord() {
		return this.finalWord;
	}
	/**
	 * @param _finalWord the finalWord to set
	 */
	public void setFinalWord(String _finalWord) {
		this.finalWord = _finalWord;
	}
	/**
	 * @return the numOfGuesses
	 */
	public int getNumOfGuesses() {
		return this.numOfGuesses;
	}
	/**
	 * @param _numOfGuesses the numOfGuesses to set
	 */
	public void setNumOfGuesses(int _numOfGuesses) {
		this.numOfGuesses = _numOfGuesses;
	}
	/**
	 * @return the startedGame
	 */
	public boolean isStartedGame() {
		return this.startedGame;
	}
	/**
	 * @param _startedGame the startedGame to set
	 */
	public void setStartedGame(boolean _startedGame) {
		this.startedGame = _startedGame;
	}
	
	
}

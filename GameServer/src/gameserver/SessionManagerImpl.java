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
	
	
	public SessionManagerImpl() {
		this.wordbank = WordBank.getInstance("words.txt");
		UUID ID = UUID.randomUUID();
		this.gameID = ID.toString();
		this.currentWord = "";
		this.finalWord = "";
		this.numOfGuesses = 9;
		this.startedGame = false;
		this.players = new ArrayList<Player>();

	}
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
		this.numOfGuesses = 9;
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getPlaying() != 0)
				this.players.get(i).setPlaying(2);
		}
		this.startedGame = false;
		this.nextTurn();
	}
	
	public void sendToAll(String msg, StompClient stompy) {
		for(int i=0;i<this.players.size();i++) {
			stompy.send("/queue/" + this.players.get(i).getName(), msg);
		}
	}
	
	public int numberOfPlayers() {
		return this.players.size();
	}
	
	public void addPlayer(String name, int score) {
		if(this.isStartedGame())
			this.players.add(new Player(name,1,score));
		else
			this.players.add(new Player(name,2,score));	
	}
	
	public void removePlayer(String name) {
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name))
				this.players.remove(i);
		}
	}
	
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
	
	public void wrongGuess() {
		this.numOfGuesses = this.numOfGuesses-1;
	}
	
	public boolean haveGuessesLeft() {
		return (this.numOfGuesses>0);
	}
	
	public String scores() {
		String scores = "The scores are:";
		for(int i=0;i<this.players.size();i++) {
			scores = scores + " " + this.players.get(i).getName() + ": " + this.players.get(i).getScore();
			//The scores are: <username1>: <score1> ...\n
		}
		scores = scores + ".\n";
		
		
		return scores;
	}
	private void updateScorePlusOne(int i) {
		this.players.get(i).setScore(this.players.get(i).getScore()+1);
	}
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
	
	
	public void nextTurn() {
		boolean exists = false;
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(this.playerTurn)) {
				exists=true;
				if(i == this.players.size()-1) {
					this.playerTurn = this.players.get(0).getName();
				}
				else {
					this.playerTurn = this.players.get(i+1).getName();
				}
			}
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
	
	public boolean endGame() {
		if(this.numOfGuesses == 0) {
			return true;
		}
		if(!this.currentWord.contains("_")) {
			return true;
		}
		return false;
	}
	
	
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

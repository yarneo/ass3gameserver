package gameserver;

import java.util.ArrayList;
import stompclient.StompClient;

/**
 * @author Alon Segal
 *
 */
public interface SessionManager {
	
	/**
	 * creates new Game
	 * 
	 */
	public void newGame();
	
	/**
	 * Sends a message to all the players in the current session
	 * @param msg The message to send
	 * @param stompy The stopm client instance
	 */
	public void sendToAll(String msg, StompClient stompy);
	
	/**
	 * Returns the number of players
	 * @return The number of players
	 */
	public int numberOfPlayers();
	
	/**
	 * Adds a player to the session
	 * @param name The name of the player to add
	 */
	public void addPlayer(String name);
	
	/**
	 * Removes a player from the session
	 * @param name The name of the player to remove
	 */
	public void removePlayer(String name);
	
	/**
	 * Update the correct letter in the current word and adds 1 point to the 
	 * player in the index place.
	 * @param letter The letter to update
	 * @param index The index of the player in the array that suppose to get 1 point
	 */
	public void updateLetter(String letter,int index);
		
	/**
	 * Decrease 1 point from the number of guesses in the game
	 */
	public void wrongGuess();
	
	/**
	 * Update the score of the given player according to the state.
	 * @param name The name of the player
	 * @param state "Good" if the guess is correct, "Wrong" otherwise
	 * @return The score of the player.
	 */
	public int updatePlayerScore(String name,String state);
		
	/**
	 * Passing the turn to the next player
	 */
	public void nextTurn();
	
	/**
	 * Check if the end of the game is reached
	 * @return True if game has ended, false otherwise
	 */
	public boolean endGame();
	
	/**
	 * @return the players
	 */
	public ArrayList<Player> getPlayers();
	
	/**
	 * @param _players the players to set
	 */
	public void setPlayers(ArrayList<Player> _players);
	
	/**
	 * @return the gameID
	 */
	public String getGameID();
	
	/**
	 * @param _gameID the gameID to set
	 */
	public void setGameID(String _gameID);
	/**
	 * @return the playerTurn
	 */
	public String getPlayerTurn();
	/**
	 * @param _playerTurn the playerTurn to set
	 */
	public void setPlayerTurn(String _playerTurn);
	/**
	 * @return the currentWord
	 */
	public String getCurrentWord();
	/**
	 * @param _currentWord the currentWord to set
	 */
	public void setCurrentWord(String _currentWord);
	/**
	 * @return the finalWord
	 */
	public String getFinalWord();
	/**
	 * @param _finalWord the finalWord to set
	 */
	public void setFinalWord(String _finalWord);
	/**
	 * @return the numOfGuesses
	 */
	public int getNumOfGuesses();
	/**
	 * @param _numOfGuesses the numOfGuesses to set
	 */
	public void setNumOfGuesses(int _numOfGuesses);
	/**
	 * @return the startedGame
	 */
	public boolean isStartedGame();
	/**
	 * @param _startedGame the startedGame to set
	 */
	public void setStartedGame(boolean _startedGame);
	
	
}

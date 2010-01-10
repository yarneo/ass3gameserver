package gameserver;

import java.util.ArrayList;
import java.util.UUID;

import stompclient.StompClient;

/**
 * @author Yarneo
 *
 */
public interface SessionManager {
	
	public void newGame();
	
	public void sendToAll(String msg, StompClient stompy);
		
	public int numberOfPlayers();
	
	public void addPlayer(String name);
	
	public void removePlayer(String name);
		
	public void updateLetter(String letter,int index);
		
	
	public void wrongGuess();
	
	public int updatePlayerScore(String name,String state);
		
	
	public void nextTurn();
	
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

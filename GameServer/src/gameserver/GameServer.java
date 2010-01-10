/**
 * 
 */
package gameserver;

import java.util.ArrayList;

import stompclient.GameListener;
import stompclient.Listener;
import stompclient.StompClient;
import stompclient.StompClientWrapper;
import stompclient.StompFrame;

/**
 * @author Alon Segal
 * 
 * Wraps the StompClient object.
 * All the communication is performed through the StompClient methods.
 */
public class GameServer implements StompClientWrapper {
	
	private String host;
	private int port;
	private StompClient stompClient;
	private Listener listener;
	private Thread listenerThread;
	private ArrayList<Player> players;
	private ArrayList<SessionManager> managers;
	
	/**
	 * Constructor
	 * @param _host The host of the STOMP server
	 * @param _port The port of the STOMP server
	 */
	public GameServer(String _host, int _port) {
		this.host = _host;
		this.port = _port;
	}
	
	/**
	 * Starts the game server.
	 */
	public void start() {
		this.stompClient = new StompClient(this.host, this.port, this);
		
		this.stompClient.connectToTcp();
		this.listener = new GameListener();
		this.listener.setStompClient(this.stompClient);
		this.listenerThread = new Thread((Runnable)this.listener);
		this.listenerThread.start();
		this.players = new ArrayList<Player>();
		this.managers = new ArrayList<SessionManager>();
	}
	
	/**
	 * Invoked by the StompClient when connected to the TCP
	 */
	public void connectedToTCP() {
		this.stompClient.connect("gserver");
	}
	
	/**
	 * Invoked by the StompClient when data arrives from server
	 * @param _data The STOMP frame that arrived
	 */
	public void onData(StompFrame _data) {
		if (_data.getType().equals("CONNECTED")) {
			this.stompClient.subscribe("/queue/gserver");
		}
		if (_data.getType().equals("MESSAGE")) {
			if(_data.getBody().contains("new player ")) {
				this.playerLogin(_data.getBody());
			}
			if(_data.getBody().contains("play ")) {
				//when a client sends play, he will send me "play <username>"
				this.playerPlay(_data.getBody());
			}
			if(_data.getBody().contains("choose ")) {
				//when a client sends choose, he will send me "choose <letter> <username>"
				this.letterChose(_data.getBody());
			}
			if(_data.getBody().contains("guess ")) {
				//when a client sends guess, he will send me "guess <word> <username>"
				this.playerGuess(_data.getBody());
			}
			if(_data.getBody().contains("quit")) {
				//when a client sends quit, he will send me "quit <username>"
				this.playerQuit(_data.getBody());
			}
			if(_data.getBody().contains("exit")) {
				//when a client send exit, he will send me "exit <username>"
				this.playerExit(_data.getBody());
			}
		}
	}
	
	/***********************************************************************/
	/*                            GAME METHODS                             */
	/***********************************************************************/
	
	/**
	 * All these methods should be invoked by the onData() method above. 
	 */
	
	/**
	 * Invoked by the GameServer object when a user pressed "login"
	 * @param message comes as a pattern of "new player <player>" 
	 */
	public void playerLogin(String message) {
		String nameOfPlayer = message;
		nameOfPlayer = nameOfPlayer.substring(11);
		if(this.playerExists(nameOfPlayer)) {
			//error player already connected
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " already connected\n");
		}
		else {
		Player tmpPlayer = new Player(nameOfPlayer,0);
		this.players.add(tmpPlayer);
		this.stompClient.send("/queue/" + nameOfPlayer, nameOfPlayer + " connected - not playing\n");
		}
	}
	/**
	 * Invoked by the GameServer object when a user has chose a letter
	 * @param message The message of the calling user
	 */
	public void letterChose(String message) {
		String letter = message.substring(7,8);
		String nameOfPlayer = message.substring(9);
		int managerIndex = -1;
		if(message.substring(8, 9).equals(" ")) {
		if(this.playerPlaying(nameOfPlayer)) {
		for(int i=0;i<this.managers.size() & managerIndex == -1;i++){
			for(int j=0;j<this.managers.get(i).getPlayers().size() & managerIndex == -1;j++) {
				if(this.managers.get(i).getPlayers().get(j).getName().equals(nameOfPlayer)) {
					managerIndex = i;
				}
				}
		}
		if(!this.managers.get(managerIndex).getPlayerTurn().equals(nameOfPlayer)) {
			//send ERROR: not your turn\n	
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: not your turn\n");
		}
		else {
			if((this.managers.get(managerIndex).getFinalWord().contains(letter.toUpperCase()) |
					this.managers.get(managerIndex).getFinalWord().contains(letter.toLowerCase())) &
					(!this.managers.get(managerIndex).getCurrentWord().contains(letter.toUpperCase()) &
							!this.managers.get(managerIndex).getCurrentWord().contains(letter.toLowerCase()))) {
				//chose a good letter
				this.managers.get(managerIndex).sendToAll("Good - "+nameOfPlayer+
						" chose "+letter+"\n", this.stompClient);
				this.managers.get(managerIndex).updateLetter(letter,-1);
				int myScore;
				myScore = this.managers.get(managerIndex).updatePlayerScore(nameOfPlayer,"Good");
				this.updPlayerScore(nameOfPlayer,myScore);
				this.managers.get(managerIndex).setStartedGame(true);
			}
			else {
				//chose a bad letter
				this.managers.get(managerIndex).sendToAll("Wrong - "+nameOfPlayer+
						" chose "+letter+"\n", this.stompClient);
				int myScore;
				myScore = this.managers.get(managerIndex).updatePlayerScore(nameOfPlayer,"Wrong");
				this.updPlayerScore(nameOfPlayer,myScore);
				this.managers.get(managerIndex).wrongGuess();
				this.managers.get(managerIndex).setStartedGame(true);

			}
			if(this.managers.get(managerIndex).endGame()) {
				//write game Over
				this.managers.get(managerIndex).sendToAll("Game is over\n", this.stompClient);
				//start a new game
				this.managers.get(managerIndex).newGame();
				//make everyone who wants to play into the new game
				for(int l=0;l<this.managers.get(managerIndex).getPlayers().size();l++) {
					String mrmrStr = this.managers.get(managerIndex).getPlayers().get(l).getName();
					for(int m=0;m<this.players.size()& this.managers.get(managerIndex).getPlayers().get(l).getPlaying() == 2
					;m++) {
						if(this.players.get(m).getName().equals(mrmrStr)) {
							this.players.get(m).setPlaying(2);
						}
					}
				}
			}
			//change players turn
			this.managers.get(managerIndex).nextTurn();
			 //  1.  Score: The scores are: <username1>: <score1> ...\n
			this.managers.get(managerIndex).sendToAll(this.managers.get(managerIndex).scores(),this.stompClient);
			 //  2. Current word: Current word: <encoding>\n
			this.managers.get(managerIndex).sendToAll("Current word: " + 
					this.managers.get(managerIndex).getCurrentWord() + "\n", this.stompClient);
			 //  3. Guesses left: <N> more guesses left\n
			this.managers.get(managerIndex).sendToAll(this.managers.get(managerIndex).getNumOfGuesses()
					+ " more guesses left\n",this.stompClient);
			 //  4. Next turn: It is <username>'s turn\n 
			this.managers.get(managerIndex).sendToAll("It is " + this.managers.get(managerIndex).getPlayerTurn()
					+ "'s turn\n",this.stompClient);
			
			
		}
		}
		else {
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
					"is either not connected or is not playing\n");
			//player is either not connected or isnt playing
		}
		}
		else {
			String[] tmpStrArr = nameOfPlayer.split(" ");
			nameOfPlayer = tmpStrArr[tmpStrArr.length-1];
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: you have entered more than " +
					"one letter\n");
		}
	}
	
	/**
	 * Invoked by the GameServer object when a user has chose a letter
	 * @param message The message of the calling user
	 */
	public void playerGuess(String message) {
		String tempy = message.substring(6);
		String[] tempyArr = tempy.split(" ");
		String word = tempyArr[0];
		String nameOfPlayer = tempyArr[1];
		int managerIndex = -1;
		if(this.playerPlaying(nameOfPlayer)) {
		for(int i=0;i<this.managers.size() & managerIndex == -1;i++){
			for(int j=0;j<this.managers.get(i).getPlayers().size() & managerIndex == -1;j++) {
				if(this.managers.get(i).getPlayers().get(j).equals(nameOfPlayer)) {
					managerIndex = i;
				}
				}
		}
		if(!this.managers.get(managerIndex).getPlayerTurn().equals(nameOfPlayer)) {
			//send ERROR: not your turn\n
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: not your turn\n");
		}
		else {
			if(this.managers.get(managerIndex).getFinalWord().toLowerCase().equals(word.toLowerCase())) {
				//chose a good word
				this.managers.get(managerIndex).sendToAll("Good - "+nameOfPlayer+
						" chose "+word+"\n", this.stompClient);
				int myScore;
				myScore = this.managers.get(managerIndex).updatePlayerScore(nameOfPlayer,word);
				this.updPlayerScore(nameOfPlayer,myScore);
				this.managers.get(managerIndex).setStartedGame(true);
			}
			else {
				//chose a bad word
				this.managers.get(managerIndex).sendToAll("Wrong - "+nameOfPlayer+
						" chose "+word+"\n", this.stompClient);
				int myScore;
				myScore = this.managers.get(managerIndex).updatePlayerScore(nameOfPlayer,"Wrong");
				this.updPlayerScore(nameOfPlayer,myScore);
				this.managers.get(managerIndex).wrongGuess();
				this.managers.get(managerIndex).setStartedGame(true);

			}
			if(this.managers.get(managerIndex).endGame()) {
				//write game Over
				this.managers.get(managerIndex).sendToAll("Game is over\n", this.stompClient);
				//start a new game
				this.managers.get(managerIndex).newGame();
				//make everyone who wants to play into the new game
				for(int l=0;l<this.managers.get(managerIndex).getPlayers().size();l++) {
					String mrmrStr = this.managers.get(managerIndex).getPlayers().get(l).getName();
					for(int m=0;m<this.players.size()& this.managers.get(managerIndex).getPlayers().get(l).getPlaying() == 2
					;m++) {
						if(this.players.get(m).getName().equals(mrmrStr)) {
							this.players.get(m).setPlaying(2);
						}
					}
				}
			}
			//change players turn
			this.managers.get(managerIndex).nextTurn();
			 //  1.  Score: The scores are: <username1>: <score1> ...\n
			this.managers.get(managerIndex).sendToAll(this.managers.get(managerIndex).scores(),this.stompClient);
			 //  2. Current word: Current word: <encoding>\n
			this.managers.get(managerIndex).sendToAll("Current word: " + 
					this.managers.get(managerIndex).getCurrentWord() + "\n", this.stompClient);
			 //  3. Guesses left: <N> more guesses left\n
			this.managers.get(managerIndex).sendToAll(this.managers.get(managerIndex).getNumOfGuesses()
					+ " more guesses left\n",this.stompClient);
			 //  4. Next turn: It is <username>'s turn\n 
			this.managers.get(managerIndex).sendToAll("It is " + this.managers.get(managerIndex).getPlayerTurn()
					+ "'s turn\n",this.stompClient);
			
			
		}
		}
		else {
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
			"is either not connected or is not playing\n");
			//player is either not connected or isnt playing
		}
	}
	/**
	 * Invoked by the GameServer object when a user pressed "play"
	 * @param message The message to pass
	 */
	public void playerPlay(String message) {
		int index=-1;
		boolean availableGame = false;
		String nameOfPlayer = message;
		nameOfPlayer = nameOfPlayer.substring(5);
		if(this.playerExists(nameOfPlayer)) {
			if(this.playerPlaying(nameOfPlayer)) {
				this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
				"is already playing in a game\n");
				//player is already playing in a game
			}
			else {
		for(int i=0;i<this.managers.size();i++) {
			if(this.managers.get(i).numberOfPlayers() < 4) {
				availableGame = true;
				index = i;
			}
		}
		if(availableGame) {
			this.managers.get(index).addPlayer(nameOfPlayer);
			for(int k=0;k<this.players.size();k++) {
				if(this.players.get(k).getName().equals(nameOfPlayer)) {
					if(this.managers.get(index).isStartedGame())
						this.players.get(k).setPlaying(1);
					else 
						this.players.get(k).setPlaying(2);
				}
			}
			String sendMsg = "";
			sendMsg = nameOfPlayer + " has joined game " + this.managers.get(index).getID() +
			" with players";
			for(int i=0;i<this.managers.get(index).getPlayers().size();i++) {
				sendMsg += " " + this.managers.get(index).getPlayers().get(i).getName();
			}
			sendMsg += ". Current word: " + this.managers.get(index).getCurrentWord() +
			". Next to play:" + this.managers.get(index).getPlayerTurn() + "\n";
			this.managers.get(index).sendToAll(sendMsg, this.stompClient);
		}
		else {
			//no game available, either no game open, or all games are full
			//so need to open a new game.
			SessionManager temp = new SessionManager();
			//TODO:when i do add player then it resets his score, need to add score to constructor
			temp.addPlayer(nameOfPlayer);
			for(int k=0;k<this.players.size();k++) {
				if(this.players.get(k).getName().equals(nameOfPlayer)) {
					this.players.get(k).setPlaying(2);
				}
			}
			temp.newGame();
			this.managers.add(temp);
			String sendMsg = "";
			sendMsg = nameOfPlayer + " has joined game " + temp.getID() +
			" with players";
			for(int i=0;i<temp.getPlayers().size();i++) {
				sendMsg += " " + temp.getPlayers().get(i).getName();
			}
			sendMsg += ". Current word: " + temp.getCurrentWord() +
			". Next to play:" + temp.getPlayerTurn() + "\n";
			temp.sendToAll(sendMsg, this.stompClient);
			
		}
		}
		}
		else {
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
			"doesn't exist so cant give it the command play\n");
			//player doesnt exist so cant do play on it
		}
		
		//alex has joined game G1 with players yair yoav meni. Current word: a___a_. Next to play: yoav\n
	}
	
	/**
	 * Invoked by the GameServer object when a user pressed "quit"
	 * @param message The name of the calling user 
	 */
	public void playerQuit(String message) {
		boolean anyonePlaying = false;
		int index = -1;
		String nameOfPlayer = message.substring(5);
		if(this.playerPlaying(nameOfPlayer)) {
		for(int i=0;i<this.managers.size();i++) {
			for(int j=0;j<this.managers.get(i).getPlayers().size();j++) {
				if(this.managers.get(i).getPlayers().get(j).getName().equals(nameOfPlayer)) {
					index = i;
					this.managers.get(i).getPlayers().get(j).setPlaying(1);
					if(this.managers.get(i).getPlayers().get(j).getPlaying() == 2) {
						anyonePlaying = true;
					}
				}
			}
		}
		//remove him from playing from main list of players
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(nameOfPlayer)) {
				this.players.get(i).setPlaying(1);
			}
		}
		//Bye. <username> has stopped playing.\n
		this.managers.get(index).sendToAll("Bye. " + nameOfPlayer + " has stopped playing.\n", this.stompClient);
		if(anyonePlaying) {
			for(int j=0;j<this.managers.get(index).getPlayers().size();j++) {
				if(this.managers.get(index).getPlayerTurn().equals(nameOfPlayer)) {
					this.managers.get(index).nextTurn();
			}
			}
		}
		else {
			//destroy session manager
			for(int i=0;i<this.managers.size();i++) {
				for(int j=0;j<this.managers.get(i).getPlayers().size();j++) {
					String tempStrName = this.managers.get(i).getPlayers().get(j).getName();
					for(int k=0;k<this.players.size();k++) {
						if(this.players.get(k).getName().equals(tempStrName)) {
							this.players.get(k).setPlaying(0);
						}
					}
				}
			}
			this.managers.remove(index);
		}
	
		}
		else {
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
			"is not even playing\n");
			//player isnt even playing so how can he quit a game
		}
	}
	
	/**
	 * Invoked by the GameServer object when a user pressed "exit"
	 * @param message The name of the calling user 
	 */
	public void playerExit(String message) {
		boolean anyonePlaying = false;
		int index = -1;
		String nameOfPlayer = message.substring(5);
		if(this.playerExists(nameOfPlayer)) {
		for(int i=0;i<this.managers.size();i++) {
			for(int j=0;j<this.managers.get(i).getPlayers().size();j++) {
				if(this.managers.get(i).getPlayers().get(j).getName().equals(nameOfPlayer)) {
					index = i;

					this.managers.get(i).removePlayer(nameOfPlayer);

				}
			}
		}
		
		//remove from main list of players
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(nameOfPlayer)) {
				this.players.remove(i);
			}
		}
		//check to see if anyone else is playing the game
			for(int j=0;j<this.managers.get(index).getPlayers().size();j++) {
				if(this.managers.get(index).getPlayers().get(j).getPlaying() == 2) {
					anyonePlaying = true;
				}
			}
		//Bye. <username> has left the system.\n
			this.managers.get(index).sendToAll("Bye. " + nameOfPlayer + " has left the system.\n", this.stompClient);
		//if theres someone still playing then move to the next turn if the person
		//who quit was his turn
		if(anyonePlaying) {
			for(int j=0;j<this.managers.get(index).getPlayers().size();j++) {
				if(this.managers.get(index).getPlayerTurn().equals(nameOfPlayer)) {
					this.managers.get(index).nextTurn();
			}
			}
		}
		else {
			//destroy session manager
			for(int i=0;i<this.managers.size();i++) {
				for(int j=0;j<this.managers.get(i).getPlayers().size();j++) {
					String tempStrName = this.managers.get(i).getPlayers().get(j).getName();
					for(int k=0;k<this.players.size();k++) {
						if(this.players.get(k).getName().equals(tempStrName)) {
							this.players.get(k).setPlaying(0);
						}
					}
				}
			}
			this.managers.remove(index);
		}
		}
		else {
			//player doesnt even exists so how can he exit
			this.stompClient.send("/queue/" + nameOfPlayer, "ERROR: " +nameOfPlayer + " " +
			"does not exist\n");
		}
		
		
	}
	
	
	private void updPlayerScore(String name, int score) {
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name)) {
				this.players.get(i).setScore(score);
			}
		}
	}
	private boolean playerExists(String name) {
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	private boolean playerPlaying(String name) {
		for(int i=0;i<this.players.size();i++) {
			if(this.players.get(i).getName().equals(name) & this.players.get(i).getPlaying() != 0) {
				return true;
			}
		}
		return false;
	}
}
//TODO:when people exit and only observe? VI
//a player cant do "play" twice or "login" twice   VI
//when an action is done then gameisstarted VI
//when a player quits hes still in the game, he observes it, if he chooses play again he returns
//to the same game, and also no1 else can join the game if its 3 ppl + the person who quit VI
//if everyone quits the session is destroyed VI
//join existing game,  after game is won/lost VI
//make sure that the choose command is only one letter
//when someone joins in middle? VI
//when game ends then what? new game? print new msg of who won? etc..? VI
//cant do choose/guess/quit/exit without login/play VI
//if u only quit game and not exit server then the points stay with you from game to game VI
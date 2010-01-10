/**
 * 
 */
package gameserver;

/**
 * @author Yarneo
 *
 */
public class Player {
String name;
int score;
//if playing is: 0 = he isnt playing, 1 = isnt playing but wants to play, 2 = is playing
int playing;

/**
 * Constructor
 * @param name_ name of the player
 * @param playing_ The playing status
 */
public Player(String name_,int playing_,int score_) {
	this.name = name_;
	this.score = score_;
	this.playing = playing_;
}

/**
 * @return the name
 */
public String getName() {
	return this.name;
}

/**
 * @param _name the name to set
 */
public void setName(String _name) {
	this.name = _name;
}

/**
 * @return the score
 */
public int getScore() {
	return this.score;
}

/**
 * @param _score the score to set
 */
public void setScore(int _score) {
	this.score = _score;
}

/**
 * @return the playing
 */
public int getPlaying() {
	return this.playing;
}

/**
 * @param _playing the playing to set
 */
public void setPlaying(int _playing) {
	this.playing = _playing;
}
	
	
}

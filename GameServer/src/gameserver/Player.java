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
	
public Player(String name_,int playing_) {
	name = name_;
	score = 0;
	playing = playing_;
}

/**
 * @return the name
 */
public String getName() {
	return name;
}

/**
 * @param name the name to set
 */
public void setName(String name) {
	this.name = name;
}

/**
 * @return the score
 */
public int getScore() {
	return score;
}

/**
 * @param score the score to set
 */
public void setScore(int score) {
	this.score = score;
}

/**
 * @return the playing
 */
public int getPlaying() {
	return playing;
}

/**
 * @param playing the playing to set
 */
public void setPlaying(int playing) {
	this.playing = playing;
}
	
	
}

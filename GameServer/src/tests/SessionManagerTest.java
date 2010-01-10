/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import gameserver.Player;
import gameserver.SessionManager;
import gameserver.SessionManagerImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Alon Segal
 *
 */
public class SessionManagerTest {

	SessionManager sm;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sm = createInstance();
	}
	
	private SessionManager createInstance() {
		SessionManager s = new SessionManagerImpl();
		
		//create 3 players with score 0
		s.addPlayer("Alon", 0);
		s.addPlayer("Yarden", 0);
		s.addPlayer("Eyal", 0);
	
		return s;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		this.sm = createInstance();
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#newGame()}.
	 */
	@Test
	public final void testNewGame() {
		this.sm.newGame();
		
		assertNotNull(this.sm.getCurrentWord());
		assertNotNull(this.sm.getFinalWord());
		assertEquals("Guesses is not 9", 9, this.sm.getNumOfGuesses());
		
		//check that all the players are playing
		ArrayList<Player> list = this.sm.getPlayers();
		for (int i=0;i<list.size();i++) {
			assertEquals("Not all the players are playing", 2, list.get(i).getPlaying());
		}
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#addPlayer(java.lang.String)}.
	 */
	@Test
	public final void testAddPlayer() {
		this.sm.addPlayer("Omer", 0);
		assertTrue("Player has not been inserted to the currect place", this.sm.getPlayers().get(3).getName().equals("Omer"));
		this.sm.addPlayer("Meir", 0);
		assertTrue("Player has not been inserted to the currect place", this.sm.getPlayers().get(4).getName().equals("Meir"));
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#removePlayer(java.lang.String)}.
	 */
	@Test
	public final void testRemovePlayer() {
		this.sm.removePlayer("Alon");
		assertEquals("Player has not been removed", 2, this.sm.getPlayers().size());
		this.sm.addPlayer("Meir", 0);
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#updateLetter(java.lang.String, int)}.
	 */
	@Test
	public final void testUpdateLetter() {
		this.sm.newGame();
		this.sm.setFinalWord("test word");
		this.sm.setCurrentWord(" _ _ s _   _ _ _ _");
		this.sm.updateLetter("e", 0);
		assertEquals("Score has not updated", 1, this.sm.getPlayers().get(0).getScore());
		assertEquals("Word has not been updeted", " _ e s _   _ _ _ _", 
				this.sm.getCurrentWord());
		this.sm.updateLetter("t", 2);
		assertEquals("Score has not updated", 2, this.sm.getPlayers().get(2).getScore());
		assertEquals("Word has not been updated", " t e s t   _ _ _ _", 
				this.sm.getCurrentWord());
		this.sm.updateLetter("w", 1);
		assertEquals("Score has not updated", 1, this.sm.getPlayers().get(1).getScore());
		assertEquals("Word has not been updated", " t e s t   w _ _ _", 
				this.sm.getCurrentWord());
		this.sm.updateLetter("d", 0);
		assertEquals("Score has not updated", 2, this.sm.getPlayers().get(0).getScore());
		assertEquals("Word has not been updated", " t e s t   w _ _ d", 
				this.sm.getCurrentWord());
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#updatePlayerScore(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testUpdatePlayerScore() {
		this.sm.updatePlayerScore("Alon", "Good");
		assertEquals("Score has not been updated correctly", 1, this.sm.getPlayers().get(0).getScore());
		this.sm.updatePlayerScore("Alon", "Wrong");
		assertEquals("Score has not been updated correctly", 0, this.sm.getPlayers().get(0).getScore());
		this.sm.updatePlayerScore("Alon", "Wrong");
		assertEquals("Score has not been updated correctly", -1, this.sm.getPlayers().get(0).getScore());
		this.sm.updatePlayerScore("Alon", "Good");
		assertEquals("Score has not been updated correctly", 0, this.sm.getPlayers().get(0).getScore());
		this.sm.updatePlayerScore("Yarden", "Wrong");
		assertEquals("Score has not been updated correctly", -1, this.sm.getPlayers().get(1).getScore());
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#nextTurn()}.
	 */
	@Test
	public final void testNextTurn() {
		this.sm.newGame();
		assertEquals("Not the currect player turn", "Alon", this.sm.getPlayerTurn());
		this.sm.nextTurn();
		assertEquals("Not the currect player turn", "Yarden", this.sm.getPlayerTurn());
		this.sm.nextTurn();
		assertEquals("Not the currect player turn", "Eyal", this.sm.getPlayerTurn());
		this.sm.nextTurn();
		assertEquals("Not the currect player turn", "Alon", this.sm.getPlayerTurn());
	}

	/**
	 * Test method for {@link gameserver.SessionManagerImpl#endGame()}.
	 */
	@Test
	public final void testEndGame() {
		this.sm.newGame();
		this.sm.setNumOfGuesses(0);
		assertTrue("Game suppose to end", this.sm.endGame());
		this.sm.setNumOfGuesses(1);
		assertFalse("Game suppose to continue", this.sm.endGame());
	}
}

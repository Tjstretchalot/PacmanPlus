package tim.pacman;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * A game mode which handles all mode-related
 * logic, such as ghosts, ending the game, collision 
 * detection and scoring.
 * 
 * @author Timothy
 *
 */
public interface GameMode {
	
	/**
	 * Does one game tick.
	 * @param time the current time
	 * @param delta the time since last tick
	 */
	public void doTick(long time, long delta);
	
	/**
	 * Does the necessary check to see if the game is over.
	 * This method should not affect gameplay
	 * @return if the game is over
	 */
	public boolean isGameOver();
	
	/**
	 * Returns the current leader
	 * @return the leading player
	 */
	public Player getLeader();
	
	/**
	 * Returns player 1
	 * @return player 1
	 */
	public Player getPlayer1();
	
	/**
	 * Returns player 2
	 * @return player 2
	 */
	public Player getPlayer2();
	
	/**
	 * Called when the main application has unsynced all resources so
	 * it is safe to end the game. Must change the GUI to whatever is next.
	 */
	public void onGameOver();
	
	/**
	 * Does custom rendering, primary ghosts
	 * @param cont the container
	 * @param g the graphics
	 */
	public void doCustomRendering(GameContainer cont, Graphics g);
	
	/**
	 * Returns the number of ghosts. May be dynamic, implementation specific
	 * @return the number of ghosts
	 */
	public int getNumberGhosts();

	/**
	 * Returns all of the ghosts in an array
	 * @return the number of ghosts
	 */
	public Ghost[] getGhosts();
	
	/**
	 * (Re)spawns a ghost that would be at the specified index
	 * in getGhosts() on any of the <code>allowed</code> spots on the map.
	 * @param index the index
	 * @param allowed the map
	 */
	public void spawnGhost(int index, byte... allowed);

	/**
	 * Returns the chance of the ghost respawning. May
	 * be dynamic.
	 * @return the ghost respawn chance
	 */
	public float getGhostRespawnChance();

	/**
	 * Checks the boundaries on a player for walls or 
	 * player or ghosts.
	 * @param g the ghost
	 */
	public void checkBoundaries(Player g);

	/**
	 * Snaps the specified player to the grid
	 * @param g
	 */
	public void snapToGrid(Player g);

	/**
	 * Returns the map that is attached to this game mode
	 * @return the game map
	 */
	public GameMap getGameMap();
}

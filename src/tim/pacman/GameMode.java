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
	
	public boolean isGameOver();
	
	public Player getLeader();
	
	public Player getPlayer1();
	
	public Player getPlayer2();
	
	public void onGameOver();
	
	public void doCustomRendering(GameContainer cont, Graphics g);
	
	public int getNumberGhosts();

	public Ghost[] getGhosts();
	
	public void spawnGhost(int index, byte... allowed);

	public float getGhostRespawnChance();
}

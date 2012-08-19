package tim.pacman;

/**
 * The game controls, handling setting the velocity and direction
 * of each player.
 * @author Timothy
 */
public interface GameControls {
	
	/**
	 * Retrieves the current status of each player and
	 * updates his/her velocity accordingly
	 * @param time the game time
	 */
	public void doTick(long time);
}

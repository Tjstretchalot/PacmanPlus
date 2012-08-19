package tim.pacman;

/**
 * A basic implementation of game controls, allows easy
 * access to setting the player location with matching velocity
 * and rotation.
 * 
 * @author Timothy
 */
public abstract class AbstractGameControls implements GameControls {

	private Player player1;
	private Player player2;
	
	protected GameMode gameMode;
	
	/**
	 * Creates the game controls.  Will have a matching pointer to
	 * the GameMode's players.
	 * 
	 * @param gameMode the game mode
	 */
	public AbstractGameControls(GameMode gameMode)
	{
		if(gameMode != null)
		{
			setPlayer1(gameMode.getPlayer1());
			setPlayer2(gameMode.getPlayer2());
		}else
		{
			
		}
		
		this.gameMode = gameMode;
	}
	
	/**
	 * Returns player 2
	 * @return player 2
	 */
	public Player getPlayer2() {
		return player2;
	}

	/**
	 * Sets the controls handling of player 2
	 * @param player2 the new player 2
	 */
	public void setPlayer2(Player player2) {
		this.player2 = player2;
	}

	/**
	 * Returns player 1
	 * @return player 1
	 */
	public Player getPlayer1() {
		return player1;
	}

	/**
	 * Sets the controls handling of player 1
	 * @param player1 the new player 1
	 */
	public void setPlayer1(Player player1) {
		this.player1 = player1;
	}
	
}

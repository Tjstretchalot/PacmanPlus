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
	
	private GameMode gameMode;
	
	/**
	 * Creates the game controls.  Will have a matching pointer to
	 * the GameMode's players.
	 * 
	 * @param gameMode the game mode
	 */
	public AbstractGameControls(GameMode gameMode)
	{
		setPlayer1(gameMode.getPlayer1());
		setPlayer2(gameMode.getPlayer2());
		
		this.gameMode = gameMode;
	}
	
	/**
	 * Sets player 1's direction up, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer1Up()
	{
		getPlayer1().getVelocity().y = -1;
		getPlayer1().getVelocity().x = 0;
		getPlayer1().setRotation(270f);
	}
	
	/**
	 * Sets player 1's direction down, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer1Down()
	{
		getPlayer1().getVelocity().y = 1;
		getPlayer1().getVelocity().x = 0;
		getPlayer1().setRotation(90f);
	}
	
	/**
	 * Sets player 1's direction left, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer1Left()
	{
		getPlayer1().getVelocity().x = -1;
		getPlayer1().getVelocity().y = 0;
		getPlayer1().setRotation(180f);
	}
	
	/**
	 * Sets player 1's direction right, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer1Right()
	{
		getPlayer1().getVelocity().x = 1;
		getPlayer1().getVelocity().y = 0;
		getPlayer1().setRotation(0f);
	}
	
	/**
	 * Sets player 2's direction up, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer2Up()
	{
		getPlayer2().getVelocity().y = -1;
		getPlayer2().getVelocity().x = 0;
		getPlayer2().setRotation(270f);
	}
	
	/**
	 * Sets player 2's direction down, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer2Down()
	{
		getPlayer2().getVelocity().y = 1;
		getPlayer2().getVelocity().x = 0;
		getPlayer2().setRotation(90f);
	}
	
	/**
	 * Sets player 2's direction left, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer2Left()
	{
		getPlayer2().getVelocity().x = -1;
		getPlayer2().getVelocity().y = 0;
		getPlayer2().setRotation(180f);
	}
	
	/**
	 * Sets player 2's direction right, with a matching
	 * velocity and rotation
	 */
	protected void setPlayer2Right()
	{
		getPlayer2().getVelocity().x = 1;
		getPlayer2().getVelocity().y = 0;
		getPlayer2().setRotation(0f);
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

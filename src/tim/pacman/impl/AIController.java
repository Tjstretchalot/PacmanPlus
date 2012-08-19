package tim.pacman.impl;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.GhostController;
import tim.pacman.Player;

/**
 * An AI Controller is a wrapper of a player that makes
 * it act like an AI.
 * 
 * @author Timothy
 */
public abstract class AIController {
	protected Player thePlayer;
	protected Player oppPlayer;
	protected GameMap gMap;
	protected GameMode mode;
	
	protected AIController(Player player, Player opponent, GameMode mode, GameMap map)
	{
		thePlayer = player;
		oppPlayer = opponent;
		this.mode = mode;
		gMap = map;
	}
	
	/**
	 * Does AI Processing.
	 * @param time
	 */
	public abstract void doTick(long time);
	
	// Switched to the player having these methods
	
	protected void goUp()
	{
		thePlayer.goUp();
	}
	
	protected void goDown()
	{
		thePlayer.goDown();
	}
	
	protected void goLeft()
	{
		thePlayer.goLeft();
	}
	
	protected void goRight()
	{
		thePlayer.goRight();
	}
}

package tim.pacman;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Timer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.pacman.impl.GameOver;

/**
 * A basic implementation of a game mode, handles basic user
 * interfacing for displaying the players score, time remaining,
 * and the lead player.  These messages can be changed by overriding
 * the appropriate get***Message().  Also handles rendering of ghosts
 * and collisions.
 * 
 * @author Timothy
 */
public abstract class AbstractGameMode implements GameMode {
	/**
	 * Player 1.
	 */
	protected Player player1;
	
	/**
	 * Player 2
	 */
	protected Player player2;
	
	/**
	 * The current length of the game in milliseconds
	 */
	protected long gameTime;
	
	/**
	 * If the game is over
	 */
	protected boolean gameOver;
	
	/**
	 * The game map that is used.  Generally an
	 * AStarGameMap
	 */
	protected GameMap gameMap;
	
	/**
	 * The list of ghosts
	 */
	private Ghost[] ghosts;
	
	/**
	 * The timer used for scheduling events
	 */
	protected Timer timer;
	
	/**
	 * The current movement modifier
	 */
	protected float player1Modifier;
	
	protected float player2Modifier;
	
	
	
	@Override
	public void doTick(long time, long delta)
	{
		gameTime += delta;
		doMovement(player1, player1Modifier);
		doMovement(player2, player2Modifier);
		
		Rectangle2D.Float pla1 = player1.getLocationAsRect();
		Rectangle2D.Float pla2 = player2.getLocationAsRect();
		
		if(pla1.intersects(pla2))
			onPlayerCollide(time);
		
		checkEntityCollisions(time);
		// Now 'pull' the player 0.1 to the nearest square.
		
		checkBoundaries(player1);
		checkBoundaries(player2);
		
		snapToGrid(player1);
		snapToGrid(player2);
	}
	
	/**
	 * Moves the specified players coordinate the appropriate
	 * velocity with the specified modifier
	 * @param player the player
	 * @param modifier the modifier
	 */
	protected void doMovement(Player player, float modifier) {
		player.getLocation().x += player.getVelocity().x * modifier;
		player.getLocation().y += player.getVelocity().y * modifier;
	}

	/**
	 * Checks if any of the entities are colliding with the player
	 * @param time the current game time
	 */
	protected void checkEntityCollisions(long time) {
		Rectangle2D.Float pla1 = player1.getLocationAsRect();
		Rectangle2D.Float pla2 = player2.getLocationAsRect();
		for(int i = 0; i < ghosts.length; i++)
		{
			Ghost g = ghosts[i];
			
			if(g.isDead())
				continue;
			
			Rectangle2D.Float ghRect = g.getLocationAsRect();
			
			if(ghRect.intersects(pla1))
				onPlayerCollideWithGhost(time, player1, i);
			
			if(ghRect.intersects(pla2))
				onPlayerCollideWithGhost(time, player2, i);
		}
	}

	/**
	 * Checks if the specified player is colliding 
	 * with any special material, determined by isSpecial.
	 * 
	 * @param pl the player
	 */
	public void checkBoundaries(Player pl) {
		Point loc = gameMap.toGridLocation(pl.getLocation());
		int locationX = loc.x;
		int locationY = loc.y;
		
		if(gameMap.getType(loc.x, loc.y) == GameMap.WALL)
		{
			if(loc.x > 0)
				pl.getLocation().x -= 1f;
			else
				pl.getLocation().x += 1f;
		}
		
		Point[] surrounding = gameMap.getSurrounding((int) locationX, (int) locationY);
		
		Rectangle2D.Float player = pl.getLocationAsRect();
		for(Point p : surrounding)
		{
			Rectangle2D.Float rect = new Rectangle2D.Float(p.x * 16 + 112, p.y * 16 + 32, 16, 16);
			if(rect.intersects(player))
			{
				byte type = gameMap.getType(p.x, p.y);
				if(isSpecial(pl, type))
				{
					specialMovement(pl, type);
//					System.out.println(pl.getClass().getSimpleName() + " hit a boundary");
				}
				
				if(type == GameMap.ORB)
				{
					gameMap.setType(p.x, p.y, GameMap.EATEN_ORB);
					
					onOrbCollect(pl);
				}
			}
		}
	}

	/**
	 * Called when checkBoundaries determines that a player
	 * is on a special block.  Should undo any movement.
	 * 
	 * @param pl the player that hit the block
	 * @param type the type of block
	 */
	protected void specialMovement(Player pl, byte type) {
		doMovement(pl, -0.25f);
	}

	/**
	 * Called when a player picks up an orb.  The orb will be removed by
	 * checkBoundaries so only score needs to be added
	 * 
	 * @param pl
	 */
	protected abstract void onOrbCollect(Player pl);

	/**
	 * Returns if the specified block type is significant to
	 * the specified player
	 * @param pl the player
	 * @param type the block type
	 * @return if it is special
	 */
	protected boolean isSpecial(Player pl, byte type) {
		return type == GameMap.WALL || (type == GameMap.SPAWNER && !(pl instanceof Ghost));
	}

	/**
	 * Snaps the player to the grid, where the grid
	 * is <code>player x, y - 2 % 16 == 0<code>
	 * Will move a maximum of 0.1f in each direction.
	 * Will not change the direction the player is going.
	 * 
	 * @param pl The player to attach to the grid
	 */
	public void snapToGrid(Player pl) {
		// Find the closest spot
		
		if(pl.getVelocity().x == 0 && pl.getLocation().x - 2 % 16 != 0)
		{
			float locationX = Math.round(pl.getLocation().x / 16f);
			float preferredX = (locationX * 16) + 2f;
			
			float move = preferredX - pl.getLocation().x;
			if(Math.abs(move) > 0.1f)
				move = 0.1f * Math.signum(move);
			pl.getLocation().x += move;
		}
		
		if(pl.getVelocity().y == 0 && pl.getLocation().y - 2 % 16 != 0)
		{
			float locationY = Math.round(pl.getLocation().y / 16f);
			float preferredY = (locationY * 16) + 2f;
			
			float move = preferredY - pl.getLocation().y;
			if(Math.abs(move) > 0.1f)
				move = 0.1f * Math.signum(move);
			pl.getLocation().y += move;
		}
		
	}

	/**
	 * Prepares the game mode for logical ticks.
	 * @param map the map.
	 */
	public AbstractGameMode(GameMap map)
	{
		gameMap = map;
		ghosts = new Ghost[getNumberGhosts()];
		player1Modifier = 0.25f;
		player2Modifier = 0.25f;
		for(int i = 0; i < getNumberGhosts(); i++)
		{
			spawnGhost(i, GameMap.SPAWNER);
		}
		player1 = new Player("Player 1", 130, 2 + 32 + 11 * 16);
		player2 = new Player("Player 2", 130, 2 + 32 + 14 * 16);
	}

	/**
	 * Spawns a ghost
	 * 
	 * @param index the ghosts index to respawn
	 * @param allowed the allowed map types
	 */
	@Override
	public void spawnGhost(int index, byte... allowed) {
		List<Point> locations = gameMap.getAll(allowed);
		int ind = PacmanApplication.getRND().nextInt(locations.size());
		Point p = locations.get(ind);
		System.out.println("Spawning ghost at " + p);
		
		Point2D.Float notGrid = gameMap.fromGridLocation(p);
		
		if(ghosts[index] == null)
		{
			ghosts[index] = new Ghost("Ghost", notGrid.x - 2, notGrid.y - 2);
		}else
		{
			ghosts[index].setLocation(notGrid);
		}
	}

	@Override
	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public Player getLeader() {
		if(player1.getScore() > player2.getScore())
			return player1;
		return player2;
	}
	
	/**
	 * Convienence method for calculating the loser.
	 * @return the player that is losing.
	 */
	public Player getLoser() {
		return getLeader() == player1 ? player2 : player1;
	}

	@Override
	public Player getPlayer1() {
		return player1;
	}

	@Override
	public Player getPlayer2() {
		return player2;
	}

	@Override
	public void doCustomRendering(GameContainer cont, Graphics g)
	{
		for(Ghost gh : ghosts)
		{
			if(!gh.isDead())
				gh.render(cont, g);
		}
		String left = getLeftMessage(), right = getRightMessage(), center = getCenterMessage();
		g.drawString(left, 112, 10);
		g.drawString(right, 528 - g.getFont().getWidth(right), 10);
		
		String bottom = getBottomCenterMessage();
		
		PacmanApplication.drawCenteredText(g, center, 10);
		PacmanApplication.drawCenteredText(g, bottom, 450);
	}
	
	/**
	 * Returns all of the ghosts, including dead ones
	 * 
	 * @return the list of ghosts
	 */
	public Ghost[] getGhosts()
	{
		return ghosts;
	}

	/**
	 * Returns the message that is centered above
	 * the map.
	 * @return top center message
	 */
	protected String getCenterMessage() {
		return getLeader().getName() + " leads";
	}

	/**
	 * Returns the message that is to the right above
	 * the map.
	 * @return top right message
	 */
	protected String getRightMessage() {
		return player1.getName() + ": " + player1.getScore();
	}

	/**
	 * Returns the message that is to the left above
	 * the map.
	 * @return top left message
	 */
	protected String getLeftMessage() {
		return player2.getName() + ": " + player2.getScore();
	}
	
	/**
	 * Returns the message that is centered below
	 * the map.
	 * @return bottom center message
	 */
	protected String getBottomCenterMessage() {
		return "Time So Far: " + (gameTime / 1000) + " seconds";
	}
	
	@Override
	public float getGhostRespawnChance()
	{
		return 0.005f;
	}
	
	@Override
	public void onGameOver()
	{
		PacmanApplication.application.setGUI(new GameOver(getLeader(),
				getLoser()));
	}
	
	@Override
	public GameMap getGameMap()
	{
		return gameMap;
	}

	/**
	 * Called when the players collide with each other
	 * @param time the game time
	 */
	protected void onPlayerCollide(long time) {
		specialMovement(player1, GameMap.WALL);
		specialMovement(player2, GameMap.WALL);
	}

	/**
	 * Called when a player collides with a ghost. Implementation kills the 
	 * ghost and removes 50 points from the players score.
	 * @param time the game time
	 * @param pl the player
	 * @param ghostIndex the ghosts index.
	 */
	protected void onPlayerCollideWithGhost(long time, Player pl, int ghostIndex) {
		ghosts[ghostIndex].kill(time);
		pl.addToScore(-50);
	}
}

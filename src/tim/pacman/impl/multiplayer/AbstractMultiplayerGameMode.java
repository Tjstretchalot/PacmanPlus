package tim.pacman.impl.multiplayer;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Ghost;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.network.PacmanNetworking;

public abstract class AbstractMultiplayerGameMode implements GameMode {
	private int numberGhosts;

	private PacmanNetworking network;
	
	private GameMap gameMap;

	private GhostMP[] ghosts;

	private long gameTime;

	private boolean waiting;

	private String dots;
	
	private long dotChange;

	public AbstractMultiplayerGameMode(GameMap map, PacmanNetworking networking, int numGhosts) {
		network = networking;
		gameMap = map;
		this.numberGhosts = numGhosts;
		ghosts = new GhostMP[numberGhosts];
		waiting = true;
		dots = "";
	}

	@Override
	public void doTick(long time, long delta) {
		if(waiting)
		{
			dotChange -= delta;
			if(dotChange <= 0)
			{
				dotChange = 500;
				dots += ".";
				if(dots.length() > 3)
					dots = "";
			}
			return;
		}
		gameTime += delta;
		movePlayers();
	}
	
	@Override
	public GameMap getGameMap()
	{
		return gameMap;
	}

	/**
	 * Move the players according to their velocity
	 * if they are not blocked by anything
	 */
	protected void movePlayers() {
		synchronized(network)
		{
			for(ClientMP client : network.getPlayers())
			{
				float newX = client.getLocation().x + client.getVelocity().x;
				float newY = client.getLocation().y + client.getVelocity().y;

				if(isAcceptable(client, newX, newY))
				{
					client.getLocation().x = newX;
					client.getLocation().y = newY;
				}
			}
		}
	}
	
	

	protected boolean isAcceptable(ClientMP pl, float x, float y) {
		Point onGrid = gameMap.toGridLocation(x, y);
		if(onGrid.x < 0)
			return true;
		return canWalkThrough(gameMap.getType(onGrid.x, onGrid.y), pl);
	}

	protected boolean canWalkThrough(byte type, ClientMP pl) {
		return type == GameMap.WALL || type == GameMap.SPAWNER;
	}

	@Override
	public Player getLeader() {
		int highest = Integer.MIN_VALUE;
		Player leader = null;
		synchronized(network)
		{
			for(ClientMP player : network.getPlayers())
			{
				if(player.getScore() > highest)
				{
					leader = player;
					highest = player.getScore();
				}
			}
		}
		return leader;
	}

	@Override
	public Player getPlayer1() {
		throw new AssertionError("No player one in multiplayer");
	}

	@Override
	public Player getPlayer2() {
		throw new AssertionError("No player two in multiplayer");
	}

	@Override
	public void onGameOver() {
		// TODO Get the scores from host and display them on GameOver
	}

	@Override
	public void doCustomRendering(GameContainer cont, Graphics g) {
		if(waiting)
		{
			PacmanApplication.drawCenteredText(g, "Waiting for other players" + dots, 155);
			return;
		}
		synchronized(network)
		{
			for(ClientMP player : network.getPlayers())
			{
				player.render(cont, g);
			}
		}
		
		for(GhostMP gh : ghosts)
		{
			if(gh != null)
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
		if(network.getPlayers().size() >= 3)
		{
			ClientMP player = getPlayerAt(2);
			return "3rd: " + player.getName() + " at " + player.getScore();
		}
		return "Nobody in 3rd";
	}

	/**
	 * Returns the message that is to the left above
	 * the map.
	 * @return top left message
	 */
	protected String getLeftMessage() {
		ClientMP player = getPlayerAt(1);
		return "2cd: " + player.getName() + " at " + player.getScore();
	}
	
	/**
	 * Returns the message that is centered below
	 * the map.
	 * @return bottom center message
	 */
	protected String getBottomCenterMessage() {
		return "Time So Far: " + (gameTime / 1000) + " seconds";
	}
	
	private ClientMP getPlayerAt(int pos) {
		// Order the list 
		List<ClientMP> temp = new ArrayList<ClientMP>(Arrays.asList(new ClientMP[network.getPlayers().size()]));
		Collections.copy(temp, network.getPlayers());
		return temp.get(pos);
	}

	@Override
	public int getNumberGhosts() {
		return numberGhosts;
	}

	@Override
	public Ghost[] getGhosts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void spawnGhost(int index, byte... allowed) {
		Point2D.Float location = gameMap.fromGridLocation(gameMap.chooseRandom(allowed));
		if(ghosts[index] == null)
		{
			ghosts[index] = new GhostMP("Ghost", location.x, location.y);
		}else
		{
			ghosts[index].setLocation(location);
		}
	}

	@Override
	public float getGhostRespawnChance() {
		return 0.005f;
	}

	@Override
	public void checkBoundaries(Player g) {
		throw new AssertionError("Not used in multiplayer game mode");
	}

	@Override
	public void snapToGrid(Player pl) {
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

	protected void onOrbCollect(Player pl)
	{
		pl.addToScore(10);
	}

	public boolean isWaiting()
	{
		return waiting;
	}
	
	public void setWaiting(boolean b) {
		waiting = b;
	}
}

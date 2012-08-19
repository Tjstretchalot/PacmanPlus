package tim.pacman.impl;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;

import tim.pacman.GameMap;
import tim.pacman.Player;

public class BuiltToLastMode extends OneVsOneMode {

	private static final Point PLAYER_RESPAWN = new Point(24, 14);
	private static final int MAX_DEATHS = 10;
	private int totalPlayerDeaths;

	public BuiltToLastMode(GameMap map) {
		super(map);
	}
	
	@Override
	public void doTick(long time, long delta)
	{
		super.doTick(time, delta);
	}

	@Override
	public int getNumberGhosts() {
		return 2;
	}
	
	@Override
	public float getGhostRespawnChance()
	{
		return 0.0075f;
	}

	@Override
	protected void onOrbCollect(Player pl) {
		pl.addToScore(10);
	}

	@Override
	public boolean isGameOver()
	{
		return totalPlayerDeaths > MAX_DEATHS;
	}
	
	@Override
	protected void onPlayerCollideWithGhost(long time, Player pl, int ghostIndex) {
		pl.setLocation(gameMap.fromGridLocation(PLAYER_RESPAWN));
		pl.goLeft();
		totalPlayerDeaths++;
		super.onPlayerCollideWithGhost(time, pl, ghostIndex);
	}
	
	@Override
	protected String getBottomCenterMessage()
	{
		return "Respawns Used: " + totalPlayerDeaths + " / " + MAX_DEATHS;
	}
}

package tim.pacman.impl.multiplayer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Ghost;
import tim.pacman.Player;

public class AbstractMultiplayerGameMode implements GameMode {

	private int numberGhosts;

	public AbstractMultiplayerGameMode(GameMap map, int numGhosts) {
		this.numberGhosts = numGhosts;
	}

	@Override
	public void doTick(long time, long delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Player getLeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player getPlayer1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player getPlayer2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onGameOver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doCustomRendering(GameContainer cont, Graphics g) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getGhostRespawnChance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void checkBoundaries(Player g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void snapToGrid(Player g) {
		// TODO Auto-generated method stub
		
	}

	protected void onOrbCollect(Player pl)
	{
		pl.addToScore(10);
	}
}

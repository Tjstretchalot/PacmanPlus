package tim.pacman.impl.multiplayer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.pacman.AbstractGameMode;
import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Ghost;
import tim.pacman.Player;

public class FreeForAllMode extends AbstractMultiplayerGameMode {
	private int numberGhosts;
	
	public FreeForAllMode(GameMap map, int numberGhosts) {
		super(map, numberGhosts);
		
	}

	@Override
	public void doTick(long time, long delta) {
		super.doTick(time, delta);
	}

	@Override
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		return false;
	}
}

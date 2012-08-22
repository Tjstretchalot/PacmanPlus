package tim.pacman.impl.multiplayer;

import tim.pacman.GameMap;
import tim.pacman.network.PacmanNetworking;

public class MultiplayerBuiltToLast extends AbstractMultiplayerGameMode {

	public MultiplayerBuiltToLast(GameMap map, PacmanNetworking networking, int numGhosts) {
		super(map, networking, numGhosts);
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

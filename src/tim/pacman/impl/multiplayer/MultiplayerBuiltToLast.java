package tim.pacman.impl.multiplayer;

import tim.pacman.GameMap;

public class MultiplayerBuiltToLast extends AbstractMultiplayerGameMode {

	public MultiplayerBuiltToLast(GameMap map, int numGhosts) {
		super(map, numGhosts);
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

package tim.pacman.impl.multiplayer;

import tim.pacman.GameMap;
import tim.pacman.network.PacmanNetworking;

public class MultiplayerTagMode extends AbstractMultiplayerGameMode {

	public MultiplayerTagMode(GameMap map, PacmanNetworking networking, int numGhosts) {
		super(map, networking, numGhosts);
	}

	@Override
	public boolean isGameOver() {
		// TODO Auto-generated method stub
		return false;
	}

}

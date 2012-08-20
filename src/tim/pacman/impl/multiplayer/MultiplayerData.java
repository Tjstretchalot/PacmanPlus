package tim.pacman.impl.multiplayer;

import tim.pacman.GameMode;
import tim.pacman.impl.ai.AStarGameMap;

public class MultiplayerData {
	public static final String[] MULTIPLAYER_MODES = new String[] {
		"Free For All",
		"Everyone fights it out to reach 1000 points",
		
		"Built to Last",
		"Avoid ghosts while attempting to get points before running out of respawns",
		
		"Tag Mode",
		"A friendly game of tag"
	};
	private static final int FREE_FOR_ALL = 0;
	private static final int BUILT_TO_LAST = 1;
	private static final int TAG_MODE = 2;
	
	public static final String nameOf(GameMode gameMode)
	{
		return MULTIPLAYER_MODES[indexOf(gameMode) * 2];
	}
	
	public static final String infoOf(GameMode gameMode)
	{
		return MULTIPLAYER_MODES[indexOf(gameMode) * 2 + 1];
	}

	public static int indexOf(GameMode gameMode) {
		if(gameMode instanceof FreeForAllMode)
			return FREE_FOR_ALL;
		else if(gameMode instanceof MultiplayerBuiltToLast)
			return BUILT_TO_LAST;
		else if(gameMode instanceof MultiplayerTagMode)
			return TAG_MODE;
		throw new AssertionError("Unknown type '" + gameMode.getClass().getName() + "'");
	}

	public static GameMode createInstance(byte b, int numGhosts, AStarGameMap gameMap) {
		switch(b)
		{
		case 0:
			return new FreeForAllMode(gameMap, numGhosts);
		case 1:
			return new MultiplayerBuiltToLast(gameMap, numGhosts);
		case 2:
			return new MultiplayerTagMode(gameMap, numGhosts);
		}
		return null;
	}
}

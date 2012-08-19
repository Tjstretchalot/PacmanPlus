package tim.pacman.impl;

import org.lwjgl.input.Keyboard;

import tim.pacman.AbstractGameControls;
import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Player;
import tim.pacman.impl.ai.AStarController;
import tim.pacman.impl.ai.AStarGameMap;

public class HumanAgainstAIControls extends AbstractGameControls {
	private AIController aiController;
	public HumanAgainstAIControls(GameMode gameMode, AStarGameMap gameMap) {
		super(gameMode);
		aiController = new AStarController(getPlayer2(), getPlayer1(), gameMode, gameMap);
	}
	
	@Override
	public void doTick(long time) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP))
			getPlayer1().goUp();
		else if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			getPlayer1().goDown();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			getPlayer1().goLeft();
		else if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			getPlayer1().goRight();
		
		aiController.doTick(time);
	}

}

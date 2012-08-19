package tim.pacman.impl;

import static org.lwjgl.input.Keyboard.*;

import tim.pacman.AbstractGameControls;
import tim.pacman.GameControls;
import tim.pacman.GameMode;

public class MultiplayerSingleKeyboardControls extends AbstractGameControls {
	public MultiplayerSingleKeyboardControls(GameMode gameMode) {
		super(gameMode);
	}

	@Override
	public void doTick(long time) {
		if(isKeyDown(KEY_W))
		{
			getPlayer1().goUp();
		}else if(isKeyDown(KEY_S))
		{
			getPlayer1().goDown();
		}
		
		if(isKeyDown(KEY_A))
		{
			getPlayer1().goLeft();
		}else if(isKeyDown(KEY_D))
		{
			getPlayer1().goRight();
		}
		
		if(isKeyDown(KEY_LEFT))
		{
			getPlayer2().goLeft();
		}else if(isKeyDown(KEY_RIGHT))
		{
			getPlayer2().goRight();
		}
		
		if(isKeyDown(KEY_UP))
			getPlayer2().goUp();
		else if(isKeyDown(KEY_DOWN))
			getPlayer2().goDown();
	}
	
}

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
			setPlayer1Up();
		}else if(isKeyDown(KEY_S))
		{
			setPlayer1Down();
		}
		
		if(isKeyDown(KEY_A))
		{
			setPlayer1Left();
		}else if(isKeyDown(KEY_D))
		{
			setPlayer1Right();
		}
		
		if(isKeyDown(KEY_LEFT))
		{
			setPlayer2Left();
		}else if(isKeyDown(KEY_RIGHT))
		{
			setPlayer2Right();
		}
		
		if(isKeyDown(KEY_UP))
			setPlayer2Up();
		else if(isKeyDown(KEY_DOWN))
			setPlayer2Down();
	}
	
}

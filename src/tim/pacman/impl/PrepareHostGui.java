package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static tim.pacman.PacmanApplication.drawCenteredHText;
import static tim.pacman.PacmanApplication.drawCenteredText;
import static tim.pacman.impl.multiplayer.MultiplayerData.MULTIPLAYER_MODES;
import static org.lwjgl.input.Keyboard.*;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.impl.ai.AStarGameMap;
import tim.pacman.impl.multiplayer.AbstractMultiplayerGameMode;
import tim.pacman.impl.multiplayer.FreeForAllMode;
import tim.pacman.impl.multiplayer.MultiplayerBuiltToLast;
import tim.pacman.impl.multiplayer.MultiplayerData;
import tim.pacman.impl.multiplayer.MultiplayerTagMode;
import tim.pacman.network.HostNetworking;
import tim.pacman.network.LANGame;

/**
 * The gui for attempting to find and connect to 
 * other LAN Pacman Plus games. Consists of a list
 * of all known games and a return game
 * 
 * @author Timothy
 */
public class PrepareHostGui implements Gui {
	private static boolean addedDelims;
	
	
	private List<LANGame> games;
	private byte currentOption;
	
	private int mode;
	private int maxPlayers;
	private int numGhosts;
	
	private boolean blinkOn;
	private long blinkSwitch;
	private long canSwitch;
	private String currentName;
	
	
	public PrepareHostGui()
	{
		games = new ArrayList<LANGame>();
		mode = 0;
		maxPlayers = 3;
		numGhosts = 1;
		currentName = "Player " + (PacmanApplication.getRND().nextInt(999) + 1);
		
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		if(!addedDelims)
		{
			for(int i = 1; i + 1 < MULTIPLAYER_MODES.length; i += 2)
			{
				MULTIPLAYER_MODES[i] = PacmanApplication.addDelimiters(graphics, MULTIPLAYER_MODES[i], 480);
			}
		}
		
		boolean chScreen = PacmanApplication.getTime() - PacmanApplication.application.getLastChange() > 500;
		PacmanApplication.application.getLogo().bind();
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2f(64f, 20f);
		
			glTexCoord2f(1, 0);
			glVertex2f(576f, 20f);
		
			glTexCoord2f(1, 1);
			glVertex2f(576f, 148f);
		
			glTexCoord2f(0, 1);
			glVertex2f(64f, 148f);
		glEnd();
		
		float y = 175f;
		String msg = MULTIPLAYER_MODES[mode * 2];
		
		if(currentOption == 0 && blinkOn)
		{
			msg = " > " + msg + " < ";
		}
		drawCenteredText(graphics, msg, y);
		y += 25f;
		
		msg = MULTIPLAYER_MODES[mode * 2 + 1];
		String[] lines = msg.split("\n");
		for(String str : lines)
		{
			drawCenteredText(graphics, str, y);
			y += graphics.getFont().getHeight(str) + 5;
		}
		y += 20f;
		
		msg = "Max Players: " + maxPlayers;
		if(currentOption == 1 && blinkOn)
		{
			msg = " > " + msg + " < ";
		}
		drawCenteredText(graphics, msg, y);
		y += 50f;
		
		msg = "Number of Ghosts: " + numGhosts;
		if(currentOption == 2 && blinkOn)
		{
			msg = " > " + msg + " < ";
		}
		drawCenteredText(graphics, msg, y);
		y += 50f;
		
		boolean chName = drawCenteredHText(graphics, "Name: " + currentName, y);
		if(chName && chScreen)
		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					currentName = JOptionPane.showInputDialog(null, "What should your name be?");
				}
				
			}).start();
			PacmanApplication.application.setLastChange(PacmanApplication.getTime());
		}
		y += 50f;
		
		boolean done = drawCenteredHText(graphics, "Done", y);
		if(done && chScreen)
		{
			AStarGameMap map = new AStarGameMap(GameMap.GAME_MAP);
			AbstractMultiplayerGameMode gMode = null;
			
			HostNetworking networking = new HostNetworking(gMode, maxPlayers, numGhosts, currentName);
			gMode = MultiplayerData.createInstance((byte) mode, networking, numGhosts, map);
			networking.gameMode = gMode;
			
			PacmanApplication.application.setGUI(new LobbyGui(networking, gMode, true, MULTIPLAYER_MODES[mode * 2], 
					MULTIPLAYER_MODES[mode * 2 + 1], numGhosts, maxPlayers, currentName, this));
		}
		
		y += 50f;
		
		boolean back = drawCenteredHText(graphics, "Go Back", y);
		if(back && chScreen)
			PacmanApplication.application.setGUI(new MultiplayerGui());
	}

	@Override
	public void update(GameContainer cont, int delta) {
		blinkSwitch -= delta;
		if(blinkSwitch <= 0)
		{
			blinkSwitch = 500;
			blinkOn = !blinkOn;
		}
		
		if(canSwitch > 0)
		{
			canSwitch -= delta;
			if(canSwitch > 0)
				return;
		}
		
		if(isKeyDown(KEY_UP))
		{
			currentOption--;
			if(currentOption < 0)
				currentOption = 2;
			canSwitch = 150;
		}else if(isKeyDown(KEY_DOWN))
		{
			currentOption++;
			if(currentOption > 2)
				currentOption = 0;
			canSwitch = 150;
		}else if(isKeyDown(KEY_LEFT))
		{
			switch(currentOption)
			{
			case 0:
				mode--;
				if(mode < 0)
					mode = 2;
				break;
			case 1:
				maxPlayers--;
				if(maxPlayers < 2)
					maxPlayers = 5;
				break;
			case 2:
				numGhosts--;
				if(numGhosts < 0)
					numGhosts = 5;
				break;
			}
			canSwitch = 150;
		}else if(isKeyDown(KEY_RIGHT))
		{
			switch(currentOption)
			{
			case 0:
				mode++;
				if(mode > 2)
					mode = 0;
				break;
			case 1:
				maxPlayers++;
				if(maxPlayers > 5)
					maxPlayers = 2;
				break;
			case 2:
				numGhosts++;
				if(numGhosts > 5)
					numGhosts = 0;
				break;
			}
			canSwitch = 150;
		}
	}

}

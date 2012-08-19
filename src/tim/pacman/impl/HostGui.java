package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static tim.pacman.PacmanApplication.drawCenteredHText;
import static tim.pacman.PacmanApplication.drawCenteredText;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.pacman.GameMode;
import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.network.HostNetworking;
import tim.pacman.network.LANGame;
import tim.pacman.network.PacmanNetworking;

/**
 * The gui for attempting to find and connect to 
 * other LAN Pacman Plus games. Consists of a list
 * of all known games and a return game
 * 
 * @author Timothy
 */
public class HostGui implements Gui {
	private HostNetworking network;
	private GameMode gameMode;
	private PrepareHostGui prepareHostGUI;
	
	public HostGui(GameMode gameMode, PrepareHostGui prepareHostGUI)
	{
		network = new HostNetworking(gameMode);
		this.gameMode = gameMode;
		this.prepareHostGUI = prepareHostGUI;
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
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
		y += 25;
		drawCenteredText(graphics, "Sorry, this hasn't been implemented yet :(", y);
		
		
		y += 50;
		boolean back = drawCenteredHText(graphics, "Go Back", y);
		if(back && chScreen)
		{
			network.cleanup();
			PacmanApplication.application.setGUI(prepareHostGUI);
		}
	}

	@Override
	public void update(GameContainer cont, int delta) {
		
	}

}

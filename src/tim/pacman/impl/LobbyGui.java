package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static tim.pacman.PacmanApplication.drawCenteredText;
import static tim.pacman.PacmanApplication.drawHighlightableText;
import static tim.pacman.PacmanApplication.drawText;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.pacman.GameMode;
import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.network.HostNetworking;
import tim.pacman.network.PacmanNetworking;

/**
 * The gui for attempting to find and connect to 
 * other LAN Pacman Plus games. Consists of a list
 * of all known games and a return game
 * 
 * @author Timothy
 */
public class LobbyGui implements Gui {
	private PacmanNetworking network;
	private GameMode gameMode;
	private Gui previousGUI;
	private Player playerSelected;
	private int numGhosts;
	private int maxPlayers;
	private String mode;
	private String modeInfo;
	private boolean addedDelims;
	private boolean host;
	
	public LobbyGui(PacmanNetworking network, GameMode gameMode, boolean host, String mode, String modeInfo, 
			int numGhosts, int maxPlayers, String plName, Gui previousGUI)
	{
		this.network = network;
		this.gameMode = gameMode;
		this.previousGUI = previousGUI;
		this.numGhosts = numGhosts;
		this.maxPlayers = maxPlayers;
		this.mode = mode;
		this.modeInfo = modeInfo;
		this.host = host;
	}
	
	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		if(!addedDelims)
		{
			modeInfo = PacmanApplication.addDelimiters(graphics, modeInfo, 250);
			addedDelims = true;
		}
		boolean clickable = PacmanApplication.getTime() - PacmanApplication.application.getLastChange() > 500;
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
		float y = 15f;
		drawCenteredText(graphics, "Lobby", y);

		y = 225f;
		float x = 15f;
		float leftX = graphics.getFont().getWidth("Kick Player");
		float temp = graphics.getFont().getWidth("Start Game");
		if(temp > leftX)
			leftX = temp;
		
		if(host)
		{
			if(playerSelected != null)
			{
				boolean kick = drawHighlightableText(graphics, "Kick Player", x, y);
				if(kick && clickable)
				{
					((HostNetworking) network).kickPlayer(playerSelected);
				}
			}else
				drawText(graphics, "Kick Player", x, y);
			y += 50;

			if(network.getPlayers().size() >= 2)
			{
				boolean start = drawHighlightableText(graphics, "Start Game", x, y);

				if(start && clickable)
				{
					((HostNetworking) network).startGame();
					// TODO Game mode etc..
				}
			}else
				drawText(graphics, "Start Game", x, y);
		}
		
		
		y += 50;
		boolean back = drawHighlightableText(graphics, "Go Back", x, y);
		if(back && clickable && host)
		{
			((HostNetworking) network).cleanup();
			PacmanApplication.application.setGUI(previousGUI);
		}
		
		leftX += 5;
		float rightX = Display.getWidth() - 215f;
		y = 175f;
		PacmanApplication.drawCenteredText(graphics, "Players", leftX, rightX, y);
		PacmanApplication.drawCenteredText(graphics, "Players", leftX, rightX, y);
		y += 50f;
		
		synchronized(network.getPlayers())
		{
			for(Player pl : network.getPlayers())
			{
				boolean select = PacmanApplication.drawCenteredHText(graphics, pl.getName(), leftX, rightX, y);
				if(select && clickable)
					playerSelected = pl;
				y += 25f;
			}
		}
		
		y = 185f;
		leftX = Display.getWidth() - 215f;
		rightX = Display.getWidth();
		
		PacmanApplication.drawCenteredText(graphics, "Game Info", leftX, rightX, y);
		PacmanApplication.drawCenteredText(graphics, "Game Info", leftX, rightX, y);
		y += 50f;
		PacmanApplication.drawCenteredText(graphics, mode, leftX, rightX, y);
		y += 25f;
		
		String[] modeData = modeInfo.split("\n");
		for(int i = 0; i < modeData.length; i++)
		{
			PacmanApplication.drawCenteredText(graphics, modeData[i], leftX, rightX, y);
			y += 20f;
		}
		
		y += 30f;
		PacmanApplication.drawCenteredText(graphics, "Max Players: " + maxPlayers, leftX, rightX, y);
		
		y += 50f;
		PacmanApplication.drawCenteredText(graphics, "Number of Ghosts: " + numGhosts, leftX, rightX, y);
	}

	@Override
	public void update(GameContainer cont, int delta) {
		
	}

}

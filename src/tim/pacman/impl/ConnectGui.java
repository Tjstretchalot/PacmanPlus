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

import javax.swing.JOptionPane;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.network.LANGame;
import tim.pacman.network.PacmanNetworking;

/**
 * The gui for attempting to find and connect to 
 * other LAN Pacman Plus games. Consists of a list
 * of all known games and a return game
 * 
 * @author Timothy
 */
public class ConnectGui implements Gui {
	private List<LANGame> games;
	private Thread searchThread;
	private long timeUntilDotChange;
	private String dots;
	private String playerName;
	
	public ConnectGui()
	{
		games = new ArrayList<LANGame>();
		searchThread = new Thread(new Runnable() {

			@Override
			public void run() {
				PacmanNetworking.scanForLocalGames(games);
			}
			
		});
		searchThread.start();
		dots = "";
		playerName = "Player " + (PacmanApplication.getRND().nextInt(999) + 1);
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
		
		float y = 175;
		
		boolean chName = drawCenteredHText(graphics, "Name: " + playerName, y);
		if(chName && chScreen)
		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					playerName = JOptionPane.showInputDialog(null, "What should your name be?");
				}
				
			}).start();
			PacmanApplication.application.setLastChange(PacmanApplication.getTime());
		}
		y += 50f;
		if(searchThread.isAlive())
		{
			drawCenteredText(graphics, "Scanning" + dots, y);
			y += 25;
		}
		if(games.size() == 0)
		{
			drawCenteredText(graphics, "No games found..", y);
			y += 25;
		}
		for(LANGame game : games)
		{
			y += 25;
			boolean connect = drawCenteredHText(graphics, game.getName() + " - " + game.getAddress(), y);
			if(connect && chScreen)
			{
				if(searchThread.isAlive())
					searchThread.suspend();
				PacmanNetworking.doConnect(game, playerName, this);
			}
		}
		
		y += 50;
		boolean back = drawCenteredHText(graphics, "Go Back", y);
		if(back && chScreen)
			PacmanApplication.application.setGUI(new MultiplayerGui());
	}

	@Override
	public void update(GameContainer cont, int delta) {
		timeUntilDotChange -= delta;
		
		if(timeUntilDotChange <= 0)
		{
			dots += ".";
			if(dots.length() > 3)
				dots = "";
			timeUntilDotChange = 500l;
		}
	}

}

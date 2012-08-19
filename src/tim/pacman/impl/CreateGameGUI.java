package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import javax.swing.JOptionPane;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import tim.pacman.GameControls;
import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.impl.ai.AStarGameMap;
import tim.pacman.impl.ai.AStarGhostControls;

/**
 * The GUI for creating a game.
 * @author Timothy
 */
public class CreateGameGUI implements Gui {

	private int gameType;

	private Texture logo;
	
	private static final long MINIMUM_DELAY = 200;
	
	public static final String[] MODES = new String[] {
		"Built to last",
		"Try to avoid the monsters in the middle while aquiring points",
		"1 v 1",
		"Classic pacman, play in multiplayer mode against another person or AI, first to 2000 points wins.",
		"Tag",
		"Try to tag the other person, the longer you are not it the more points you get. " +
				" The person with the most points at end of game wins"
	};
	
	private int mode;
	private boolean showSign;
	private long nextToggle;

	private long lastPress;

	private boolean addedDelimiters;

	/**
	 * Creates the gui.
	 * @param type the game mode, where
	 * <ul>
	 * <li>0 is Singleplayer</li>
	 * <li>1 is Multiplayer on this keyboard</li>
	 * </ul>
	 */
	public CreateGameGUI(int type, Texture logo)
	{
		gameType = type;
		this.logo = logo;
		mode = 0;
		showSign = true;
		nextToggle = PacmanApplication.getTime() + 500;
	}

	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		if(!addedDelimiters)
		{
			for(int i = 1; i < MODES.length; i += 2)
			{
				MODES[i] = PacmanApplication.addDelimiters(graphics, MODES[i], 480);
			}
			addedDelimiters = true;
		}
		
		logo.bind();

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
		
		String m = "Game Mode " + (showSign ? "> " : "  ");
		m += MODES[mode * 2];
		
		float y = 175f;
		y += PacmanApplication.drawCenteredText(graphics, m, y).height + 15;
		
		String[] spl = MODES[mode * 2 + 1].split("\n");
		for(String str : spl)
		{
			y += PacmanApplication.drawCenteredText(graphics, str, y).height + 5;
		}
		
		y += 55f;
		boolean returnToMainMenu = PacmanApplication.drawCenteredHText(graphics, "Return to main menu", y);
		if(returnToMainMenu)
			PacmanApplication.application.setGUI(new MainMenuGui());
	}
	
	@Override
	public void update(GameContainer cont, int delta)
	{
		long time = PacmanApplication.getTime();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_RETURN) && time - lastPress > MINIMUM_DELAY)
		{
			System.out.println(MODES[mode * 2] + " was selected");
			
			GameMap gameMap = new AStarGameMap(GameMap.GAME_MAP);
			GameMode gameMode = null;
			GameControls gameControls = null;
			
			
			
			if(mode == 1)
				gameMode = new OneVsOneMode(gameMap);
			else if(mode == 2)
				gameMode = new TagMode(gameMap);
			else if(mode == 0)
				gameMode = new BuiltToLastMode(gameMap);
			else
			{
				return;
			}
			
			if(gameType == 0)
			{
				gameControls = new HumanAgainstAIControls(gameMode, (AStarGameMap) gameMap);
			}
			else if(gameType == 1)
				gameControls = new MultiplayerSingleKeyboardControls(gameMode);
			
			PacmanApplication.application.playGame(gameMode, gameMap, gameControls, new AStarGhostControls(gameMode, (AStarGameMap) gameMap));
			lastPress = time;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) && time - lastPress > MINIMUM_DELAY)
		{
			mode--;
			lastPress = time;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)&& time - lastPress > MINIMUM_DELAY)
		{
			mode++;
			lastPress = time;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_B) && time - lastPress > MINIMUM_DELAY)
		{
			PacmanApplication.application.setGUI(new MainMenuGui(logo));
		}
		
		if(mode < 0)
			mode = MODES.length / 2 - 1;
		if(mode >= MODES.length / 2)
			mode = 0;
		
		if(time >= nextToggle)
		{
			showSign = !showSign;
			nextToggle = time + 500;
		}
	}

}

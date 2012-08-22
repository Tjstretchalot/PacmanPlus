package tim.pacman;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.Vector;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import tim.pacman.impl.MainMenuGui;
import tim.pacman.impl.ai.AStarGhostControls;
import tim.pacman.impl.multiplayer.AbstractMultiplayerGameMode;

/*
 * The main goal of this application is ability to add more 
 * game modes.  Hence a good hierarchy is in place.
 * 
 * GameControls - This interface must be implemented for different
 * game controls, such as adding a server game mode where the opponent
 * is chosen randomly
 * 
 * AbstractGameControls - A basic implementation, which can be extended 
 * for ease of use
 * 
 * GameMode - This interface is for adding different game modes.  Such a class
 * would contain all of the player data and decide what happens.  Also can modify
 * player speeds before actually moving the player
 * 
 * AbstractGameMode - Basic implementation of game mode, implementing basic movement
 * etc.
 * 
 * GameMap - Basic representation of a map.  No real reason to extend it, the constructor
 * works fine.
 */
/**
 * The entry point for the pac man plus, a pacman clone with
 * a few more features.
 * 
 * <ul>
 * 
 * <li>
 * Multiplayer mode on one keyboard -> Use WASD for player one, 
 * and arrow keys for player two.
 * </li>
 * 
 * <li>
 * Multiplayer mode on LAN -> Detects any LAN connections for the 
 * pacman port.
 * </li>
 * 
 * Game Modes
 * <ul>
 *   <li>Built to Last - Try to avoid the monsters in the middle, which
 *   only get more dangerous!</li>
 *   
 *   <li>1 v 1: Classic pacman, play in multiplayer mode against another 
 *   person and attempt to survive longer</li>
 *   
 *   <li>Tag: Try to tag the other person, the longer you are not it the
 *   more points you get.  The person with the most points at end of game wins</li>
 * </ul>
 * 
 * @author Timothy
 */
public class PacmanApplication extends BasicGame {

	/**
	 * The instance of the application
	 */
	public static PacmanApplication application = new PacmanApplication();
	
	/**
	 * The website regular expression used for determining if a noncommented string is
	 * a website.  Used internally for making websites clickable
	 */
	public static final String WEBSITE_REGEX = "^(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\ "
			+ "-\\.\\?\\,\\'\\/\\+&amp;%\\$#_]*)?$";
	
	/**
	 * The email regular expression used. Not compliant to pretty much anything
	 */
	public static final String EMAIL_REGEX = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";

	private static Random gen;

	private GameMode gameMode;
	private GameMap theMap;
	private GameControls controls;

	private volatile Gui gui;
	private volatile Gui nextGui;

	private UnicodeFont font;

	private Texture logo;
	private GhostController ghostControls;

	private long lastChange;
	private static long linkLastPressed;


	/**
	 * Prepares the pacman application under
	 * the title 'Pacman Plus'
	 */
	public PacmanApplication()
	{
		super("Pacman Plus");
		// This does not start the program.
	}

	/**
	 * @return Pacman Plus
	 */
	@Override
	public String getTitle() {
		return "Pacman Plus";
	}

	@Override
	public void init(GameContainer arg0) throws SlickException {
		try {
			InputStream stream = new FileInputStream(new File("res/logo.png"));
			logo = TextureLoader.getTexture("PNG", stream);
		} catch (IOException ex) {
			Sys.alert(getTitle(), "Could not load resources - " + ex.getCause());
			ex.printStackTrace();
		}
		application = this;
		
		gui = new MainMenuGui();
		gameMode = null;
		theMap = null;
		controls = null;
		
		java.awt.Font awtFont = new java.awt.Font("SansSerif", Font.BOLD, 24);
		Player.loadImage();
		font = new UnicodeFont(awtFont, 24, true, false);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 640, 480, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_TEXTURE_2D);
	}

	@Override
	public void render(GameContainer cont, Graphics g) throws SlickException {
		synchronized(this)
		{
			if(gui != null)
				gui.render(cont, g);
		}
		
		if(gui == null)
		{
			assert gameMode != null : "Game mode is null and not in a gui!";
			assert theMap != null : "Map is null and not in a gui!";
			assert controls != null : "Controls are null and not in a gui!";
			
			
//			glClearColor(0.0f, 0.2f, 0.2f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
			
			theMap.render(cont, g);
			if(!(gameMode instanceof AbstractMultiplayerGameMode))
			{
				gameMode.getPlayer1().render(cont, g);
				gameMode.getPlayer2().render(cont, g);
			}
			
			gameMode.doCustomRendering(cont, g);
		}
		
		Display.sync(300);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		
		if(gameMode != null && gameMode.isGameOver())
		{
			gameMode.onGameOver();
			gameMode = null;
			theMap = null;
			controls = null;
		}
		synchronized(this)
		{
			if(nextGui != null)
			{
				System.out.println("GUI -> " + nextGui.getClass().getSimpleName());
				gui = nextGui;
				nextGui = null;
				
			}
		}
		
		if(gui != null)
			gui.update(cont, delta);
		else
		{
			long time = getTime();
			
			gameMode.doTick(time, delta);
			controls.doTick(time);
			
			if(ghostControls != null)
				ghostControls.doTick(time);
		}
	}
	
	/**
	 * Runs the application
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
        	application = new PacmanApplication();
            AppGameContainer app = new AppGameContainer(application);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

	/**
	 * Draws the specified text in the center of the screen 
	 * at the specified y coordinate.  If the text contains
	 * any websites or emails, they will be made clickable.
	 * @param graphics the graphics to use for drawing
	 * @param txt the text
	 * @param y the y coordinate
	 * @return the location where the text is
	 */
	public static Rectangle2D.Float drawCenteredText(Graphics graphics, String txt, float y) {
		float leftX = 0f;
		float rightX = Display.getWidth();
		return drawCenteredText(graphics, txt, leftX, rightX, y);
	}
	
	/**
	 * Draws the specified text in the center of the screen and 
	 * returns if it is being clicked. Same as drawCenteredText(graphics, txt, 0, Display.getWidth(), y)
	 * @param graphics the graphics
	 * @param txt the text
	 * @param y the y coordinate
	 * @return if it is being pressed
	 */
	public static boolean drawCenteredHText(Graphics graphics, String txt, float y)
	{
		return drawCenteredHText(graphics, txt, 0, Display.getWidth(), y);
	}
	
	/**
	 * Draws a centered text, except rather than using 0 and 
	 * the screen width it uses set values.
	 * @param graphics  the graphics
	 * @param txt the text
	 * @param leftX the left x
	 * @param rightX the right x
	 * @param y the y coordinate
	 * @return if it is being clicked
	 */
	public static boolean drawCenteredHText(Graphics graphics, String txt, float leftX, float rightX, float y)
	{
		float width = graphics.getFont().getWidth(txt);
		float x = leftX + ((rightX - leftX) / 2 - width / 2);
		return drawHighlightableText(graphics, txt, x, y);
	}
	
	/**
	 * Draws the specified text at the specified coordinate.  If
	 * the text contains any websites or emails they will be highlighted
	 * and upon being pressed the appropriate website or mailto address
	 * will be opened.
	 * 
	 * @param graphics the graphics
	 * @param txt the text
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the location where the text is being drawn
	 */
	public static Rectangle2D.Float drawText(Graphics graphics, String txt, float x, float y)
	{
		String[] split = txt.split(" ");
		float width = graphics.getFont().getWidth(txt);
		float height = graphics.getFont().getHeight(txt);
		
		for(String str : split)
		{
			if(!isWebsite(str) && !isEmail(str))
			{
				graphics.drawString(str + " ", x, y);
				x += graphics.getFont().getWidth(str + " ") + 5;
			}else
			{
				if(isWebsite(str))
				{
					boolean goToWebsite = drawHighlightableText(graphics, str + " ", x, y);

					if(goToWebsite && getTime() - linkLastPressed >= 1000)
					{
						Sys.openURL(str);
						linkLastPressed = getTime();
					}
				}else if(isEmail(str))
				{
					boolean goToEmail = drawHighlightableText(graphics, str + " ", x, y);
					
					if(goToEmail && getTime() - linkLastPressed >= 1000)
					{
						Sys.openURL("mailto:" + str);
						linkLastPressed = getTime();
					}
				}
				x += graphics.getFont().getWidth(str + " ") + 5;
				
			}
		}
		
		Rectangle2D.Float res = new Rectangle2D.Float();
		res.x = x;
		res.y = y;
		res.width = width;
		res.height = height;
		
		return res;
	}
	
	private static boolean isEmail(String str) {
		return str.matches(EMAIL_REGEX);
	}

	/**
	 * Same as the global one but does not highlight websites if told not to
	 * @param graphics the graphics
	 * @param txt the text to write
	 * @param x xcoord
	 * @param y ycoord 
	 * @param b highlight website
	 * @return the rectangle the text is located in
	 */
	public static Rectangle2D.Float drawText(Graphics graphics, String txt, float x, float y, boolean b)
	{
		if(b)
			return drawText(graphics, txt, x, y);
		
		float width = graphics.getFont().getWidth(txt);
		float height = graphics.getFont().getHeight(txt);
		
		graphics.drawString(txt, x, y);
		
		Rectangle2D.Float res = new Rectangle2D.Float();
		res.x = x;
		res.y = y;
		res.width = width;
		res.height = height;
		
		return res;
	}
	
	private static boolean isWebsite(String txt) {
		return txt.matches(WEBSITE_REGEX);
	}

	/**
	 * Draws a text that gets brighter if the mouse is hovering on it
	 * @param graphics the graphics
	 * @param text the text
	 * @param x x location
	 * @param y y location
	 * @return if the text is being clicked
	 */
	public static boolean drawHighlightableText(Graphics graphics, String text, float x, float y)
	{
		Rectangle2D.Float rect = drawText(graphics, text, x, y, false);
		if(rect.contains(Mouse.getX(), (Display.getHeight() - Mouse.getY())))
		{
			if(Mouse.isButtonDown(0)) 
			{
				graphics.setColor(Color.yellow);
				graphics.drawString(text, rect.x, rect.y);
				graphics.setColor(Color.white);
				return true;
			}else
				drawText(graphics, text, x, y, false); // Double drawing gives a great effect
		}
		return false;
	}
	
	/**
	 * Sets the gui.  Will not be changed until the 
	 * next update tick.
	 * @param newGui the new gui
	 */
	public synchronized void setGUI(final Gui newGui)
	{
		nextGui = newGui;
		lastChange = getTime();
	}
	
	/**
	 * Sets the specified game as the new gui.  
	 * @param gameMode the game mode
	 * @param gameMap the game map
	 * @param gameControls the game controls
	 * @param ghostControls the ghost controls
	 */
	public void playGame(GameMode gameMode, GameMap gameMap, GameControls gameControls, GhostController ghostControls)
	{
		if(gameMode == null)
			throw new IllegalArgumentException("Game mode cannot be null");
		if(gameMap == null)
			throw new IllegalArgumentException("Game map cannot be null");
		if(gameControls == null)
			throw new IllegalArgumentException("Game controls cannot be null");
		this.gameMode = gameMode;
		this.theMap = gameMap;
		this.controls = gameControls;
		this.ghostControls = ghostControls;
		
		synchronized(this) {
			gui = null;
			lastChange = getTime();
		}
	}
	
	/**
	 * Returns the time in milliseconds
	 * @return the time in milliseconds
	 */
	public static long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Adds delimiters to a string based off of the specified graphics
	 * 
	 * @param graphics the graphics
	 * @param string the string
	 * @param i maximum width
	 * @return the result
	 */
	public static String addDelimiters(Graphics graphics, String string, int i) {
		if(graphics.getFont().getWidth(string) <= i || string.split(" ").length == 1)
			return string;
		
//		System.out.println("Width of '" + string + "': " + graphics.getFont().getWidth(string));
		
		String[] splt = string.split(" ");
		String newString = "";
		
		int counter = -1;
		String temp = null;
		while(graphics.getFont().getWidth(newString) < i && (counter + 1) < splt.length)
		{
			counter++;
			temp = newString;
			newString += splt[counter] + " ";
//			System.out.println("String is now '" + newString + "', length: " + graphics.getFont().getWidth(newString));
		}
		newString = temp;
//		System.out.println("End result is: " + newString);
		
		return newString + "\n" + addDelimiters(graphics, string.substring(newString.length()), i);
	}

	/**
	 * Returns the random number generator used
	 * @return the random number generator
	 */
	public static Random getRND() {
		if(gen == null)
			gen = new Random();
		return gen;
	}

	/**
	 * Returns the logo used at the top of most interfaces
	 * @return the logo
	 */
	public Texture getLogo() {
		return logo;
	}

	public long getLastChange() {
		return lastChange;
	}

	public void setLastChange(long time) {
		lastChange = time;
	}

	/**
	 * Draws centered text where leftX is the left 
	 * of the center and right x is the right of the screen
	 * @param graphics the graphics
	 * @param txt the string
	 * @param leftX left x
	 * @param rightX right x
	 */
	public static Rectangle2D.Float drawCenteredText(Graphics graphics, String txt,
			float leftX, float rightX, float y) {
		float width = graphics.getFont().getWidth(txt);
		float x = leftX + ((rightX - leftX) / 2 - width / 2);
		
		return drawText(graphics, txt, x, y);
	}

}

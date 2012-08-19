package tim.pacman;

import static org.lwjgl.opengl.GL11.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Player {
	
	private static Texture image;
	private Point2D.Float location;
	private Point2D.Float velocity;
	private int score;
	private float rotation;
	private String name;
	private Rectangle2D.Float rect;
	
	public static final float WIDTH = 12;
	public static final float HEIGHT = 12;
	
	public Player(String nm, float x, float y)
	{
		setLocation(new Point2D.Float(x, y));
		setVelocity(new Point2D.Float());
		setRotation(0f);
		name = nm;
		
		score = 0;
	}

	public int getScore() {
		return score;
	}
	
	public void setScore(int newScore)
	{
		score = newScore;
	}
	
	public void addToScore(int amount)
	{
		score += amount;
	}

	public Point2D.Float getLocation() {
		return location;
	}
	
	public Rectangle2D.Float getLocationAsRect() {
		if(rect == null)
			rect = new Rectangle2D.Float(0, 0, WIDTH, HEIGHT);
		rect.x = location.x;
		rect.y = location.y;
		return rect;
	}

	public void setLocation(Point2D.Float location) {
		this.location = location;
	}

	public Point2D.Float getVelocity() {
		return velocity;
	}

	public void setVelocity(Point2D.Float velocity) {
		this.velocity = velocity;
	}
	
	public void render(GameContainer container, Graphics g)
	{
		render(container, g, WIDTH, HEIGHT, image);
	}
	
	protected void render(GameContainer container, Graphics g, float width, float height, Texture image) {
		image.bind();
		glPushMatrix();
			glTranslatef(getLocation().x + width / 2, getLocation().y + height / 2, 0);
			glRotatef(getRotation(), 0, 0, 1f);
			if(getRotation() == 180f)
				glRotatef(getRotation(), 1, 0, 0);
			glTranslatef(-getLocation().x - width / 2, -getLocation().y - height / 2, 0);
			glBegin(GL_QUADS);
				glTexCoord2f(0, 0);
				glVertex2f(getLocation().x, getLocation().y);
				
				glTexCoord2f(1, 0);
				glVertex2f(getLocation().x + width, getLocation().y);
				
				glTexCoord2f(1, 1);
				glVertex2f(getLocation().x + width, getLocation().y + height);
				
				glTexCoord2f(0, 1);
				glVertex2f(getLocation().x, getLocation().y + height);
			glEnd();
		glPopMatrix();
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	/**
	 * Should be called in the init method, loads
	 * the player image to memory.
	 */
	public static void loadImage()
	{
		Ghost.loadImage();
		try
		{
			InputStream stream = new FileInputStream(new File("res/pacman.png"));
			image = TextureLoader.getTexture("png", stream);
		}catch(IOException exc)
		{
			exc.printStackTrace();
			System.err.println("Failed to load player image");
			System.exit(1);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void goUp()
	{
		getVelocity().y = -1;
		getVelocity().x = 0;
		setRotation(270f);
	}
	
	public void goDown()
	{
		getVelocity().y = 1;
		getVelocity().x = 0;
		setRotation(90f);
	}
	
	public void goLeft()
	{
		getVelocity().y = 0;
		getVelocity().x = -1;
		setRotation(180f);
	}
	
	public void goRight()
	{
		getVelocity().y = 0;
		getVelocity().x = 1;
		setRotation(0f);
	}

	
}

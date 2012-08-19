package tim.pacman;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import tim.pacman.impl.ai.AStarPath;

/**
 * A ghost, which is implemented such that the AStar algorithm is
 * extremely easy.
 * 
 * @author Timothy
 */
public class Ghost extends Player {
	private static Texture ghostImage;
	
	private Player target;
	private AStarPath path;
	private long deadSince;
	private boolean dead;
	
	/**
	 * Creates a ghost with the specified name at 
	 * the specified locatin.
	 * @param nm the name of the ghost
	 * @param x the x coordinate of the ghost
	 * @param y the y coordinate of the ghost
	 */
	public Ghost(String nm, float x, float y) {
		super(nm, x, y);
	}

	/**
	 * Returns the current target of the ghost
	 * @return the target, may be null
	 */
	public Player getTarget() {
		return target;
	}

	/**
	 * Sets the target for the ghost
	 * @param target the new target, may be null
	 */
	public void setTarget(Player target) {
		this.target = target;
	}

	/**
	 * Retrieves the path the ghost is currently using
	 * to get to the target
	 * @return the current path
	 */
	public AStarPath getPath() {
		return path;
	}

	/**
	 * Sets the path the ghost will use to get to the 
	 * target
	 * 
	 * @param path the new path
	 */
	public void setPath(AStarPath path) {
		this.path = path;
	}

	/**
	 * Retrieves if the ghost is dead at this time
	 * @return if the ghost is dead
	 */
	public boolean isDead() {
		return dead;
	}
	
	/**
	 * Retrieves when the ghost was killed
	 * @return the time the ghost was killed
	 */
	public long deadSince()
	{
		return deadSince;
	}
	
	/**
	 * Sets the ghost to dead and the time at <code>time</code>
	 * @param time the game time
	 */
	public void kill(long time)
	{
		dead = true;
		deadSince = time;
	}
	
	/**
	 * 'Revives' the ghost by setting dead to false
	 */
	public void revive()
	{
		dead = false;
	}
	
	/**
	 * Does nothing.
	 */
	@Override
	public void setRotation(float f)
	{
		// ghosts dont rotate!
	}
	
	/**
	 * Loads the player image into memory
	 */
	public static void loadImage()
	{
		try
		{
			InputStream stream = new FileInputStream(new File("res/pacman-ghost.png"));
			ghostImage = TextureLoader.getTexture("png", stream);
		}catch(IOException exc)
		{
			exc.printStackTrace();
			System.err.println("Failed to load ghost image");
			System.exit(1);
		}
	}
	
	@Override
	public void render(GameContainer container, Graphics g)
	{
		super.render(container, g, 14, 14, ghostImage);
	}
}

package tim.pacman;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * A GUI for the application, such as the menu screen.
 * @author Timothy
 *
 */
public interface Gui {
	
	/**
	 * Renders the GUI.
	 * @param container
	 * @param graphics
	 */
	public void render(GameContainer container, Graphics graphics) throws SlickException;

	/**
	 * Does updating without rendering
	 * @param cont
	 * @param delta
	 */
	void update(GameContainer cont, int delta);
}

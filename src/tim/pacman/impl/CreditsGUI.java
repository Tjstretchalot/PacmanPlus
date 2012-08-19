package tim.pacman.impl;

import org.newdawn.slick.Graphics;


public class CreditsGUI extends ScrollingGui {
	private static final String[] messages = new String[] {
		"Developer: Timothy Moore (Tjstretchalot)",
		"Pacman Picture: http://videogames.ultisky.com/free-pacman-top-four-sites-to-visit",
		"Pacman Ghost: http://www.clker.com/clipart-pacman-ghost.html",
		"The AI: http://memoization.com/2008/11/30/a-star-algorithm-in-java/",
		
	};
	public CreditsGUI(Graphics grap)
	{
		super(grap, messages);
	}
}

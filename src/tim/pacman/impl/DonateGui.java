package tim.pacman.impl;

import org.newdawn.slick.Graphics;

public class DonateGui extends ScrollingGui {
	private static final String[] messages = new String[] {
		"Willing to donate?",
		"Money from donating will directly support me (the developer) maintain "
		+ "and develop this and other games.  If you wish, you can also "
		+ "be emailed for each nightly build of all free future games.",
		"",
		"Simply shoot an email to me at tjstretchalot@gmail.com with the word " +
		"'Donate' and the title of the game (Pacman Plus) and I will respond as " +
		"soon as possible.",
		"",
		" - Timothy Moore"
	};
	
	public DonateGui(Graphics grap) {
		super(grap, messages);
	}
}

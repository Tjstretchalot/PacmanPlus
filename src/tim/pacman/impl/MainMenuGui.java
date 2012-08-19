package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import tim.pacman.Gui;
import tim.pacman.PacmanApplication;



public class MainMenuGui implements Gui{
	public static final int LOGO = 0;

	private static final String HOST = "http://staticvoidgames.com";
	
	private Texture[] textures;
	
	private static String[] options = new String[] {
		"Singleplayer",
		"Multiplayer on this keyboard",
		"Multiplayer on LAN",
		"Credits"
	};
	
	public MainMenuGui()
	{
		textures = new Texture[1];
		textures[LOGO] = PacmanApplication.application.getLogo();
	}
	
	public MainMenuGui(Texture logo2) {
		textures = new Texture[1];
		textures[LOGO] = logo2;
	}

	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		boolean chScreen = PacmanApplication.getTime() - PacmanApplication.application.getLastChange() > 500;
		textures[LOGO].bind();
		
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
		
		boolean clicked = PacmanApplication.drawHighlightableText(graphics, "Donate", 10f, (480f - graphics.getFont().getHeight("Donate") - 10f));
		if(clicked && chScreen)
			onClick(graphics, 100);
		
		float x = Display.getWidth() - graphics.getFont().getWidth(HOST) - 10;
		PacmanApplication.drawText(graphics, HOST, x, (480f - graphics.getFont().getHeight(HOST) - 10f));
		
		for(int i = 0; i < options.length; i++)
		{
			String str = options[i];
			clicked = PacmanApplication.drawCenteredHText(graphics, str, y);
			if(clicked && chScreen)
				onClick(graphics, i);
			y += 50f;
		}
		
		
	}

	protected void onClick(Graphics grap, int text)
	{
		if(text < 2) // Not Credits or Donate
		{
			CreateGameGUI gui = new CreateGameGUI(text, textures[LOGO]);;
			PacmanApplication.application.setGUI(gui);
		}else if(text == 2)
		{
			PacmanApplication.application.setGUI(new MultiplayerGui());
		}else if(text == 100)
		{
			PacmanApplication.application.setGUI(new DonateGui(grap));
		}else if(text == 3)
		{
			PacmanApplication.application.setGUI(new CreditsGUI(grap));
		}
	}

	@Override
	public void update(GameContainer cont, int delta) {
	}
}

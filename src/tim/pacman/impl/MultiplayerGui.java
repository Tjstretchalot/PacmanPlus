package tim.pacman.impl;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.pacman.Gui;
import tim.pacman.PacmanApplication;

public class MultiplayerGui implements Gui {
	
	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
		long time = PacmanApplication.getTime();
		long lastClick = PacmanApplication.application.getLastChange();
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
		
		float y = 175f;
		boolean host = PacmanApplication.drawCenteredHText(graphics, "Host Game", y);
		if(host && time - lastClick > 500)
			onClickHost();
		
		y += 50f;
		
		boolean connect = PacmanApplication.drawCenteredHText(graphics, "Join Game", y);
		if(connect && time - lastClick > 500)
			onClickConnect();
		
		y += 50f;
		boolean goBack = PacmanApplication.drawCenteredHText(graphics, "Return to main menu", y);
		
		if(goBack && time - lastClick > 500)
		{
			PacmanApplication.application.setGUI(new MainMenuGui());
		}
		
	}
	
	@Override
	public void update(GameContainer cont, int delta) {
		
	}

	private void onClickHost() {
		PacmanApplication.application.setGUI(new PrepareHostGui());
	}
	
	private void onClickConnect() {
		PacmanApplication.application.setGUI(new ConnectGui());
	}
}

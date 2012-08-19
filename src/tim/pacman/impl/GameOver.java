package tim.pacman.impl;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.opengl.Texture;

import tim.pacman.Gui;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;

import static org.lwjgl.opengl.GL11.*;

public class GameOver implements Gui {
	private Player winner;
	private Player loser;
	private Texture logo;

	public GameOver(Player winner, Player loser)
	{
		this.winner = winner;
		this.loser = loser;

		logo = PacmanApplication.application.getLogo();
	}

	@Override
	public void render(GameContainer container, Graphics graphics)
			throws SlickException {
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

		float y = 175f;

		PacmanApplication.drawCenteredText(graphics, winner.getName() + " wins!", y);

		y += 50f;

		PacmanApplication.drawCenteredText(graphics, "Scores", y);
		PacmanApplication.drawCenteredText(graphics, "Scores", y); // Effectively bolds it

		y += 25f;

		PacmanApplication.drawCenteredText(graphics,winner.getName() + ": " + winner.getScore(), y);
		y += 15f;
		PacmanApplication.drawCenteredText(graphics, loser.getName() + ": " + loser.getScore(), y);

		y += 45f;
		boolean returnToMainMenu = PacmanApplication.drawCenteredHText(graphics, "Return to main menu", y);
		if(returnToMainMenu)
		{
			PacmanApplication.application.setGUI(new MainMenuGui());
		}

	}

	@Override
	public void update(GameContainer cont, int delta) {

	}

}

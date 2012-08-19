package tim.pacman.impl;

import java.awt.geom.Rectangle2D;
import java.util.TimerTask;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.pacman.AbstractGameMode;
import tim.pacman.GameMap;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;

/**
 * Tag mode, where one player is designated the taggie, and he or she must tag
 * the other player. The longer you are not the taggie, the more points you
 * have.
 * 
 * @author Timothy
 * 
 */
public class TagMode extends AbstractGameMode {

	private static final long FORCE_SWAP = 30; // Amount of time (seconds)
												// before the other player is
												// forcibly made it.
	private static final long GAME_TIME = 120;
	
	private final TimerTask PL1_BOOST = new TimerTask() {

		@Override
		public void run() {
			player1Modifier = 0.25f;
		}
		
	};
	
	private final TimerTask PL2_BOOST = new TimerTask() {

		@Override
		public void run() {
			player2Modifier = 0.25f;
		}
		
	};
	
	private boolean player1It;
	private long lastSwitched;
	private long start;
	private long nextOrbRespawn;
	private long speedBoostEnds;
	private long stopShowingTeamsSwitched;
	private boolean forced;

	public TagMode(GameMap map) {
		super(map);
		player1It = PacmanApplication.getRND().nextBoolean();

		start = PacmanApplication.getTime();
		stopShowingTeamsSwitched = -1;
		lastSwitched = start;
	}

	@Override
	public void doTick(long time, long delta) {
		if (player1It)
			player2.addToScore((int) delta);
		else
			player1.addToScore((int) delta);

		Rectangle2D.Float play1 = player1.getLocationAsRect();
		Rectangle2D.Float play2 = player2.getLocationAsRect();
		
		if (play1.intersects(play2) && time - lastSwitched > 1500) {
			player1It = !player1It;
			lastSwitched = time;
			speedBoostEnds = time + 500;
			stopShowingTeamsSwitched = time + 1000;
			forced = false;
			
			if(player1It)
			{
				player1Modifier = 0.5f;
				timer.schedule(PL1_BOOST, 500);
			}else
			{
				player2Modifier = 0.5f;
				timer.schedule(PL2_BOOST, 500);
			}
			
		}
		// Increase the speed for the person who is it a little
//		if (player1It) {
//			player1.getLocation().x += player1.getVelocity().x * 0.1f;
//			player1.getLocation().y += player1.getVelocity().y * 0.1f;
//
//			if (time < speedBoostEnds) {
//				player2.getLocation().x += player2.getVelocity().x * 0.45f;
//			}
//		} else {
//			player2.getLocation().x += player2.getVelocity().x * 0.1f;
//			player2.getLocation().y += player2.getVelocity().y * 0.1f;
//
//			if (time < speedBoostEnds) {
//				player1.getLocation().x += player1.getVelocity().x * 0.45f;
//			}
//		}

		if ((time - lastSwitched) / 1000 >= FORCE_SWAP) {
			stopShowingTeamsSwitched = time + 1000;
			lastSwitched = time;
			player1It = !player1It;
			forced = true;
		}

		if (time >= nextOrbRespawn) {
			gameMap.respawnAllOrbs();
			nextOrbRespawn = time + 30000; // 4 a game. 30 seconds, 60 seconds,
											// 90 seconds, 120 seconds
		}
		super.doTick(time, delta);
	}

	@Override
	public void specialMovement(Player pl, byte type) {
		if (isCurrentlyIt(pl)) {
			if (type == GameMap.SPAWNER) {
				pl.getLocation().x += pl.getVelocity().x * 0.05;
				pl.getLocation().y += pl.getVelocity().y * 0.05;
			} else {
				pl.getLocation().x -= pl.getVelocity().x * 0.35;
				pl.getLocation().y -= pl.getVelocity().y * 0.35;
			}
		} else {
			pl.getLocation().x -= pl.getVelocity().x * 0.25;
			pl.getLocation().y -= pl.getVelocity().y * 0.25;

			if (PacmanApplication.getTime() <= speedBoostEnds) {
				pl.getLocation().x -= pl.getVelocity().x * 0.45;
				pl.getLocation().y -= pl.getVelocity().y * 0.45;
			}
		}
	}

	@Override
	public boolean isGameOver() {
		return getTimeRemaining() <= 0;
	}

	@Override
	public void onGameOver() {
		player1.setScore(player1.getScore() / 500);
		player2.setScore(player2.getScore() / 500);

		super.onGameOver();
	}

	protected long getTimeRemaining() {
		return GAME_TIME - gameTime / 1000;
	}

	@Override
	protected String getLeftMessage() {
		return player1.getName() + ": " + player1.getScore() / 500;
	}

	@Override
	protected String getRightMessage() {
		return player2.getName() + ": " + player2.getScore() / 500;
	}

	@Override
	protected String getCenterMessage() {
		return player1It ? player1.getName() + " It" : player2.getName()
				+ " It";
	}

	@Override
	protected String getBottomCenterMessage() {
		return "Time Remaining: " + getTimeRemaining();
	}

	@Override
	protected void onOrbCollect(Player pl) {
		pl.addToScore(5000);
	}

	@Override
	public void doCustomRendering(GameContainer cont, Graphics g) {
		super.doCustomRendering(cont, g);

		if (PacmanApplication.getTime() < stopShowingTeamsSwitched) {
			String msg = forced ? "Teams Evened! "
					+ (player1It ? player1.getName() : player2.getName())
					+ " is now It!" : (player1It ? player1.getName() : player2
					.getName()) + " is now It!";
			PacmanApplication.drawCenteredText(g, msg, 81);
			PacmanApplication.drawCenteredText(g, msg, 81);
		}
	}

	public boolean isCurrentlyIt(Player thePlayer) {
		if(thePlayer != player1 && thePlayer != player2)
			return false;
		
		return thePlayer == player1 && player1It || thePlayer == player2 && !player1It;
	}

	@Override
	public int getNumberGhosts() {
		return 0;
	}
}

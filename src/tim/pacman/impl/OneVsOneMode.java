package tim.pacman.impl;

import java.awt.geom.Rectangle2D;

import tim.pacman.AbstractGameMode;
import tim.pacman.GameMap;
import tim.pacman.Ghost;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;

public class OneVsOneMode extends AbstractGameMode {

	private static final float BOUNCE_SPEED = 0.75f;
	private static final long BOUNCE_TIME = 350;
	private long nextOrbReset;
	private long doneBouncingAt;
	private byte bouncingDirection;
	
	public OneVsOneMode(GameMap map) {
		super(map);
		nextOrbReset = PacmanApplication.getTime() + 30000;
	}
	
	@Override
	public void doTick(long time, long delta)
	{
		super.doTick(time, delta);
		
		if(time >= nextOrbReset)
		{
			gameMap.respawnAllOrbs();
			nextOrbReset = time + 30000;
			System.out.println("Respawning orbs");
		}
		
		Rectangle2D.Float pla1 = player1.getLocationAsRect();
		Rectangle2D.Float pla2 = player2.getLocationAsRect();
		
		if(doneBouncingAt > time)
		{
			if(bouncingDirection == 0)
			{
				// left/right
				if(pla1.x < pla2.x)
				{
					player1.getLocation().x -= BOUNCE_SPEED;
					player2.getLocation().x += BOUNCE_SPEED;
				}else
				{
					player1.getLocation().x += BOUNCE_SPEED;
					player2.getLocation().x -= BOUNCE_SPEED;
				}
			}else
			{
				if(pla1.y < pla2.y)
				{
					player1.getLocation().y -= BOUNCE_SPEED;
					player2.getLocation().y += BOUNCE_SPEED;
				}else
				{
					player1.getLocation().y += BOUNCE_SPEED;
					player2.getLocation().y -= BOUNCE_SPEED;
				}
			}
		}
	}
	
	@Override
	public void specialMovement(Player pl, byte type) {
		super.specialMovement(pl, type);
		if(pl instanceof Ghost)
			return;
		if(PacmanApplication.getTime() < doneBouncingAt)
		{
			if(pl == player1)
			{
				if(bouncingDirection == 0 )
				{
					if(pl.getLocation().x < player2.getLocation().x)
					{
						pl.getLocation().x += BOUNCE_SPEED;
					}else
					{
						pl.getLocation().x -= BOUNCE_SPEED;
					}
				}else
				{
					if(pl.getLocation().y < player2.getLocation().y)
					{
						pl.getLocation().y += BOUNCE_SPEED;
					}else
					{
						pl.getLocation().y -= BOUNCE_SPEED;
					}
				}
			}else
			{
				if(bouncingDirection == 0)
				{
					if(pl.getLocation().x < player1.getLocation().x)
					{
						pl.getLocation().x += BOUNCE_SPEED;
					}else
					{
						pl.getLocation().x -= BOUNCE_SPEED;
					}
				}else
				{
					if(pl.getLocation().y < player1.getLocation().y)
					{
						pl.getLocation().y += BOUNCE_SPEED;
					}else
					{
						pl.getLocation().y -= BOUNCE_SPEED;
					}
				}
			}
		}
		
	}

	@Override
	public void onGameOver() {
		PacmanApplication.application.setGUI(new GameOver(getLeader(),
				getLoser()));
	}
	
	@Override
	public boolean isGameOver()
	{
		return getLeader().getScore() >= 2000;
	}

	@Override
	protected void onOrbCollect(Player pl) {
		pl.addToScore(10);
	}

	@Override
	public int getNumberGhosts() {
		return 1;
	}
	
	@Override
	protected void onPlayerCollide(long time)
	{
		doneBouncingAt = time + BOUNCE_TIME;
		
		if(player1.getLocation().getX() < player2.getLocation().getX())
			bouncingDirection = 0;
		else
			bouncingDirection = 1;
	}
}

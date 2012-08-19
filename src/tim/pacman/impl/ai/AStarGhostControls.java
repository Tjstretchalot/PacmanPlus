package tim.pacman.impl.ai;

import static tim.pacman.PacmanApplication.getRND;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.List;

import tim.pacman.AbstractGameMode;
import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Ghost;
import tim.pacman.GhostController;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;

public class AStarGhostControls implements GhostController {
	private GameMode gameMode;
	private AStarGameMap gameMap;
	
	private long player1LastHit;
	private long player2LastHit;
	private long lastRecalculate;
	
	public AStarGhostControls(GameMode gMode, AStarGameMap gMap)
	{
		gameMode = gMode;
		gameMap = gMap;
	}
	
	@Override
	public void doTick(long time) {
		// Cycle through each ghost and do his logic
		
		Ghost[] ghosts = gameMode.getGhosts();
		
		int counter = 0;
		for(Ghost g : ghosts)
		{
			if(g.isDead())
			{
				if(time - g.deadSince() > 2000 && getRND().nextFloat() < gameMode.getGhostRespawnChance())
				{
					g.revive();
					gameMode.spawnGhost(counter, GameMap.SPAWNER);
				}else
				{
					g.setTarget(null);
				}
			}else
				doLogic(g, time);
			counter++;
		}
		
		// Move them all
		for(Ghost g : ghosts)
		{
			g.getLocation().x += g.getVelocity().x * 0.2;
			g.getLocation().y += g.getVelocity().y * 0.2;
		}
	}

	protected void doLogic(Ghost g, long time) {
		Player target = g.getTarget();
		
		if(target == null)
		{
			decideTarget(g);
			target = g.getTarget();
			if(target == null)
			{
				g.getVelocity().x = 0;
				g.getVelocity().y = 0;
				return; // lazy..
			}
				
		}
		
		Point gridDest = gameMap.toGridLocation(g.getTarget().getLocation());
		
		if(g.getPath() == null || !g.getPath().getLastStepLocation().equals(gridDest) || time - lastRecalculate > 250)
		{
			lastRecalculate = time;
			revalueSteps();
			calculatePath(g);
			
			if(g.getPath() == null)
				return;
		}
		Point plLoc = gameMap.toGridLocation(g.getLocation());
		
		if(!plLoc.equals(gridDest))
		{
			PathStep nextStep = g.getPath().getNextStep(plLoc.x, plLoc.y);
			
			if(nextStep != null)
				doNextStep(g, nextStep, plLoc);
		}
		
		((AbstractGameMode) gameMode).checkBoundaries(g);
		((AbstractGameMode) gameMode).snapToGrid(g);
	}

	private void revalueSteps() {
		gameMap.forceRecalculate(gameMode, 0, 125);
	}

	protected void doNextStep(Ghost g, PathStep nextStep, Point plLoc) {
		
		// it is either left right top or down.
		
		if(nextStep.getY() < plLoc.y)
		{
			g.goUp();
			return;
		}else if(nextStep.getY() > plLoc.y)
		{
			g.goDown();
			return;
		}else if(nextStep.getX() < plLoc.x)
		{
			g.goLeft();
			return;
		}else if(nextStep.getX() > plLoc.x)
		{
			g.goRight();
			return;
		}
		
		throw new AssertionError("Shouldn't be able to get here");
	}

	protected void calculatePath(Ghost g) {
		Point gridLoc = gameMap.toGridLocation(g.getLocation());
		Point gridDest = gameMap.toGridLocation(g.getTarget().getLocation());
		AStarPath path = gameMap.findBestPath(gridLoc.x, gridLoc.y,
				gridDest.x, gridDest.y);
		g.setPath(path);
	}

	protected void decideTarget(Ghost g) {
		boolean leader = getRND().nextFloat() < 0.55; // slightly more likely to attack the leader
		g.setTarget(gameMode.getLeader() == gameMode.getPlayer1() ? 
				(leader ? gameMode.getPlayer1() : gameMode.getPlayer2()) :
				(leader ? gameMode.getPlayer2() : gameMode.getPlayer1()));
	}

}

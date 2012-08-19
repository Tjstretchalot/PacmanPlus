package tim.pacman.impl.ai;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.PacmanApplication;
import tim.pacman.Player;
import tim.pacman.impl.AIController;
import tim.pacman.impl.BuiltToLastMode;
import tim.pacman.impl.OneVsOneMode;
import tim.pacman.impl.TagMode;

public class AStarController extends AIController {
	private static final long MINIMUM_RECALCULATE_DELAY = 200;
	private AStarPath path;
	private int index;
	private long lastRecalculate;
	
	private double ghostWeight;
	private double playerWeight;
	
	public AStarController(Player player, Player opponent, GameMode mode, AStarGameMap map) {
		super(player, opponent, mode, map);
		
		if(mode instanceof TagMode)
		{
			ghostWeight = 0;
			playerWeight = 0;
		}else if(mode instanceof OneVsOneMode)
		{
			ghostWeight = 10000;
			playerWeight = 125;
		}else if(mode instanceof BuiltToLastMode)
		{
			ghostWeight = 10000;
			playerWeight = 125;
		}
	}

	@Override
	public void doTick(long time) {
		// If the path is still valid, calculate the path

		Point2D.Float nextDest = getNextDestination();
		if(nextDest == null)
			return;
		Point endLocation = gMap.toGridLocation(nextDest);
		Point plLoc = gMap.toGridLocation(thePlayer.getLocation());

		if (plLoc.equals(endLocation))
			return; // We are done here o.o

		PathStep nextStep = null;
		if (path != null && !path.getLastStepLocation().equals(plLoc))
			nextStep = path.getNextStep(plLoc.x, plLoc.y);
		if (path == null || nextStep == null
				|| time - lastRecalculate > MINIMUM_RECALCULATE_DELAY
				&& !endLocation.equals(path.getLastStepLocation())) {
			
			revalueSteps();
			path = ((AStarGameMap) gMap).findBestPath(plLoc.x, plLoc.y,
					endLocation.x, endLocation.y);
			if(path != null)
				nextStep = path.getNextStep(plLoc.x, plLoc.y);
			
//			System.out.println("Next step is " + nextStep);
			lastRecalculate = time;
			index++;
		}
		
		if (nextStep != null && !path.getLastStepLocation().equals(plLoc))
			moveTowards(plLoc, nextStep);
	}
	
	private void revalueSteps() {
		((AStarGameMap) gMap).forceRecalculate(mode, playerWeight, ghostWeight);
	}

	protected void moveTowards(Point plLoc, PathStep nextStep) {
		assert nextStep != null : "Next step can't be null. CALL SECURITY";
		
		// it is either left right top or down.
		
		if(nextStep.getY() < plLoc.y)
		{
			goUp();
			return;
		}else if(nextStep.getY() > plLoc.y)
		{
			goDown();
			return;
		}else if(nextStep.getX() < plLoc.x)
		{
			goLeft();
			return;
		}else if(nextStep.getX() > plLoc.x)
		{
			goRight();
			return;
		}
		
		throw new AssertionError("Shouldn't be able to get here");
	}

	protected Point2D.Float getNextDestination() {
		if(mode instanceof TagMode)
		{
			TagMode thMode = (TagMode) mode;
			
			if(thMode.isCurrentlyIt(thePlayer))
			{
				// Run towards the player
				return oppPlayer.getLocation();
			}else
			{
				return gMap.getFurthestFrom(oppPlayer.getLocation());
			}
		}else if(mode instanceof OneVsOneMode || mode instanceof BuiltToLastMode)
		{
			if(path != null && gMap.getType(path.getLastStepLocation().x,
					path.getLastStepLocation().y) == GameMap.ORB)
				return gMap.fromGridLocation(path.getLastStepLocation());
			
			Point nearest = gMap.getNearest(GameMap.ORB, 
					gMap.toGridLocation(new Point2D.Float(thePlayer.getLocation().x, thePlayer.getLocation().y))
					);
			if(nearest == null)
				return gMap.fromGridLocation(gMap.chooseRandom(GameMap.EMPTY));
			return gMap.fromGridLocation(nearest);
		}
		return null;
	}

}

package tim.pacman.impl.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import tim.pacman.GameMap;
import tim.pacman.GameMode;
import tim.pacman.Ghost;
import tim.pacman.PacmanApplication;

public class AStarGameMap extends GameMap {
	private PathStep[][] objectMapArray;
	private List<PathStep> opened = new ArrayList<PathStep>();
	private List<PathStep> closed = new ArrayList<PathStep>();
	private List<PathStep> bestList = new ArrayList<PathStep>();
	private PathStep goal;
	private List<Point> lastSpecial;
	
	public AStarGameMap(byte[][] mapArray) {
		super(mapArray);
		
		objectMapArray = new PathStep[getRows()][getColumns()];
	}

	public AStarGameMap(GameMap gameMap) {
		this(gameMap.getTheMap());
		lastSpecial = new ArrayList<Point>();
	}

	public PathStep getStep(int x, int y) {
		if(x < 0 || x >= getRows() || y < 0 || y >= getColumns())
			return null;
		if(objectMapArray[x][y] == null)
		{
			objectMapArray[x][y] = new PathStep(x, y, this);
			recalculate(x, y);
		}
		return objectMapArray[x][y];
	}
	
	private void recalculate(int x, int y) 
	{
		switch(getType(x, y))
		{
		case EMPTY: case EATEN_ORB:
			objectMapArray[x][y].setParentCost(3000);
			break;
		case WALL:
			objectMapArray[x][y].setParentCost((double) Integer.MAX_VALUE * 2);
			break;
		case SPAWNER:
			objectMapArray[x][y].setParentCost((double) Integer.MAX_VALUE); // prefer spawner over the walls
			break;
		case ORB:
			objectMapArray[x][y].setParentCost(0);
			break;
		default:
			System.err.println("Unknown type '" + getType(x, y) + "' Avoiding it.");
			objectMapArray[x][y].setParentCost(Double.MAX_VALUE / 2);
			break;
		}
	}

	public AStarPath findBestPath(final int xStart, final int yStart, final int xEnd, final int yEnd)
	{
		preFindPath(xStart, yStart, xEnd, yEnd);
		//System.out.println("Calculating best path.. (" + xStart + ", " + yStart + ") -> (" + xEnd + ", " + yEnd +")");
		
		PathStep beginning = getStep(xStart, yStart);
		goal = getStep(xEnd, yEnd);
		Set<PathStep> adjacencies = beginning.getAdjacencies();
		for (PathStep adjacency : adjacencies) {
			adjacency.setParent(beginning);
			if (!(adjacency.getX() == xStart && adjacency.getY() == yStart)) {
				opened.add(adjacency);
			}
		}

		while (opened.size() > 0) {
			PathStep best = findBestPassThrough(xEnd, yEnd);
			opened.remove(best);
			closed.add(best);
			//System.out.println("Checking " + best.getX() + ", " + best.getY());
			if (best.getX() == xEnd && best.getY() == yEnd) {
				//System.out.println("Found Goal");
				populateBestList(goal, xStart, yStart);
				return bestListToPath(xStart, yStart, xEnd, yEnd);
			} else {
				//System.out.println("Not the end.");
				Set<PathStep> neighbors = best.getAdjacencies();
				for (PathStep neighbor : neighbors) {
					//System.out.println("Old: " + neighbor);
					if (opened.contains(neighbor)) {
						PathStep tmpPathStep = new PathStep(neighbor.getX(),
								neighbor.getY(), this);
						tmpPathStep.setParent(best);
						if (tmpPathStep.getPassThrough(goal) >= neighbor
								.getPassThrough(goal)) {
							continue;
						}
					}

					if (closed.contains(neighbor)) {
//						PathStep tmpPathStep = new PathStep(neighbor.getX(),
//								neighbor.getY(), this);
//						tmpPathStep.setParent(best);
//						if (tmpPathStep.getPassThrough(goal) >= neighbor
//								.getPassThrough(goal)) {
//							continue;
//						}
						continue;
					}
					
					//System.out.println("New neighbor: " + best);
					neighbor.setParent(best);

					opened.remove(neighbor);
					//closed.remove(neighbor);
					opened.add(0, neighbor);
				}
			}
		}

		System.out.println("No Path to goal");
		return null;
	}

	private void preFindPath(int xStart, int yStart, int xEnd, int yEnd) {
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getColumns(); j++) {
				getStep(i, j).calculateAdjacencies();
			}
		}
	}

	private AStarPath bestListToPath(int xStart, int xEnd, int yStart, int yEnd) {
		// Reverse the order
		bestList.add(getStep(xStart, xEnd));
		//System.out.println("Size: " + bestList.size());
		Collections.reverse(bestList);
		//System.out.println("Size: " + bestList.size());
		
		List<PathStep> result = new ArrayList<>(bestList.size());
		
		for(int i = 0; i < bestList.size(); i++)
		{
			result.add(bestList.get(i));
		}
		
		// Clean up
		bestList.clear();
		opened.clear();
		closed.clear();
		
		//System.out.println("Size: " + result.size());
		goal = null;
		
		return new AStarPath(result);
	}

	private void populateBestList(PathStep step, int stX, int stY) {
		bestList.add(step);
		if (step.getParent() != null && !(step.getParent().getX() == stX && step.getParent().getY() == stY)) {
			
			if(step.getParent().getParent() != step)
				populateBestList(step.getParent(), stX, stY);
		}
	}

	private PathStep findBestPassThrough(int enX, int enY) {
		PathStep best = null;
		for (PathStep step : opened) {
			if (best == null
					|| step.getPassThrough(goal) < best.getPassThrough(goal)) {				
				//System.out.println("Old best: " + ((best == null) ? "none" : best.getPassThrough(goal)));
				best = step;
				//System.out.println("New best: " + step.getPassThrough(goal));
			}
		}

		return best;
	}

	public void forceRecalculate(GameMode mode, double playerValue, double ghostValue) {
		for(Point po : lastSpecial)
		{
			recalculate(po.x, po.y);
		}
		lastSpecial.clear();
		
		Point pla1 = toGridLocation(mode.getPlayer1().getLocation());
		Point pla2 = toGridLocation(mode.getPlayer2().getLocation());
		
		getStep(pla1.x, pla1.y).setParentCost(playerValue);
		getStep(pla2.x, pla2.y).setParentCost(playerValue);
		lastSpecial.add(pla1);
		lastSpecial.add(pla2);
		
		Ghost[] ghosts = mode.getGhosts();
		
		for(Ghost g : ghosts)
		{
			Point loc = toGridLocation(g.getLocation());
			PathStep step = getStep(loc.x, loc.y);
			if(step == null)
				return;
			step.setParentCost(ghostValue);
			lastSpecial.add(loc);
		}
	}

}

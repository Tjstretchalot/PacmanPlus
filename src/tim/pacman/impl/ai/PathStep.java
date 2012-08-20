package tim.pacman.impl.ai;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import tim.pacman.GameMap;

/**
 * A pretty straightforward Astar approach.  Changed from 
 * http://memoization.com/2008/11/30/a-star-algorithm-in-java/ to match
 * my API.
 * 
 * @author Timothy
 *
 */
public class PathStep {
	
	/**
	 * The grid x
	 */
	private int x;
	
	/**
	 * The grid y
	 */
	private int y;
	
	private double localCost;
	private double parentCost;
	private double passThroughCost;
	
	private AStarGameMap map;
	
	private PathStep parent;
	private Set<PathStep> adjacencies;
	
	public PathStep(int x, int y, AStarGameMap map) {
		super();
		this.x = x;
		this.y = y;
		this.map = map;
		
		adjacencies = new HashSet<PathStep>();
		
		passThroughCost = -1;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public double getLocalCost() {
		return localCost;
	}

	public void setLocalCost(double localCost) {
		this.localCost = localCost;
	}

	public void setParentCost(double parentCost) {
		this.parentCost = parentCost;
	}
	
	public AStarGameMap getMap() {
		return map;
	}

	public void setMap(AStarGameMap map) {
		this.map = map;
	}

	public PathStep getParent() {
		return parent;
	}

	public void setParent(PathStep parent) {
		this.parent = parent;
	}

	public Set<PathStep> getAdjacencies() {
		return adjacencies;
	}

	public void setAdjacencies(Set<PathStep> adjacencies) {
		this.adjacencies = adjacencies;
	}
	
	public void addAdjacency(PathStep step)
	{
		getAdjacencies().add(step);
	}
	
	public void removeAdjacency(PathStep step)
	{
		getAdjacencies().remove(step);
	}
	
	public void calculateAdjacencies()
	{
		int top = x - 1;
		int bottom = x + 1;
		int left = y - 1;
		int right = y + 1;

		if (bottom < map.getRows()) {
			map.getStep(bottom, y).addAdjacency(this);
			addAdjacency(map.getStep(bottom, y));
		}

		if (right < map.getColumns()) {
			map.getStep(x, right).addAdjacency(this);
			this.addAdjacency(map.getStep(x, right));
		}
	}
	
	public double getPassThrough(PathStep goal)
	{
		if(parent == null)
		{
			return 0.0;
		}else
		{
			passThroughCost = getLocalCost(goal) + getParentCost();
			return passThroughCost;
		}
	}

	public double getLocalCost(PathStep goal) {
		if(parent == null)
			return 0.0;
		
		localCost = Point.distanceSq(x, y, goal.x, goal.y);
		return localCost;
	}
	
	public double getParentCost() {

		if (parent == null) {
			return 0.0;
		}
		
		if(parent.parent == this)
			return  1.0 + .5 * (parent.parentCost - 1.0);

		if (parentCost == 0.0) {
			parentCost = 1.0 + .5 * (parent.getParentCost() - 1.0);
		}

		return parentCost;
	}

	@Override
	public String toString() {
		return "PathStep [x=" + x + ", y=" + y + ", localCost=" 
				+ localCost + ", parentCost=" + parentCost 
				+ ", passThroughCost=" + passThroughCost
				+ ", map=" + map + "]";
	}
}

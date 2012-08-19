package tim.pacman.impl.ai;

import java.awt.Point;
import java.util.List;

public class AStarPath {
	private List<PathStep> route;
	
	public AStarPath(List<PathStep> route) {
		this.route = route;
	}

	public Point getLastStepLocation()
	{
		return new Point(route.get(route.size() - 1).getX(), route.get(route.size() - 1).getY());
	}

	public List<PathStep> getRoute() {
		return route;
	}

	public void setRoute(List<PathStep> route) {
		this.route = route;
	}

	public PathStep getNextStep(int x, int y) {
		for(int i = 0; i < route.size(); i++)
		{
			PathStep step = route.get(i);
			if(step.getX() == x && step.getY() == y)
				return route.get(i + 1);
		}
		
		return null;
	}
	
	
}

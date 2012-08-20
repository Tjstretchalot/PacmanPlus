package tim.pacman;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * The game map, with a static array of the default
 * map. Contains convenience methods for going to and
 * from grid locations and pixel locations.
 * 
 * @author Timothy
 */
public class GameMap {

	/**
	 * A wall.
	 */
	public static final byte WALL = 0;
	
	/**
	 * An empty grid location
	 */
	public static final byte EMPTY = 1;
	
	/**
	 * An orb that has been eaten. Should be treated
	 * the same as an empty location.
	 */
	public static final byte EATEN_ORB = 2;
	
	/**
	 * A grid spot with an orb on it
	 */
	public static final byte ORB = 3;
	
	/**
	 * A spawner grid location, colored red on the map.
	 */
	public static final byte SPAWNER = 4;
	
	
	private static final byte[][] BASIC_MAP_ARRAY = new byte[][] {
		new byte[] {
				WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
				WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL
		},
		
		new byte[] {
				WALL, ORB, ORB, ORB, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, ORB, ORB, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, WALL,
				WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, SPAWNER,
				SPAWNER, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, SPAWNER,
				SPAWNER, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, SPAWNER,
				SPAWNER, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, SPAWNER,
				SPAWNER, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL, WALL,
				WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, WALL, WALL, WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL, WALL, WALL, ORB, WALL
		},
		
		new byte[] {
				WALL, ORB, ORB, ORB, ORB, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, WALL,
				WALL, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, ORB, ORB, ORB, ORB, WALL
		},
		
		new byte[] {
				WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL,
				WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL, WALL
		},
	};
	/**
	 * The main map for the game
	 */
	public static final GameMap GAME_MAP = new GameMap(BASIC_MAP_ARRAY);
	
	private byte[][] theMap;

	/**
	 * Creates a game map with the specified array. Does no
	 * validity checks but will have insidious errors later
	 * if any are invalid.
	 * 
	 * @param mapArray the array representing the map.
	 */
	public GameMap(byte[][] mapArray) {
		this(mapArray, true);
	}
	
	/**
	 * Creates the game map
	 * @param mapArray the array representing the map
	 * @param clone if the array should be deep-copied or just referenced.
	 */
	protected GameMap(byte[][] mapArray, boolean clone)
	{
		if(clone)
			theMap = Arrays.copyOf(mapArray, mapArray.length);
		else
			theMap = mapArray;
	}



	/**
	 * Renders the map onto the screen using
	 * the parameters.
	 */
	public void render(GameContainer cont, Graphics g) {
		
		float width = 16f;
		float height = 16f;
		
		for(int i = 0; i < getTheMap().length; i++)
		{
			for(int j = 0; j < getTheMap()[i].length; j++)
			{
				float x = i * width + 112;
				float y = j * width + 32;
				switch(getTheMap()[i][j])
				{
				case WALL:
					g.drawRect(x, y, width, height);
					break;
				case EMPTY: case EATEN_ORB:
					break;
				case ORB:
					g.fillOval(x + 5, y + 5, width - 10, height - 10);
					break;
				case SPAWNER:
					g.setColor(Color.red.darker(0.5f));
					g.fillRect(x + 1, y + 1, width, height);
					g.setColor(Color.white);
					break;
				default:
					throw new UnsupportedOperationException("Unsupported type '" + getTheMap()[i][j] + "'");
				}
			}
		}
	}


	/**
	 * Returns the type at the specified location
	 * @param locationX the location-x
	 * @param locationY the location-y
	 * @return the byte type at that location.
	 */
	public byte getType(int locationX, int locationY) {
		return getTheMap()[locationX][locationY];
	}


	/**
	 * Sets the block type at the specified location
	 * @param locationX the location x
	 * @param locationY the location y
	 * @param type the type
	 */
	public void setType(int locationX, int locationY, byte type) {
		getTheMap()[locationX][locationY] = type;
	}


	/**
	 * Goes through each block, and if it is an eaten orb it 
	 * is set to a regular orb.
	 */
	public void respawnAllOrbs() {
		for(int i = 0; i < getTheMap().length; i++)
		{
			for(int j = 0; j < getTheMap()[i].length; j++)
			{
				if(getType(i, j) == EATEN_ORB)
					setType(i, j, ORB);
			}
		}
	}


	/**
	 * Returns all the points surrounding the location AND
	 * the location
	 * @param locationX
	 * @param locationY
	 * @return the surrounding points
	 */
	public Point[] getSurrounding(int locationX, int locationY) {
		Point[] res = new Point[] {
				new Point(locationX - 1, locationY - 1),
				new Point(locationX - 1, locationY),
				new Point(locationX - 1, locationY + 1),
				new Point(locationX, locationY - 1),
				new Point(locationX, locationY),
				new Point(locationX, locationY + 1),
				new Point(locationX - 1, locationY - 1),
				new Point(locationX - 1, locationY),
				new Point(locationX - 1, locationY + 1)
		};
		return res;
	}



	/**
	 * Returns the number of rows.  Confusingly rows
	 * is referenced as what a column normally would be,
	 * due to a bug in the beginning.
	 * @return the number of rows.
	 */
	public int getRows() {
		return 26;
	}
	
	/**
	 * Returns the number of columns.
	 * @return the number of columns.
	 */
	public int getColumns() {
		return 26;
	}


	/**
	 * Returns a reference to the map.  All changes in the 
	 * array will be reflected on the map.
	 * @return a reference to the map.
	 */
	public byte[][] getTheMap() {
		return theMap;
	}

	/**
	 * Converts a pixel location to a grid location
	 * @param f the pixel location
	 * @return the grid location
	 */
	public Point toGridLocation(Point2D.Float f) {
		return toGridLocation(f.x, f.y);
	}
	
	/**
	 * Converts a grid location into a pixel location
	 * @param p the grid location
	 * @return the pixel location
	 */
	public Point2D.Float fromGridLocation(Point p)
	{
		Point2D.Float res = new Point2D.Float(p.x * 16 + 112, p.y * 16 + 32);
		return res;
	}
	
	/**
	 * Gets the furthest non-wall block from the specified location.
	 * 
	 * Implementation is always corners of the map.
	 * @param location the location
	 * @return the furthest block from <code>location</code>
	 */
	public Point2D.Float getFurthestFrom(Float location) {
		Point grid = toGridLocation(location);
		
		Point gridResult = new Point();
		if(grid.x < getRows() / 2)
			gridResult.x = getRows() - 2;
		else
			gridResult.x = 1;
		
		if(grid.y < getColumns() / 2)
			gridResult.y = getColumns() - 2;
		else
			gridResult.y = 1;
		
		return fromGridLocation(gridResult);
	}

	/**
	 * Retrieves all tiles of any of the specified types
	 * @param types the types to retrieve
	 * @return the list of points
	 */
	public List<Point> getAll(byte... types) {
		List<Byte> typeList = new ArrayList<Byte>(types.length);
		for(byte b : types)
		{
			typeList.add(b);
		}
		List<Point> result = new ArrayList<Point>();
		
		for(int i = 0; i < getRows(); i++)
		{
			for(int j = 0; j < getColumns(); j++)
			{
				if(typeList.contains(getType(i, j)))
					result.add(new Point(i, j));
			}
		}
		return result;
	}

	/**
	 * Searches for the nearest block of type <code>type</code>
	 * from the location
	 * @param type the block type
	 * @param loc the location
	 * @return the nearest <code>type</code> from <code>loc</code>
	 */
	public Point getNearest(byte type, Point loc) {
		List<Point> all = getAll(type);
		
		double lowestDistance = Double.MAX_VALUE;
		Point min = null;
		
		for(Point p : all)
		{
			double dis = p.distance(loc);
			
			if(dis < lowestDistance)
			{
				min = p;
				lowestDistance = dis;
			}
		}
		return min;
	}

	/**
	 * Chooses a random block of any of the 
	 * specified types
	 * @param types the type of block
	 * @return a random one on the grid
	 */
	public Point chooseRandom(byte... types) {
		List<Point> poss = getAll(types);
		
		int ran = PacmanApplication.getRND().nextInt(poss.size());
		return poss.get(ran);
	}

	/**
	 * Returns the grid location contained by the specified x and y coordinate
	 * @param x the xcoord
	 * @param y the y coord
	 * @return the grid location
	 */
	public Point toGridLocation(float x, float y) {
		int locationX = Math.round((x - 112) / 16f);
		int locationY = Math.round((y - 32) / 16f);
		
		return new Point(locationX, locationY);
	}

}

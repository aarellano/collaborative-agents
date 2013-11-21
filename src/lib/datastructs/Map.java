/**
 * 
 */
package lib.datastructs;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.swt.graphics.ImageData;

import lib.Utils;
import environment.EnvCellEnum;
import environment.Options;

/**
 * @author moh_khaled
 *
 */
public class Map {

	public String id;
	private int width, height;
	private EnvCellEnum[][] map; 
	
	private boolean observed;
	private boolean dirty;
	
	private int unknownsCount;
	
	// Map profile, loadMap related-info
	public Point startingPoint;
	public Vector<Point> greenHidings;
	public Vector<Point> redHidings;

	public Map(int width, int height) {
		super();
		id = "";
		this.width = width;
		this.height = height;
		map = new EnvCellEnum[height][width];
		for(int i = 0; i < this.height; i++)
			for(int j = 0; j < this.width; j++)
				map[i][j] = EnvCellEnum.UNKNOWN;
		observed = false;
		dirty = true;
		unknownsCount = this.width*this.height;
		
		greenHidings = new Vector<Point>();
		redHidings = new Vector<Point>();
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Initializers
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
	public static Map loadMapWithID(String mapID) {
		String name = Options.PATH_MAPS+"map"+(mapID.length() > 0 ? "_"+mapID : "")+".bmp";
		Map map = loadMap(name, 1, 1);
		map.id = mapID;
		return map;
	}
	
	private static Map loadMap(String mapFile, int cellWidth, int cellHeight) {
		
		ImageData imageData = new ImageData(mapFile);
		Map map = new Map(imageData.width, imageData.height);
		
		if(map.getWidth()%cellWidth != 0  || map.getHeight()%cellHeight != 0) {
			System.out.println("Map parsing Failed!");
			System.out.println("Invalid Cell size");
			return null;
		}
		
		for(int i = 0 ; i < map.getHeight(); i += cellHeight) {
			for(int j = 0; j < map.getWidth(); j += cellWidth) {
				int pixel = imageData.getPixel(j, i);
				EnvCellEnum reward = EnvCellEnum.UNKNOWN;
				if (pixel == 0)	//Black pixel
					reward = EnvCellEnum.BLOCKED;
				else if(pixel == 164)	//Gray pixel 
					reward = EnvCellEnum.UNKNOWN;
				//else if(pixel == 249)	//Red pixel
				//else if(pixel == 250)	//Green pixel 
				//else if(pixel == 252)	//Blue pixel 
				//else if(pixel == 255) //White pixel
				else {//White pixel
					reward = EnvCellEnum.FREE;
				}
				
				Point p = new Point(i, j);
				if(pixel == 249)	//Red pixel
					map.redHidings.add(p);
				else if(pixel == 250)	//Green pixel
					map.greenHidings.add(p);
				else if(pixel == 252)	//Blue pixel
					map.startingPoint = p;
					
				map.setCell(i, j, reward);
			}
		}
		return map;
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Logic
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
	public boolean isObstacle(int row, int col)
	{
		return map[row][col] == EnvCellEnum.BLOCKED;
	}
	
	public boolean isFree(int row, int col)
	{
		return map[row][col] == EnvCellEnum.FREE;
	}
	
	public boolean isUnknown(int row, int col)
	{
		return map[row][col] == EnvCellEnum.UNKNOWN;
	}

	public boolean isValidCell(int row, int col)
	{
		return isValidRow(row) && isValidCol(col);
	}
	
	public boolean isValidRow(int row)
	{
		return row >= 0 && row < height;
	}
	
	public boolean isValidCol(int col)
	{
		return col >= 0 && col < width;
	}	
	public Vector<Point> getCellsWithNeighborValue(EnvCellEnum neighborValue) {
		Vector<Point> cells = new Vector<Point>();
		for(int i = 0; i < height; i++)
			for(int j = 0; j < width; j++) {
				Point p = new Point(i, j);
				if(map[i][j] == EnvCellEnum.FREE) {
					if(isValidCell(p.row-1, p.col) && map[p.row-1][p.col] == neighborValue)
						cells.add(p);
					if(isValidCell(p.row+1, p.col) && map[p.row+1][p.col] == neighborValue)
						cells.add(p);
					if(isValidCell(p.row, p.col-1) && map[p.row][p.col-1] == neighborValue)
						cells.add(p);
					if(isValidCell(p.row, p.col+1) && map[p.row][p.col+1] == neighborValue)
						cells.add(p);
				}
			}
		return cells;
	}
	
	public Vector<Point> getNeighborsWithValue(Point p, EnvCellEnum neighborValue, int distance) {
		Vector<Point> neighbors = getCellsWithinDistance(p, distance, VisionDirectionEnum.ALL_D);
		Vector<Point> result = new Vector<Point>();
		for (Point point : neighbors) {
			if(getCell(point.row, point.col) == neighborValue  && getSightLineBlocker(p, point) == null)
				result.add(point);
		}
		return result;
	}
	
	public Vector<Point> getNeighborsWithValue(Point p, EnvCellEnum neighborValue) {
		return getNeighborsWithValue(p, neighborValue, 3);
	}
	
	/**
	 * list of cells with Manhattan distance d from center cell
	 * @param center
	 * @param d
	 * @param visionDirection 
	 * @return
	 */
	public Vector<Point> getCellsWithinDistance(Point center, int d, VisionDirectionEnum visionDirection) {
		Vector<Point> cells = new Vector<Point>();
		
		switch (visionDirection) {
		case ONE_D:
			
			break;
			
		case FOUR_D:
			cells.add(new Point(center.row, center.col));
			// Look Left
			for(int col = center.col-1, row = center.row; col >= 0; col--) {
				cells.add(new Point(row, col));
			}
			// Look Right
			for(int col = center.col+1, row = center.row; col < width; col++) {
				cells.add(new Point(row, col));
			}
			// Look Up
			for(int col = center.col, row = center.row-1; row >= 0; row--) {
				cells.add(new Point(row, col));
			}
			// Look Left
			for(int col = center.col, row = center.row+1; row < height; row++) {
				cells.add(new Point(row, col));
			}
			break;
			
		case ALL_D:
			Point from = new Point(center.row-d, center.col-d);
			Point to = new Point(center.row+d, center.col+d);
			for(int i = from.row; i <= to.row; i++) {
				if(isValidRow(i)) {
					for(int j = from.col; j <= to.col; j++) {
						Point p = new Point(i, j);
						if(isValidCol(j) && center.manDistance(p) <= d)
							cells.add(p);
					}
				}
			}
			break;
			
		default:
			break;
		}
		
		return cells;
	}
	
	public int getCellOpenness(Point cell) {
		int count = 0;
		//TODO use good heuristic for d, e.g. average partition length
		Vector<Point> cells = getCellsWithinDistance(cell, 5, VisionDirectionEnum.ALL_D);
		for (Point point : cells) {
			if(getSightLineBlocker(cell, point) == null) count++;
		}
		return count;
	}
	
	public Point getTransition(Point from, OrientationEnum action) {
		Point b = null;
		switch (action) {
		case NORTH:
			b = new Point(from.row-1, from.col);
			break;
		case SOUTH:
			b = new Point(from.row+1, from.col);
			break;
		case EAST:
			b = new Point(from.row, from.col+1);
			break;
		case WEST:
			b = new Point(from.row, from.col-1);
			break;
		default:
			break;
		}
		
		if(isValidCell(b.row, b.col) && !isObstacle(b.row, b.col))
			return b.clone();
		else return null;
	}
	
	public Vector<OrientationEnum> getPossibleActions(Point cell) {
		Vector<OrientationEnum> actions = new Vector<OrientationEnum>();
		if(getTransition(cell, OrientationEnum.NORTH) != null) 
			actions.add(OrientationEnum.NORTH);
		if(getTransition(cell, OrientationEnum.SOUTH) != null) 
			actions.add(OrientationEnum.SOUTH);
		if(getTransition(cell, OrientationEnum.EAST) != null) 
			actions.add(OrientationEnum.EAST);
		if(getTransition(cell, OrientationEnum.WEST) != null) 
			actions.add(OrientationEnum.WEST);
		return actions;
	}
	
	public boolean isInverseActions(OrientationEnum a1, OrientationEnum a2) {
		if((a1 == OrientationEnum.NORTH && a2 == OrientationEnum.SOUTH) ||
			(a1 == OrientationEnum.EAST && a2 == OrientationEnum.WEST) ||
			(a1 == OrientationEnum.SOUTH && a2 == OrientationEnum.NORTH) ||
			(a1 == OrientationEnum.WEST && a2 == OrientationEnum.EAST)) return true;
		return false;
	}
	
	public Point getSightLineBlocker(Point from, Point to)
	{
		Point blocker = null;
		Vector<Point> path = Utils.sightLine(from, to);
		Iterator<Point> itr = path.iterator();
		while (itr.hasNext() && (blocker == null)) {
			Point p = itr.next();
			if (isObstacle(p.row, p.col))
				blocker = p;
		}
		return blocker;
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Getters
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public EnvCellEnum getCell(int row, int col)
	{
		return map[row][col];
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public int getUnknownsCount() {
		return unknownsCount;
	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Setters
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
	public void setCell(int row, int col, EnvCellEnum value)
	{
		// update unknowns count
		if(map[row][col] == EnvCellEnum.UNKNOWN && value != EnvCellEnum.UNKNOWN) unknownsCount --;
		else if(map[row][col] != EnvCellEnum.UNKNOWN && value == EnvCellEnum.UNKNOWN) unknownsCount ++;
		// track map updates
		if(observed && value != map[row][col]) dirty = true;
		// apply updates
		map[row][col] = value;
	}

	public void setObserved(boolean observed) {
		this.observed = observed;
		if(!observed) this.dirty = false;
	}
	
	public void printMap()
	{
		for(int i = 0; i < height; i++) {
			System.out.println("");
			for(int j = 0; j < width; j++) {
				if(map[i][j] == EnvCellEnum.FREE)
					System.out.print(" ");
				else
					System.out.print("|");
			}
		}
		System.out.println("\n");
	}	
	
}

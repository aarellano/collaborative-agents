/**
 * 
 */
package agent;

import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Vector;

import lib.FileLinesReader;
import lib.datastructs.ActionEnum;
import lib.datastructs.Map;
import lib.datastructs.OrientationEnum;
import lib.datastructs.Path;
import lib.datastructs.Point;
import lib.datastructs.PointValue;
import lib.datastructs.PointValueComparator;
import agent.coverage.CFSAlgorithm;
import agent.coverage.CoverageAlgorithm;
import agent.coverage.OldSearchAlgorithm;
import environment.EnvCellEnum;
import environment.Environment;
import environment.Options;

/**
 * @author moh_khaled
 *
 */
public class Agent {

	// Agent data
	private int id;
	private AgentStatus status, initialPos;	// coordinates & orientation
	private Environment env;	// reference to the environment

	private MapBuilder mapBuilder;
	private SearchMap searcher;
	private Communicator communicator;
	private CoverageAlgorithm coverageAlgorithm;

	private Vector<Point> trajectory;

	public Agent(int i, AgentStatus status, Environment env) {
		super();
		this.id = i;
		this.initialPos = status;
		this.env = env;

		int width = env.getEnvWidth(), height = env.getEnvHeight();

		mapBuilder = new MapBuilder(width, height, this);
		mapBuilder.getMap().id = env.getEnvID();
		searcher = new SearchMap(width, height, this);
		communicator = new Communicator(this);

		trajectory = new Vector<Point>();

		switch (env.options.coverageAlgorithm) {
		case OLD_ALGOZ:
			coverageAlgorithm = new OldSearchAlgorithm();
			break;
		case CFS:
			coverageAlgorithm = new CFSAlgorithm();
			break;
		case GS:
			// create GFS algorithm class
			break;
		default:
			break;
		}

	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Initializers
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void prepareGame() {
		// Initialization
		trajectory.clear();
		resetNextDestination();
		status = new AgentStatus(initialPos.coordinates.clone(), initialPos.orientation);
		searcher.init();
		communicator.clearLocalSearchInfo();

		//loadSearch("visited_2");
	}

	public void loadMap(String loadingID) {
		mapBuilder.loadMap(getMap().id+(loadingID != null ? "_"+loadingID : ""));
		mapBuilder.getMap().id = env.getEnvID();
	}

	public void loadSearch(String loadingID) {
		searcher.loadSearch(getMap().id+(loadingID != null ?"_visited_"+loadingID:""));
	}

	public void loadTrajectory(int game) {
		//String path = Utils.PATH_LOGS+env.NAME;
		String path = Options.PATH_LOGS+env.testName+"/trajs";

		FileLinesReader reader_trajs = new FileLinesReader(path+"/trajs.txt");
		FileLinesReader reader_meets = new FileLinesReader(path+"/meets.txt");
		for(int i = 0; i < game-1; i++) {
			reader_trajs.readLine();
			reader_meets.readLine();
		}
		String line_trajs = reader_trajs.readLine();
		String line_meets = reader_meets.readLine();

		trajectory.clear();
		env.clock.clearTimeStamps();

		String[] times = line_trajs.split("[()],?");
		for (String t : times) {
			if(t.trim().isEmpty()) continue;
			String[] values = t.split(",");
			int i = (id-1)*2;
			trajectory.add(new Point(new Integer(values[i]), new Integer(values[i+1])));
		}
		String[] meets = line_meets.split(",");
		for(int i = 0; i < meets.length; i+=2) {
			if(meets[i].trim().isEmpty()) continue;
			env.clock.addTimeStamp(new Integer(meets[i].trim()));
		}

		System.out.println("Game#"+ game + "): # of meetings: "+ meets.length/2);
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Public Actions
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void observeMap() {
		mapBuilder.getMap().setObserved(true);

		// get cells within agent's line of sight
		Vector<Point> cells = env.sightCells(status.coordinates, env.options.Ds);
		Vector<EnvCellEnum> values = env.readSensorsForCells(cells);
		Vector<Agent> neighbors = new Vector<Agent>();

		// Read sensors
		for(int i = 0; i < cells.size(); i++) {
			Point cell = cells.get(i);
			EnvCellEnum v = values.get(i);
			if(v == EnvCellEnum.OCCUP_AGENT) {
				v = EnvCellEnum.FREE;
				values.set(i, v);

				Agent neighbour = env.getSeekerAtCell(cell);
				if(neighbour != this) neighbors.add(neighbour);
			}
			mapBuilder.getMap().setCell(cell.row, cell.col, v);
		}

		// Share news with neighbors
		if (env.options.fullCommunication) {
			this.commit();
		}

		// Update components
		searcher.visitCell(status.coordinates.row, status.coordinates.col);
		communicator.addLocalMapInfo(cells, values);
		communicator.addLocalSearchInfo(cells);
		mapBuilder.updateMap();
	}

	public void takeATurn() {
		// Cancel destination path if the destination cell is already visited
		if(mapBuilder.getDestinatedPath() == null || !mapBuilder.getDestinatedPath().hasActions() ||
				evaluateCell(mapBuilder.getDestinatedPath().getDestination()) == 0) {
			mapBuilder.setDestinatedPath(selectPath());
		}
		if (!mapBuilder.getDestinatedPath().getPathActions().isEmpty()){
			ActionEnum action = null;
			do {
				action = mapBuilder.getDestinatedPath().getNextPathAction();
				if (action != null)
					applyAction(action);
			}while(action != ActionEnum.FORWARD);
		}
		trajectory.add(status.coordinates.clone());

		checkForTermination();
	}

	public void checkForTermination() {
		// TODO review this termination condition
		// If there is any unvisited cells
		for(int row = 0; row < getMap().getHeight(); row++) {
			for(int col = 0; col < getMap().getWidth(); col++) {
				if(getMap().isFree(row, col) && !isVisitedCell(new Point(row, col)))
					return;	// there still some work
			}
		}
		// NO unvisited, work is done
		env.endGame();
	}

	public Point takeAction(OrientationEnum action) {
		switch (action) {
		case NORTH:
			status.coordinates.row --;
			break;
		case SOUTH:
			status.coordinates.row ++;
			break;
		case EAST:
			status.coordinates.col ++;
			break;
		case WEST:
			status.coordinates.col --;
			break;
		default:
			break;
		}
		status.orientation = action;
		observeMap();
		return status.coordinates.clone();
	}

	public int getDistance(Point p1, Point p2) {
		return mapBuilder.getPlanner().getDistanceInPoints(p1, p2);
	}

	public Vector<Point> getPath(Point from, Point to) {
		return mapBuilder.getPlanner().pathPlan(
				new AgentStatus(from, OrientationEnum.NORTH), to).getPathCells();
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Communication Actions
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void commit() {

		communicator.commitToAgents(env.getSeekers());
		env.clock.addTimeStamp();
	}

	public void receiveMapInfo(Hashtable<Point, EnvCellEnum> cells)
	{
		mapBuilder.getMap().setObserved(true);
		for (Point cell : cells.keySet()) {
			mapBuilder.getMap().setCell(cell.row, cell.col, cells.get(cell));
		}
		mapBuilder.updateMap();
	}

	public void receiveSearchInfo(Vector<Point> cells, Agent sender) {
		for (Point cell : cells) {
			if(sender.isVisitedCell(cell))
				searcher.visitCell(cell.row, cell.col);
		}
	}

	public void receiveNextDestination(Point nextDestination) {
		if(nextDestination != null && nextDestination.equals(getNextDestination())) {
			if(getID() != 0) resetNextDestination();
		}
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Internal Actions
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	private void applyAction(ActionEnum action) {
		//TODO apply physical Action

		// update agent status
		updateAgentStatus(this.status, action);
	}

	private Path selectPath() {

		return coverageAlgorithm.selectPath(this);
	}

	public PriorityQueue<PointValue> evaluateCells(Vector<Point> cells) {

		int weightsCapacity = 5;
		PriorityQueue<PointValue> weights = new PriorityQueue<PointValue>(weightsCapacity, new PointValueComparator());
		env.tipInfo.clear();

		double w_map = 0.0067*mapBuilder.getMap().getUnknownsCount();//Math.pow(1.1, mapBuilder.getMap().getUnknownsCount()/70.0);
		double w_search = 1;
		Vector<Double> W_map = new Vector<Double>(), W_search = new Vector<Double>();

		if(w_map != 0)
			W_map = mapBuilder.suggestWeightsForCells(status, searcher, cells);
		if(w_search != 0)
			W_search = searcher.suggestWeightsForCells(status, mapBuilder.getPlanner(), cells);

		double s, m;
		for(int i = 0; i < cells.size(); i++) {
			Point cell = cells.get(i);
			s = (w_search != 0 ? W_search.get(i) : 0);
			m = (w_map != 0 ? W_map.get(i) : 0);
			if(m != 0) s = 1;
			double v = m*w_map + s*w_search;
			if(v != 0) {
				int d = mapBuilder.getPlanner().getDistance(status.coordinates, cell);
				v /= d;	// Z
				//v = (m*w_map + s*w_search)/d + h*w_hide;	// Z1
				//v = (m*w_map + s*w_search)/d + w_hide*(2*h + (1-d/width*height))/3;	// Z2
				//v = (m*w_map + s*w_search)/d + w_hide*(2*h + Math.exp(-.01*d))/3;	// Z3

				// Evaluate cell based on territory
				double terr = 1;
				if(env.needTerritories()) {
					terr = evaluateTerrForCell(cell);
				}
				v *= terr;

				if(env.options.debugMode) {
					String tip = "(s="+s+"), "+
							"(m="+m+"), "+
							"(d="+d+"), "+
							"(terr="+((int)(terr*100))/100.0+"), "+
							"(v="+((int)(v*100))/100.0+")";
					env.tipInfo.put(cell.clone(), tip);
				}
			}

			weights.add(new PointValue(cell, v));
			if(weights.size() > weightsCapacity) weights.poll();
		}

		return weights;
	}

	private double evaluateCell(Point cell) {
		Vector<Point> c = new Vector<Point>();
		c.add(cell);
		return evaluateCells(c).poll().value;
	}

	private double evaluateTerrForCell(Point cell) {
		int width = mapBuilder.getMap().getWidth(), height = mapBuilder.getMap().getHeight();;
		double v = 0;
		switch (env.options.strategy) {
		case Options.LEFT_RIGHT:
			if(id == 1) { //LEFT-Seeker
				//v = (cell.col <= width/2) ? width/2 : (width-cell.col)/10.0;
				//v = (cell.col <= width/2) ? width/2 : 1;
				v = (width-cell.col);
			} else if(id == 2) { //RIGHT-Seeker
				//v = (cell.col > width/2) ? width/2 : (cell.col+1)/10.0;
				//v = (cell.col > width/2) ? width/2 : 1;
				v = (cell.col+1);
			}
			break;
		case Options.UP_DOWN:
			if(id == 1) { //UP-Seeker
				//v = (cell.row <= height/2) ? height/2 : (height-cell.row)/10.0;
				//v = (cell.row <= height/2) ? height/2 : 1;
				v = (height-cell.row);
			} else if(id == 2) { //DOWN-Seeker
				//v = (cell.row > height/2) ? height/2 : (cell.row+1)/10.0;
				//v = (cell.row > height/2) ? height/2 : 1;
				v = (cell.row+1);
			}
			break;
		case Options.DIAGONAL:
			if(id == 1) { // LOWER-Diagonal
				//v = (cell.row > cell.col) ? height/2 : (cell.row-cell.col)/10.0;
				//v = (cell.row > cell.col) ? height/2 : 1;
				v = (cell.row-cell.col);
			} else if(id == 2) { // UPPER-Diagonal
				//v = (cell.row <= cell.col) ? height/2 : (cell.col-cell.row)/10.0;
				//v = (cell.row <= cell.col) ? height/2 : 1;
				v = (cell.col-cell.row);
			}
			break;
		default:
			break;
		}
		return Math.pow(5, v/width);
	}

	private void updateAgentStatus(AgentStatus status, ActionEnum action)
	{
		switch(action) {
		case FORWARD:
			if(status.orientation == OrientationEnum.NORTH) status.coordinates.row--;
			else if(status.orientation == OrientationEnum.SOUTH) status.coordinates.row++;
			else if(status.orientation == OrientationEnum.WEST) status.coordinates.col--;
			else if(status.orientation == OrientationEnum.EAST) status.coordinates.col++;
			observeMap();
			break;
		case ROT_L:
			if(status.orientation == OrientationEnum.NORTH)
				status.orientation = OrientationEnum.WEST;
			else if(status.orientation == OrientationEnum.WEST)
				status.orientation = OrientationEnum.SOUTH;
			else if(status.orientation == OrientationEnum.SOUTH)
				status.orientation = OrientationEnum.EAST;
			else if(status.orientation == OrientationEnum.EAST)
				status.orientation = OrientationEnum.NORTH;
			break;
		case ROT_R:
			if(status.orientation == OrientationEnum.NORTH)
				status.orientation = OrientationEnum.EAST;
			else if(status.orientation == OrientationEnum.EAST)
				status.orientation = OrientationEnum.SOUTH;
			else if(status.orientation == OrientationEnum.SOUTH)
				status.orientation = OrientationEnum.WEST;
			else if(status.orientation == OrientationEnum.WEST)
				status.orientation = OrientationEnum.NORTH;
			break;
		}
	}


	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Setters
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void setInitialPos(Point point) {
		this.initialPos.coordinates = point;
	}

	public void resetNextDestination() {
		mapBuilder.setDestinatedPath(null);
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Getters
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public Vector<Point> getTrajectory() {
		Vector<Point> traj = new Vector<Point>(trajectory.size());
		synchronized (trajectory) {
			for (Point point : trajectory) {
				traj.add(point);
			}
		}
		return traj;
	}

	public Point getNextDestination() {
		if(mapBuilder.getDestinatedPath() != null)
			return mapBuilder.getDestinatedPath().getDestination();
		else return null;
	}

	public boolean isVisitedCell(Point cell) {
		return searcher.isVisitedCell(cell.row, cell.col);
	}

	public boolean isStartingCell(Point cell) {
		return cell.equals(initialPos.coordinates);
	}

	public int getPartitionLabelOfCell(Point p) {
		return mapBuilder.getPlanner().getPartitionLabelOfCell(p);
	}

	public MapBuilder getMapBuilder() {
		return mapBuilder;
	}

	public SearchMap getSearchMap() {
		return searcher;
	}

	public Map getMap() {
		return mapBuilder.getMap();
	}

	public Environment getEnv() {
		return env;
	}

	public AgentStatus getStatus() {
		return status;
	}

	public int getID() {
		return id;
	}

	public void setLocation(Point location) {
		status.coordinates = location.clone();
		trajectory.add(status.coordinates.clone());
	}

}

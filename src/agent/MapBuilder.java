package agent;

import java.util.Vector;

import lib.datastructs.Map;
import lib.datastructs.Path;
import lib.datastructs.Point;
import agent.planner.Planner;
import environment.EnvCellEnum;

public class MapBuilder {

	private Map map;
	private Planner planner;
	
	private Agent seeker;
	
	private Path destinatedPath;
	
	//private static float DEFAULT_PROB = 0.3f;

	public MapBuilder(int width, int height, Agent parent) {
		super();
		map = new Map(width, height);
		this.seeker = parent;
		updateMap();
	}
	
	public void loadMap(String mapID) {
		map = Map.loadMapWithID(mapID);
		updateMap();
	}
	
	/**
	 * should be used along with map.setObserved(true) to track map changes in between
	 */
	public void updateMap()
	{
		if(map.isDirty()) {
			planner = new Planner(map);
			if(destinatedPath != null)
				destinatedPath = planner.pathPlan(seeker.getStatus(), destinatedPath.getDestination());
		}
		map.setObserved(false);
	}
	

	public Vector<Double> suggestWeightsForCells(AgentStatus status, SearchMap searcher, Vector<Point> cells) {
		
		Vector<Double> weights = new Vector<Double>();
		double value;
		for(int i = 0; i < cells.size(); i++) {
			value = 0.0;
			Point cell = cells.get(i);
			EnvCellEnum v = planner.getMap().getCell(cell.row, cell.col);
			if(!(v == EnvCellEnum.BLOCKED || v == EnvCellEnum.UNKNOWN 
					|| cell.equals(status.coordinates) ||
					seeker.isVisitedCell(cell))) {
				value = map.getNeighborsWithValue(cell, EnvCellEnum.UNKNOWN).size();
			}
			weights.add(value);
		}
		return weights;
	}
	
	public Map getMap() {
		return map;
	}

	public Planner getPlanner() {
		return planner;
	}

	public Path getDestinatedPath() {
		return destinatedPath;
	}

	public void setDestinatedPath(Path destinatedPath) {
		this.destinatedPath = destinatedPath;
	}
	
}

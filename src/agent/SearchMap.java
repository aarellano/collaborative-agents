package agent;

import java.util.Vector;

import lib.datastructs.Map;
import lib.datastructs.Path;
import lib.datastructs.Point;
import lib.datastructs.VisionDirectionEnum;
import lib.datastructs.VisitedStatusEnum;
import agent.planner.Planner;
import environment.EnvCellEnum;

public class SearchMap {

	private VisitedStatusEnum[][] visited;
	//	private int[][] territoryValue;
	private Agent seeker;

	public SearchMap(int width, int height, Agent parent) {
		super();
		visited = new VisitedStatusEnum[height][width];
		//		territoryValue = new int[height][width];
		this.seeker = parent;
	}

	public void init() {
		for(int i = 0; i < visited.length; i++)
			for(int j = 0; j < visited[0].length; j++)
				visited[i][j] = VisitedStatusEnum.UNVISITED;

		//initTerritory();
	}

	//	private void initTerritory() {
	//		Point territoryPoint = calcTerritoryPoint();
	//		int width = territoryValue[0].length, height = territoryValue.length;
	//		int maxDistance = (int) Math.ceil(Math.sqrt(width*width+height*height));
	//		for(int i = 0; i < height; i++) {
	//			for(int j = 0; j < width; j++) {
	//				Point cell = new Point(i, j);
	//				int distance = territoryPoint.manDistance(cell);
	//				territoryValue[i][j] = maxDistance - distance;
	//			}
	//		}
	//	}
	//
	//	private Point calcTerritoryPoint() {
	//		int count = seeker.getEnv().getSeekersCount();
	//		int id = seeker.getID();
	//		double unitAngle = 2*Math.PI/count;
	//		double angle = id*unitAngle;
	//		return null;
	//	}

	public Vector<Double> suggestWeightsForCells(AgentStatus status, Planner planner, Vector<Point> cells)
	{
		Vector<Double> weights = new Vector<Double>();
		double value;
		for(int i = 0; i < cells.size(); i++) {

			value = 0.0;
			Point cell = cells.get(i);
			EnvCellEnum cellValue = seeker.getMap().getCell(cell.row, cell.col);
			if(!(cellValue == EnvCellEnum.BLOCKED || cellValue == EnvCellEnum.UNKNOWN
					|| cell.equals(status.coordinates) ||
					seeker.isVisitedCell(cell))) {
				//value = getNeighborsWithVisited(cell, false).size();
				value = 1.0;
			}
			weights.add(value);
		}
		return weights;
	}

	public Vector<Point> getNeighborsWithVisited(Point cell, boolean visited) {
		return getNeighborsWithVisited(cell, visited, 3);
	}

	public Vector<Point> getNeighborsWithVisited(Point p, boolean visited, int distance) {
		Vector<Point> neighbors = seeker.getMap().getCellsWithinDistance(p, distance, VisionDirectionEnum.ALL_D);
		Vector<Point> result = new Vector<Point>();
		for (Point point : neighbors) {
			if(isVisitedCell(point.row, point.col) == visited &&
					!seeker.getMap().isObstacle(point.row, point.col) &&
					seeker.getMap().getSightLineBlocker(p, point) == null)
				result.add(point);
		}
		return result;
	}

	public void visitCell(int row, int col) {
		visited[row][col] = VisitedStatusEnum.VISITED;
	}

	public boolean isVisitedCell(int row, int col) {
		return visited[row][col] == VisitedStatusEnum.VISITED;
	}

	public boolean isPlannedCell(int row, int col) {
		for (Agent agent : seeker.getEnv().getSeekers()) {
			Path path = agent.getMapBuilder().getDestinatedPath();
			if (path == null)
				return true;
			Vector<Point> pathCells = path.getPathCells();
			if (pathCells.contains(new Point(row, col))) {
				//System.out.println(pathCells);
				return true;
			}
		}
		return false;
	}

	public VisitedStatusEnum getScanStatusCell(int row, int col) {
		return visited[row][col];
	}

	public void loadSearch(String mapID) {
		int width = visited[0].length, height = visited.length;
		Map map = Map.loadMapWithID(mapID);
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				EnvCellEnum cell = map.getCell(i, j);
				visited[i][j] = (cell == EnvCellEnum.FREE) ?
						VisitedStatusEnum.VISITED : VisitedStatusEnum.UNVISITED;
			}
		}
		seeker.setInitialPos(map.startingPoint.clone());
	}

}

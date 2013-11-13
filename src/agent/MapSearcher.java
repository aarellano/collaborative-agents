package agent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import lib.datastructs.Point;
import lib.datastructs.ScanStatusEnum;

public class MapSearcher {

	private ScanStatusEnum[][] visited;
	private Agent agent;

	public MapSearcher(Agent parent, ScanStatusEnum[][] currentStatus) {
		super();
		this.agent = parent;
		
		//clone currentStatus
		this.visited = new ScanStatusEnum[currentStatus.length][];
		for (int i = 0; i < currentStatus.length; i++){
			this.visited[i] = currentStatus[i].clone();
		}
	}
	
	public int[][] getDistancesMap(Point currentPos) {
		//set initial distances to -1 (unreachable)
		int nRows = visited.length;
		int nCols = visited[0].length;
		int[][] distances = new int[nRows][nCols];
		for (int i = 0; i < nRows; i++){
			for (int j = 0; j < nCols; j++){
				distances[i][j] = -1;
			}
		}
		distances[currentPos.row][currentPos.col] = 0;
		//lee algorithm
		int weight = 1;
		Queue<Point> neighs = new LinkedList<Point>(getUndefinedNeighs(currentPos, nRows, nCols, distances));
		Queue<Point> neighsOfNeighs = new LinkedList<Point>();
		while (!neighs.isEmpty()){
			Point newPos = neighs.remove();
			if (distances[newPos.row][newPos.col] == -1){
				distances[newPos.row][newPos.col] = weight;
				neighsOfNeighs.addAll(getUndefinedNeighs(newPos, nRows, nCols, distances));
			}
			if (neighs.isEmpty()){
				weight += 1;
				neighs = neighsOfNeighs;
				neighsOfNeighs = new LinkedList<Point>();
			}
		}
		
		return distances;
	}
	
	private Vector<Point> getUndefinedNeighs(Point pos, int nRows, int nCols, int[][] distances){
		Vector<Point> neighs = new Vector<Point>(4);
		//TODO: modify the VISITED by VISITED or UNVISITED 
		if ((pos.row-1 >= 0) && (visited[pos.row-1][pos.col] == ScanStatusEnum.VISITED)){
			Point p = new Point(pos.row-1, pos.col);
			neighs.add(p);
		}
		if ((pos.row+1 < nRows) && (visited[pos.row+1][pos.col] == ScanStatusEnum.VISITED)){
			Point p = new Point(pos.row+1, pos.col);
			neighs.add(p);
		}
		if ((pos.col-1 >= 0) && (visited[pos.row][pos.col-1] == ScanStatusEnum.VISITED)){
			Point p = new Point(pos.row, pos.col-1);
			neighs.add(p);
		}
		if ((pos.col+1 < nCols) && (visited[pos.row][pos.col+1] == ScanStatusEnum.VISITED)){
			Point p = new Point(pos.row, pos.col+1);
			neighs.add(p);
		}
		
		return neighs;
	}
	
	

}

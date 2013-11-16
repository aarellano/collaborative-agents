package agent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import lib.datastructs.Point;
import lib.datastructs.VisitedStatusEnum;

public class ShortestPathNaive {

//	private VisitedStatusEnum[][] visited;
	private Agent agent;

	public ShortestPathNaive(Agent parent/*, VisitedStatusEnum[][] currentStatus*/) {
		super();
		this.agent = parent;
		// parent.getMap().isObstacle(row, col)
		
		//clone currentStatus
//		this.visited = new VisitedStatusEnum[currentStatus.length][];
//		for (int i = 0; i < currentStatus.length; i++){
//			this.visited[i] = currentStatus[i].clone();
//		}
	}
	
	public Vector<Point> getPath2NearestUnvisited(Point currentPos, SearchMap currentSearchMap) {
		Point borrar = new Point(4,4);
		Vector<Point> trajectory = new Vector<Point>();
		int nRows = this.agent.getMap().getHeight();
		int nCols = this.agent.getMap().getWidth();
		Point[][] prevNeighs = new Point[nRows][nCols];
		Point p1 = new Point(-1,-1);
		for (int i = 0; i < nRows; i++){
			for (int j = 0; j < nCols; j++){
				prevNeighs[i][j] = p1;
			}
		}
		
		//lee algorithm
		Queue<Point> queue = new LinkedList<Point>();
		Vector<Point> neighs;
		queue.add(currentPos);
		Boolean found = false;
		Point lastPos = null;
//		Point prevPos = currentPos;
		while (!queue.isEmpty() && !found){
			lastPos = queue.remove();
			neighs = getFreeNeighs(lastPos, nRows, nCols);
			Iterator<Point> itr = neighs.iterator();
			while (itr.hasNext()){
				Point v = (Point) itr.next();
				if (p1.equals(prevNeighs[v.row][v.col])){ //this point was not traversed
					prevNeighs[v.row][v.col] = lastPos;
					queue.add(v);
				}
				if (!currentSearchMap.isVisitedCell(v.row,v.col)){
					found = true;
					lastPos = v;
				}
			}
		}
		
		if (found){
			while(!p1.equals(prevNeighs[lastPos.row][lastPos.col])){
				trajectory.add(0,lastPos);
				lastPos = prevNeighs[lastPos.row][lastPos.col];
				if (currentPos.equals(borrar))
					System.out.println(lastPos.toString());
				if (currentPos.equals(borrar)){
					System.out.println(this.agent.getMap().getCell(4,4));
					System.out.println(this.agent.getMap().getCell(4,3));
					System.out.println(this.agent.getMap().getCell(4,5));
					System.out.println(this.agent.getMap().getCell(5,3));
				}
			}
			if (currentPos.equals(borrar))
				System.out.println("sale2");
		}
		
		return trajectory;
	}
	
	@Deprecated
	public int[][] getDistancesMap(Point currentPos) {
		//set initial distances to -1 (unreachable)
		int nRows = this.agent.getMap().getHeight();
		int nCols = this.agent.getMap().getWidth();
		int[][] distances = new int[nRows][nCols];
		for (int i = 0; i < nRows; i++){
			for (int j = 0; j < nCols; j++){
				distances[i][j] = -1;
			}
		}
		distances[currentPos.row][currentPos.col] = 0;
		//lee algorithm
		int weight = 1;
		Queue<Point> neighs = new LinkedList<Point>(getFreeNeighs(currentPos, nRows, nCols));
		Queue<Point> neighsOfNeighs = new LinkedList<Point>();
		while (!neighs.isEmpty()){
			Point newPos = neighs.remove();
			if (distances[newPos.row][newPos.col] == -1){
				distances[newPos.row][newPos.col] = weight;
				neighsOfNeighs.addAll(getFreeNeighs(newPos, nRows, nCols));
			}
			if (neighs.isEmpty()){
				weight += 1;
				neighs = neighsOfNeighs;
				neighsOfNeighs = new LinkedList<Point>();
			}
		}
		
		return distances;
	}


	private Vector<Point> getFreeNeighs(Point pos, int nRows, int nCols){
		Vector<Point> neighs = new Vector<Point>(4);
		//TODO: I modified the VISITED by FREE. Does it work? 
		if ((pos.row-1 >= 0) && (this.agent.getMap().isFree(pos.row-1,pos.col))){
			Point p = new Point(pos.row-1, pos.col);
			neighs.add(p);
		}
		if ((pos.row+1 < nRows) && (this.agent.getMap().isFree(pos.row+1,pos.col))){
			Point p = new Point(pos.row+1, pos.col);
			neighs.add(p);
		}
		if ((pos.col-1 >= 0) && (this.agent.getMap().isFree(pos.row,pos.col-1))){
			Point p = new Point(pos.row, pos.col-1);
			neighs.add(p);
		}
		if ((pos.col+1 < nCols) && (this.agent.getMap().isFree(pos.row,pos.col+1))){
			Point p = new Point(pos.row, pos.col+1);
			neighs.add(p);
		}
		return neighs;
	}

	

}

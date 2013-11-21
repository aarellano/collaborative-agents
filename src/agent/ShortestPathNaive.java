package agent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import lib.datastructs.Point;
import lib.datastructs.VisitedStatusEnum;

public class ShortestPathNaive {

	private Agent agent;

	public ShortestPathNaive(Agent parent) {
		super();
		this.agent = parent;
	}
	
	public Vector<Point> getCFS(Point currentPos, SearchMap currentSearchMap) {
		Vector<Point> trajectory = getPath2NearestUnvisited(currentPos, currentSearchMap, false);
		if (this.agent.getEnv().options.takeRisk){
			Vector<Point> alternativeTrajectory = getPath2NearestUnvisited(currentPos, currentSearchMap, true);
			if (trajectory.size() > alternativeTrajectory.size()){
				trajectory = alternativeTrajectory;
				System.out.println("Assumming risks");
			}
		}
		return trajectory;
	}

	public Vector<Point> getPath2NearestUnvisited(Point currentPos, SearchMap currentSearchMap, boolean risky) {
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
		while (!queue.isEmpty() && !found){
			lastPos = queue.remove();
			if (risky){
				neighs = getFreeOrUnknownNeighs(lastPos, nRows, nCols);
			} else {
				neighs = getFreeNeighs(lastPos, nRows, nCols);
			}
			Iterator<Point> itr = neighs.iterator();
			while (itr.hasNext() && !found){
				Point v = (Point) itr.next();
				if (p1.equals(prevNeighs[v.row][v.col]) && !v.equals(currentPos)){ //this point was not traversed
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
			}
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

	
	private Vector<Point> getFreeOrUnknownNeighs(Point pos, int nRows, int nCols){
		Vector<Point> neighs = new Vector<Point>(4);
		if ((pos.row-1 >= 0) && ((this.agent.getMap().isFree(pos.row-1,pos.col))
								|| (this.agent.getMap().isUnknown(pos.row-1,pos.col)))){
			Point p = new Point(pos.row-1, pos.col);
			neighs.add(p);
		}
		if ((pos.row+1 < nRows) && ((this.agent.getMap().isFree(pos.row+1,pos.col))
									|| (this.agent.getMap().isUnknown(pos.row+1,pos.col)))){
			Point p = new Point(pos.row+1, pos.col);
			neighs.add(p);
		}
		if ((pos.col-1 >= 0) && ((this.agent.getMap().isFree(pos.row,pos.col-1))
								|| (this.agent.getMap().isUnknown(pos.row,pos.col-1)))){
			Point p = new Point(pos.row, pos.col-1);
			neighs.add(p);
		}
		if ((pos.col+1 < nCols) && ((this.agent.getMap().isFree(pos.row,pos.col+1))
									|| (this.agent.getMap().isUnknown(pos.row,pos.col+1)))){
			Point p = new Point(pos.row, pos.col+1);
			neighs.add(p);
		}
		return neighs;
	}

}

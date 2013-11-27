/**
 * 
 */
package agent.planner;

import java.util.Hashtable;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;

import lib.datastructs.ActionEnum;
import lib.datastructs.Map;
import lib.datastructs.OrientationEnum;
import lib.datastructs.Partition;
import lib.datastructs.Path;
import lib.datastructs.Point;
import lib.datastructs.Range;
import lib.datastructs.TwoPoints;
import agent.AgentStatus;

/**
 * @author moh_khaled
 *
 */
public class Planner {

	private Map map;
	private MapPartitioning partitioner;
	private PartitionsShortestPathFloyd shortestPathBuilder;
	OrientationEnum orientation = OrientationEnum.NORTH;	//Default

	private Hashtable<Point, Path> cachedPaths;
	private TreeMap<TwoPoints, Double> distances;
	private double maxDistance;

	public Planner(Map map) {
		super();
		this.map = map;
		partitioner = new RectanglarPartitioning();
		partitioner.partitionMap(this.map);
		//partitioner.printPartitions();
		shortestPathBuilder = new PartitionsShortestPathFloyd(partitioner);
		shortestPathBuilder.logPartitions(partitioner);

		cachedPaths = new Hashtable<Point, Path>();
		distances = new TreeMap<TwoPoints, Double>();
		// Init Lengths List
		maxDistance = map.getWidth()*map.getHeight();

		/*
		Vector<Point> cells = new Vector<Point>();
		for(int i = 0; i < map.getHeight(); i++)
			for(int j = 0; j < map.getWidth(); j++)
				if(map.getCell(i, j) == EnvCellEnum.FREE)
					cells.add(new Point(i, j));
		AgentStatus dummyStatus = new AgentStatus(new Point(0,0), OrientationEnum.NORTH);
		for(int i = 0; i < cells.size(); i++) {
			Point from = cells.get(i).clone();
			for(int j = i+1; j < cells.size(); j++) {
				Point to = cells.get(j).clone();
				dummyStatus.coordinates = from;
				double l = pathPlan(dummyStatus, to).getPathActions().size();
				distances.put(new TwoPoints(from, to), l);
			}
		}
		 */
	}

	public Path pathPlan(AgentStatus status, Point destination)
	{
		orientation = status.orientation;
		Point source = status.coordinates.clone();

		Point pathKey = getPathKey(source, destination);
		Path path = cachedPaths.get(pathKey);
		if(path == null) {
			path = new Path(source, destination);
			if(partitioner.getLabelOfCell(source.row, source.col) == -1 ||
					partitioner.getLabelOfCell(destination.row, destination.col) == -1)
				return null;
			//5. Path Planning: Floyd
			pathActions(path);
			//cachedPaths.put(pathKey, path);
		}

		//if(log_flag) printActions();
		//log_flag = false;
		return path;
	}

	public int getApproximatePathLength(Point position, Point endPoint) {
		Path path = new Path(position, endPoint);
		path.approximateCountOnly = true;
		if(partitioner.getPartitionOfCell(position.row, position.col).getLabel() == -1 ||
				partitioner.getPartitionOfCell(endPoint.row, endPoint.col).getLabel() == -1)
			return 0;
		//5. Path Planning: Floyd
		pathActions(path);
		return path.approximateCount;
	}

	private Point getPathKey(Point source, Point destination) {
		String s1 = source.row + "" + source.col;
		String s2 = destination.row + "" + destination.col;
		return new Point(Integer.parseInt(s1), Integer.parseInt(s2));
	}

	private void pathActions(Path path)
	{
		Point position = path.getSource().clone(), endPoint = path.getDestination().clone();
		Stack<Partition> partitions = shortestPathBuilder.getShortestPath(position, endPoint, partitioner);
		path.getPathPartitions().addAll(partitions);
		partitions.pop();
		Partition dst;
		while(!partitions.empty())
		{
			dst = partitions.pop();
			position = addActions(position.clone(), dst, path);
		}
		// move within dst cell to end_point
		int distance;
		if(position.row < endPoint.row)	//South
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.SOUTH));
			distance = endPoint.row - position.row;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(position.row+i+1, position.col));
				} else path.approximateCount ++;
			}
			position.row += distance;
		}
		else if(position.row > endPoint.row)	//North
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.NORTH));
			distance = position.row - endPoint.row;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(position.row-i-1, position.col));
				} else path.approximateCount ++;
			}
			position.row -= distance;
		}
		if(position.col < endPoint.col)  //East
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.EAST));
			distance = endPoint.col - position.col;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(position.row, position.col+i+1));
				} else path.approximateCount ++;
			}
			position.col += distance;
		}
		else if(position.col > endPoint.col)  //West
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.WEST));
			distance = position.col - endPoint.col;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(position.row, position.col-i-1));
				} else path.approximateCount ++;
			}
			position.col -= distance;
		}
	}

	private Point addActions(Point fromPoint, Partition toPartition, Path path)
	{
		Partition fromPartition = partitioner.getPartitionOfCell(fromPoint.row, fromPoint.col);
		int distance;
		// Actions fromCell toCell are mainly vertical only
		if(fromPoint.row < toPartition.getRow()-1)	//South
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.SOUTH));
			distance = toPartition.getRow() - 1 - fromPoint.row;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(fromPoint.row+i+1, fromPoint.col));
				} else path.approximateCount ++;
			}
			fromPoint.row += distance;
		}
		if(fromPoint.row > toPartition.getRow()+toPartition.getHeight())	//North
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.NORTH));
			distance = fromPoint.row - (toPartition.getRow()+toPartition.getHeight());
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(fromPoint.row-i-1, fromPoint.col));
				} else path.approximateCount ++;
			}
			fromPoint.row -= distance;
		}

		Range intersection = fromPartition.getPartitionHorizontalIntersection(toPartition);
		if(fromPoint.col < intersection.from)	//East
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.EAST));
			distance = intersection.from - fromPoint.col;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(fromPoint.row, fromPoint.col+i+1));
				} else path.approximateCount ++;
			}
			fromPoint.col += distance;
		}
		if(fromPoint.col > intersection.to)	//West
		{
			if(!path.approximateCountOnly)
				path.getPathActions().addAll(fixOrientation(OrientationEnum.WEST));
			distance = fromPoint.col - intersection.to;
			for(int i = 0; i < distance; i++) {
				if(!path.approximateCountOnly) {
					path.getPathActions().add(ActionEnum.FORWARD);
					path.pathLength ++;
					path.getPathCells().add(new Point(fromPoint.row, fromPoint.col-i-1));
				} else path.approximateCount ++;
			}
			fromPoint.col -= distance;
		}

		// make the step into the new cell >> now toCell is entered!
		if(fromPoint.row < toPartition.getRow())	//South
		{
			if(!path.approximateCountOnly) {
				path.getPathActions().addAll(fixOrientation(OrientationEnum.SOUTH));
				path.getPathActions().add(ActionEnum.FORWARD);
				path.pathLength ++;
				path.getPathCells().add(new Point(fromPoint.row+1, fromPoint.col));
			} else path.approximateCount ++;
			fromPoint.row ++;
		}
		if(fromPoint.row > toPartition.getRow()+toPartition.getHeight()-1)	//North
		{
			if(!path.approximateCountOnly) {
				path.getPathActions().addAll(fixOrientation(OrientationEnum.NORTH));
				path.getPathActions().add(ActionEnum.FORWARD);
				path.pathLength ++;
				path.getPathCells().add(new Point(fromPoint.row-1, fromPoint.col));
			} else path.approximateCount ++;
			fromPoint.row --;
		}

		return fromPoint;
	}

	private Vector<ActionEnum> fixOrientation(OrientationEnum desired)
	{
		Vector<ActionEnum> actions = new Vector<ActionEnum>();
		switch(orientation) {
		case NORTH:
			switch(desired) {
			case NORTH:
				break;
			case EAST:
				actions.add(ActionEnum.ROT_R);
				break;
			case SOUTH:
				actions.add(ActionEnum.ROT_R);
				actions.add(ActionEnum.ROT_R);
				break;
			case WEST:
				actions.add(ActionEnum.ROT_L);
				break;
			}
			break;
		case EAST:
			switch(desired) {
			case NORTH:
				actions.add(ActionEnum.ROT_L);
				break;
			case EAST:
				break;
			case SOUTH:
				actions.add(ActionEnum.ROT_R);
				break;
			case WEST:
				actions.add(ActionEnum.ROT_R);
				actions.add(ActionEnum.ROT_R);
				break;
			}
			break;
		case SOUTH:
			switch(desired) {
			case NORTH:
				actions.add(ActionEnum.ROT_R);
				actions.add(ActionEnum.ROT_R);
				break;
			case EAST:
				actions.add(ActionEnum.ROT_L);
				break;
			case SOUTH:
				break;
			case WEST:
				actions.add(ActionEnum.ROT_R);
				break;
			}
			break;
		case WEST:
			switch(desired) {
			case NORTH:
				actions.add(ActionEnum.ROT_R);
				break;
			case EAST:
				actions.add(ActionEnum.ROT_R);
				actions.add(ActionEnum.ROT_R);
				break;
			case SOUTH:
				actions.add(ActionEnum.ROT_L);
				break;
			case WEST:
				break;
			}
			break;
		}
		orientation = desired;
		return actions;
	}

	public int getDistance(Point p1, Point p2) {
		TwoPoints ppts = new TwoPoints(p1, p2);
		Double d = distances.get(ppts);
		if(d == null) {
			AgentStatus status = new AgentStatus(p1, OrientationEnum.NORTH);
			Path path = pathPlan(status, p2);
			d = (double)path.pathLength;
			//distances.put(ppts, d);
		}
		return d.intValue();
	}

	public double getDistanceNormalized(Point p1, Point p2) {
		return getDistance(p1, p2)/maxDistance;
	}

	public int getDistanceInPoints(Point p1, Point p2) {
		TwoPoints ppts = new TwoPoints(p1, p2);
		Double d = distances.get(ppts);
		if(d == null) {
			AgentStatus status = new AgentStatus(p1, OrientationEnum.NORTH);
			Path path = pathPlan(status, p2);
			if(path == null || !map.isValidPath(path)) return (int) maxDistance;
			d = (double)pathPlan(status, p2).getPathCells().size();
			//distances.put(ppts, d);
		}
		return d.intValue();
	}

	public int countTurnsInPath(Vector<ActionEnum> path)
	{
		int count = 0, size = path.size();
		if(size == 0) return 0;
		int i = (path.get(0) != ActionEnum.FORWARD && path.get(1) != ActionEnum.FORWARD) ? 2 :
			(path.get(0) != ActionEnum.FORWARD ? 1 : 0);
		for(; i < size; i++)
		{
			if(path.get(i) != ActionEnum.FORWARD) count ++;
		}
		return count;
	}

	public int countBranchesInPath(Vector<Partition> path)
	{
		int count =0;
		if(path.size() > 2) {
			for (Partition partition : path) {
				Vector<Partition> neighbors = shortestPathBuilder.getNeighboringPartitions(partition, partitioner);
				if(neighbors.size() > 2) count++;
			}
		}
		return count;
	}

	public void clearCache() {
		cachedPaths.clear();
	}

	public int getPartitionLabelOfCell(Point p) {
		return partitioner.getLabelOfCell(p.row, p.col);
	}

	public Map getMap() {
		return map;
	}

}

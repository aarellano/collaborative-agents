package lib.datastructs;

import java.util.Stack;
import java.util.Vector;


public class Path {

	private Point source, destination;
	private Vector<Partition> pathPartitions;
	private Vector<Point> pathCells;
	private Vector<ActionEnum> pathActions;
	public int pathLength;
	
	private int nextActionIndex;
	
	public boolean approximateCountOnly;
	public int approximateCount;
	
	public Path(Point source, Point destination) {
		super();
		this.source = source;
		this.destination = destination;
		pathPartitions = new Vector<Partition>();
		pathCells = new Vector<Point>();
		pathActions = new Vector<ActionEnum>();
		nextActionIndex = 0;
		pathLength = 0;
	}
	
	public boolean hasActions() {
		return nextActionIndex < pathActions.size();
	}
	
	public ActionEnum getNextPathAction() {
		ActionEnum action = null;
		if(hasActions()) {
			action = pathActions.get(nextActionIndex);
			nextActionIndex ++;
		}
		return action;
	}

	public double getFowardCount() {
		int size = pathActions.size(), count = 0;
		for(int i = 0; i < size; i++) {
			if(pathActions.get(i) == ActionEnum.FORWARD)
				count ++;
		}
		return count;
	}
	
	public Vector<Partition> getPathPartitions() {
		return pathPartitions;
	}

	public Vector<Point> getPathCells() {
		return pathCells;
	}

	public Vector<ActionEnum> getPathActions() {
		return pathActions;
	}

	public Point getSource() {
		return source;
	}

	public Point getDestination() {
		return destination;
	}
	
	public void printPath(Stack<Partition> path)
	{
		System.out.printf("\n");
		while(!path.empty())
		{
			Partition p = path.pop();
			System.out.printf("%d => ", p.getLabel());
		}
		System.out.printf("_\n");
	}

	public void printActions()
	{
		System.out.printf("\n");
		int size = pathActions.size();
		for(int i = 0; i < size; i++)
		{
			ActionEnum a = pathActions.get(i);
			char c = '|';
			if(a == ActionEnum.FORWARD) c = '|';
			else if(a == ActionEnum.ROT_R) c = 'R';
			else if(a == ActionEnum.ROT_L) c = 'L';
			System.out.printf("%c  ", c);
		}
		System.out.printf("_\n");
	}

}

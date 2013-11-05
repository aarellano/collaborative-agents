package agent.planner;

import java.util.Stack;
import java.util.Vector;

import lib.FileLinesWriter;
import lib.datastructs.Partition;
import lib.datastructs.Point;
import lib.datastructs.Range;
import environment.Options;

public class PartitionsShortestPathFloyd {

	public static int MY_INFINITY = Integer.MAX_VALUE;
	private Range[][] adjMatrix;
	int[][] dist;
	int[][] previous;
	
	public PartitionsShortestPathFloyd(MapPartitioning partitioner)
	{
		int size = partitioner.size();
		createAdjMatrix(partitioner);
		
		// init distance & previous array
		dist = new int[size][size];
		previous = new int[size][size];
	    for(int i = 0 ; i < size ; i++)
	    {
			for (int j = 0; j < size; j++)
			{
				dist[i][j] = (adjMatrix[i][j].from > -1) ? adjMatrix[i][j].weight : MY_INFINITY;
				previous[i][j] = i;
			}
	    }

		// shortest path (floyd)
		for(int k = 0 ; k < size ; k++)
		{
			for(int j = 0 ; j < size ; j++)
			{
				for(int i = 0 ; i < size ; i++)
				{
					int temp = dist[i][k] + dist[k][j];
					if(temp < dist[i][j])
					{
						dist[i][j] = temp;
						previous[i][j] = previous[k][j];
					}
				}
			}
		}
	}
	
	private void createAdjMatrix(MapPartitioning partitioner)
	{
		int size = partitioner.size();
		//init adjMatrix
		adjMatrix = new Range[size][size];

		//build adjMat
		for(int i = 0; i < size; i++)
		{
			Partition from = partitioner.getPartitionByLabel(i+1);
			adjMatrix[i][i] = new Range(from.getCol(), 
					from.getCol()+from.getWidth()-1);
			for(int j = i+1; j < size; j++)
			{
				Partition to = partitioner.getPartitionByLabel(j+1);
				adjMatrix[i][j] = adjMatrix[j][i] = from.getPartitionIntersection(to);
				adjMatrix[i][j].weight = from.getHeight();
				adjMatrix[j][i].weight = to.getHeight();
			}
		}
	}
	
	public Stack<Partition> getShortestPath(Point from, Point to, MapPartitioning partitioner)
	{
		Partition fromPartition = partitioner.getPartitionOfCell(from.row, from.col);
		Partition toPartition = partitioner.getPartitionOfCell(to.row, to.col);
		if(fromPartition.getLabel() == -1 || toPartition.getLabel() == -1) return null;
		Stack<Partition> path = new Stack<Partition>();

		int[] pathy = previous[fromPartition.getLabel()-1];
		path.push(toPartition);
		int prev = pathy[toPartition.getLabel()-1];
		for(;;)
		{
			if(prev == fromPartition.getLabel()-1) break;
			else
			{
				path.push(partitioner.getPartitionByLabel(prev+1));
				prev = pathy[prev];
			}
		}
		path.push(fromPartition);
		return path;
	}
	
	public Vector<Partition> getNeighboringPartitions(Partition p, MapPartitioning partitioner)
	{
		Vector<Partition> neighbors = new Vector<Partition>();
		for(int i = 0; i < adjMatrix.length; i++)
			if(p.getLabel()-1 != i && adjMatrix[p.getLabel()-1][i].from != -1)
				neighbors.add(partitioner.getPartitionByLabel(i+1));
		return neighbors;
	}
	
	public void printAdjMatrix()
	{
		int size = adjMatrix.length;
		for (int i = 0; i < size; i++)
		{
			System.out.printf("\n");
			for (int j = 0; j < size; j++)
			{
				System.out.printf("%3d,%3d ", adjMatrix[i][j].from, adjMatrix[i][j].to);
			}
		}
	}
	
	public void logPartitions(MapPartitioning partitioner) {
		FileLinesWriter writer = new FileLinesWriter(
				Options.PATH_RES+"mst.txt", false);
		String edge = "";
		Partition from;
		Vector<Partition> toList;
		for (Integer fromKey : partitioner.partitions.keySet()) {
			from = partitioner.partitions.get(fromKey);
			if(from.getWidth() > from.getHeight()) {	// Horizontal
				float y = from.getRow()+(from.getHeight()-1)/2.0f;
				edge = from.getCol()+" "+y+" "+(from.getCol()+from.getWidth()-1)+" "+y;
			} else {	// Vertical
				float x = from.getCol()+(from.getWidth()-1)/2.0f;
				edge = x+" "+from.getRow()+" "+x+" "+(from.getRow()+from.getHeight());
			}
			writer.writeLine(edge);
			toList = getNeighboringPartitions(from, partitioner);
			for (Partition to : toList) {
				edge = "";
				if(from.getWidth() > from.getHeight()) {	// Horizontal
					Range inter = from.getPartitionIntersection(to);
					float x = inter.from+(inter.to-inter.from)/2.0f;
					float y_from = from.getRow()+(from.getHeight()-1)/2.0f;
					float y_to = to.getRow()+(to.getHeight()-1)/2.0f;
					edge = x+" "+y_from+" "+x+" "+y_to;
				} else if(to.getWidth() > to.getHeight()) {	// Vertical && to_Horizontal
					float x = from.getCol()+(from.getWidth()-1)/2.0f;
					float y_from = from.getRow();
					float y_to = to.getRow()+(to.getHeight()-1)/2.0f;
					edge = x+" "+y_from+" "+x+" "+y_to;
				} else if(from.getRow() > to.getRow()){ // Vertical && to North
					float y = from.getRow();
					float x_from = from.getCol()+(from.getWidth()-1)/2.0f-1;
					float x_to = to.getCol()+(to.getWidth()-1)/2.0f+1;
					edge = x_from+" "+y+" "+x_to+" "+y;
				} else {
					float y = to.getRow();
					float x_from = from.getCol()+(from.getWidth()-1)/2.0f;
					float x_to = to.getCol()+(to.getWidth()-1)/2.0f;
					edge = x_from+" "+y+" "+x_to+" "+y;
				}
				writer.writeLine(edge);
			}
		}
		writer.close();
	}
}

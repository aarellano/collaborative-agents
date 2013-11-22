/**
 * 
 */
package agent.planner;

import java.util.Hashtable;

import lib.datastructs.Map;
import lib.datastructs.Partition;

/**
 * @author moh_khaled
 *
 */
public abstract class MapPartitioning {

	//Decomposition
	///////////////
	protected int[][] labels;
	protected Hashtable<Integer, Partition> partitions;

	public void partitionMap(Map map) {
		labels = new int[map.getHeight()][map.getWidth()];
		for(int i = 0; i < map.getHeight(); i++)
			for(int j = 0; j < map.getWidth(); j++)
				labels[i][j] = -1;
		partitions = new Hashtable<Integer, Partition>();
	}

	void setLabels(int label, int i, int j, int width, int height)
	{
		for(int l = 0; l < height; l++)
			for(int k = 0; k < width; k++)
				labels[i+l][j+k] = label;
	}

	public Partition getPartitionOfCell(int i, int j) {
		return partitions.get(new Integer(getLabelOfCell(i, j)));
	}

	protected int getLabelOfCell(int i, int j) {
		return labels[i][j];
	}

	protected Partition getPartitionByLabel(int label) {
		return partitions.get(new Integer(label));
	}

	protected void addPartition(Partition partition) {
		//partitions.remove(new Integer(partition.getLabel()));
		partitions.put(partition.getLabel(), partition);
	}

	public int size() {
		return partitions.size();
	}

	public void printPartitions()
	{
		for(int i = 0; i <  labels.length; i++)
		{
			System.out.printf("\n");
			for (int j = 0; j < labels[i].length; j++)
			{
				System.out.printf("%3d ", labels[i][j]);
			}
		}
		System.out.printf("\n");
	}

}

/**
 * 
 */
package agent.planner;

import lib.datastructs.Map;
import lib.datastructs.Partition;
import environment.EnvCellEnum;

/**
 * @author moh_khaled
 *
 */
public class RectanglarPartitioning extends MapPartitioning {

	@Override
	public void partitionMap(Map map) {
		super.partitionMap(map);

		int width = map.getWidth(), height = map.getHeight();

		Partition partition = null;
		int label = 1;
		// iterate over rows
		for(int row = 0; row < height; row++)
		{
			int col = 0;
			while(col < width && map.getCell(row, col) != EnvCellEnum.FREE) col++;
			if(col >= width) continue;
			partition = new Partition(row, col, label);

			// iterate over columns
			for(; col < width+1; col++)
			{
				if(col < width && map.getCell(row, col) == EnvCellEnum.FREE) {
					labels[row][col] = label;
				} else {
					partition.setWidth(col - partition.getCol());
					int prev_label = partition.getRow()-1 < 0 ? -1 :
						getLabelOfCell(partition.getRow()-1, partition.getCol());
					Partition p = prev_label <= 0 ? new Partition(-1, -1, prev_label) :
						getPartitionByLabel(prev_label);
					// check equivalence between this cell & the cell from previous row, then merge them
					if(p.getCol() == partition.getCol() && p.getWidth() == partition.getWidth()) {
						p.setHeight(p.getHeight()+1);
						addPartition(p);
						setLabels(prev_label,
								partition.getRow(), partition.getCol(),
								partition.getWidth(), partition.getHeight());
					} else {
						addPartition(partition);
						label++;
					}

					while(col < width && map.getCell(row, col) != EnvCellEnum.FREE) col++;
					if(col < width) col--;
					partition = new Partition(row, col+1, label);
				}
			}
		}
	}


}

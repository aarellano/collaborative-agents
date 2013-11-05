package lib.datastructs;

import java.awt.Dimension;

public class Partition {

	private int row, col;
	private Dimension dimension;
	private int label;
	
	public Partition(int row, int col, int label) {
		this.row = row;
		this.col = col;
		this.dimension = new Dimension(1, 1);
		this.label = label;
	}
	
	public Range getPartitionIntersection(Partition p) {
		Range colInter = Range.rangeIntersection(
				new Range(col, col+getWidth()-1), 
				new Range(p.getCol(), p.getCol()+p.getWidth()-1));
		Range rowInter = Range.rangeIntersection(
				new Range(row, row+getHeight()-1), 
				new Range(p.getRow(), p.getRow()+p.getHeight()-1));
		return (row+getHeight()-p.getRow() == 0 || 
				p.getRow()+p.getHeight()-row == 0 ||
				rowInter.from >= 0) ? 
				colInter : new Range(-1, -1);
	}

	public int getLabel() {
		return label;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}
		
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void setWidth(int width) {
		this.dimension.width = width;
	}
	
	public void setHeight(int height) {
		this.dimension.height = height;
	}
	
	public int getWidth() {
		return this.dimension.width;
	}
	
	public int getHeight() {
		return this.dimension.height;
	}
}

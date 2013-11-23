package lib.datastructs;


public class Partition {

	private int row, col;
	private int width, height;
	private int label;

	public Partition(int row, int col, int label) {
		this.row = row;
		this.col = col;
		this.width = this.height = 1;
		this.label = label;
	}

	@Override
	public String toString() {
		return label + ", (Row:" + row + ", Col:" + col +
				", Width:" + width + ", Height:" + height + ")";
	}

	public Range getPartitionHorizontalIntersection(Partition p) {
		Range horizontalIntersection = Range.rangeIntersection(
				new Range(col, col+getWidth()-1),
				new Range(p.getCol(), p.getCol()+p.getWidth()-1));
		Range verticalIntersection = Range.rangeIntersection(
				new Range(row, row+getHeight()-1),
				new Range(p.getRow(), p.getRow()+p.getHeight()-1));

		return (row+getHeight()-p.getRow() == 0 ||
				p.getRow()+p.getHeight()-row == 0 ||
				verticalIntersection.from >= 0) ?
						horizontalIntersection : new Range(-1, -1);
	}

	public int getLabel() {
		return label;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}

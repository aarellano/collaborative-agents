package lib.datastructs;

public class Point {

	public int row, col;

	public Point() {
		row = col = -1;
	}

	public Point(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public Point(String string) {
		String[] c = string.split(",");
		row = Integer.parseInt(c[0].trim());
		col = Integer.parseInt(c[1].trim());
	}

	public int manDistance(Point p) {
		return Math.abs(this.row-p.row) + Math.abs(this.col-p.col);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		return true;
	}

	@Override
	public Point clone() {
		return new Point(row, col);
	}

	@Override
	public String toString() {
		return row + "," + col;
	}

}

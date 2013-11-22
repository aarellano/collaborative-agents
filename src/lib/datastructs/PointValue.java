package lib.datastructs;

public class PointValue {

	public Point point;
	public double value;
	
	public PointValue(Point point, double value) {
		this.point = point.clone();
		this.value = value;
	}
	
}

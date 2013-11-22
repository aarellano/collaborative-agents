/**
 * 
 */
package agent;


import lib.datastructs.OrientationEnum;
import lib.datastructs.Point;

/**
 * @author moh_khaled
 *
 */
public class AgentStatus {

	public Point coordinates;
	public OrientationEnum orientation;

	public AgentStatus(Point coordinates, OrientationEnum orientation) {
		super();
		this.coordinates = coordinates;
		this.orientation = orientation;
	}

	public AgentStatus(int row, int col, OrientationEnum orientation) {
		super();
		this.coordinates = new Point(row, col);
		this.orientation = orientation;
	}

	@Override
	public String toString() {
		return "Coordinates: (" + coordinates.row + ", " + coordinates.col + ") Oriented: " + orientation;
	}

	@Override
	protected Object clone() {
		return new AgentStatus(coordinates.row, coordinates.col, orientation);
	}

}

package agent.coverage;

import lib.datastructs.Point;
import agent.Agent;

public class GaussianWeight {

	private Agent agent;

	private Point mean;
	private double sigmaSquared, normalFactor = 1;

	public GaussianWeight(Agent agent) {
		this.agent = agent;
		calculateParameters(new Point(agent.getMap().getHeight()/2,
				agent.getMap().getWidth()/2));
		normalize();
	}

	private void calculateParameters(Point origin) {
		double angle = 360.0/agent.getEnv().getSeekersCount();
		double myAngle = angle*(agent.getID())-1;
		double tan = Math.abs(Math.tan(Math.toRadians(myAngle)));
		int quadNum = (int) (myAngle/90)+1;
		mean = new Point();
		// decide mean point
		int x = 0, y = 0;
		int width = agent.getMap().getWidth(), height = agent.getMap().getHeight();
		if(quadNum == 1) {
			x = width-origin.col;
			y = (int)(tan*x);
			mean.col = width - 1;
			mean.row = origin.row - y;
			if (mean.row < 0) {
				y = origin.row;
				x = (int)(y/tan);
				mean.row = 0;
				mean.col = origin.col + x;
			}
		} else if(quadNum == 2) {
			x = origin.col;
			y = (int)(tan*x);
			mean.col = 0;
			mean.row = origin.row - y;
			if (mean.row < 0) {
				y = origin.row;
				x = (int)(y/tan);
				mean.row = 0;
				mean.col = origin.col - x;
			}
		} else if(quadNum == 3) {
			x = origin.col;
			y = (int)(tan*x);
			mean.col = 0;
			mean.row = origin.row + y;
			if (mean.row > height-1) {
				y = height - origin.row;
				x = (int)(y/tan);
				mean.row = height-1;
				mean.col = origin.col - x;
			}
		} else if(quadNum == 4) {
			x = width - origin.col;
			y = (int)(tan*x);
			mean.col = width-1;
			mean.row = origin.row + y;
			if (mean.row > height-1) {
				y = height - origin.row;
				x = (int)(y/tan);
				mean.row = height-1;
				mean.col = origin.col + x;
			}
		}
		//System.out.println(endPoint);
		// calculate variance
		sigmaSquared = Math.abs(Math.tan(Math.toRadians(angle-1)))*width/4;
	}

	private void normalize() {
		double sum = 0;
		for(int row = 0; row < agent.getMap().getHeight(); row++) {
			for(int col = 0; col < agent.getMap().getWidth(); col++) {
				sum += weightPoint(new Point(row, col));
			}
		}
		normalFactor = sum;
	}

	public double weightPoint(Point p) {
		Point x = new Point(p.row - mean.row, p.col - mean.col);
		double pow = -0.5 * (x.row*x.row + x.col*x.col) / sigmaSquared;
		double phi = Math.exp(pow) / Math.sqrt(2 * Math.PI * sigmaSquared);
		return phi / normalFactor;
	}

}
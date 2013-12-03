package agent.coverage;

import java.util.Vector;

import lib.datastructs.Point;
import agent.Agent;

public class GaussianWeight {

	private Agent agent;

	private Point mean;
	private double sigmaSquared, normalFactor = 1;

	private double[][] weights;

	public GaussianWeight(Agent agent) {
		this(agent, agent.getEnv().getSeekersCount(),
				new Point(agent.getMap().getHeight()/2,
						agent.getMap().getWidth()/2));
	}

	public GaussianWeight(Agent agent, int agentsCount, Point center) {
		this.agent = agent;
		weights = new double[agent.getMap().getHeight()][agent.getMap().getWidth()];
		calculateParameters(center, agentsCount);
		normalize();
		for(int row = 0; row < weights.length; row++) {
			for(int col = 0; col < weights[0].length; col++) {
				weights[row][col] = weightPoint(new Point(row, col));
			}
		}
	}

	private void calculateParameters(Point origin, int count) {
		double angle = 360.0/count;
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
		//		sigmaSquared = Math.abs(Math.tan(Math.toRadians(angle-1)))*width;
		sigmaSquared = agent.getMap().getHeight()*agent.getMap().getWidth();
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

	public double getWeight(Point p) {
		return weights[p.row][p.col];
	}

	private double weightPoint(Point p) {
		Point x = new Point(p.row - mean.row, p.col - mean.col);
		double pow = -0.5 * (x.row*x.row + x.col*x.col) / sigmaSquared;
		double phi = Math.exp(pow) / Math.sqrt(2 * Math.PI * sigmaSquared);
		return phi / normalFactor;
	}

	public double weightPath(Vector<Point> path) {
		Point destination = path.lastElement();
		double weight =  path.size() + 1;/// (agent.getMap().getWidth()*agent.getMap().getHeight()); // normalize
		weight = 1.0 / weight;
		weight = getWeight(destination) * weight;//this.weightPoint(destination) * weight;
		return weight;
	}

	//	public static double weightPath(Agent agent, Vector<Point> path, int partitionsCount, Point center) {
	//		return new GaussianWeight(agent, partitionsCount, center).weightPath(path);
	//	}
	//
	//	public static double weightPath(Agent agent, Vector<Point> path) {
	//		return new GaussianWeight(agent).weightPath(path);
	//	}
}

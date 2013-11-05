package lib;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import lib.datastructs.Point;
import lib.datastructs.PointValue;
import lib.datastructs.PointValueCollections;

public class Utils {
	
	static Random rand = new Random(System.currentTimeMillis());
	
	public static int selectByRoulette(Collection<Double> weights, double minWeight) {
	    Vector<Double> roulette = new Vector<Double>();
	    double sum = 0, v;
		int size = weights.size();
		double max_value = maxOfList(weights);
		for (Double w : weights) {
			if(w < 0)
				v = 0;
			else if(w == 0)
				v = minWeight;
			else
				v = w/max_value;
			roulette.add(v);
			sum += v;
	    }

	    int selector = rand.nextInt(100), selected = 0;
		float buf = 0;
	    for(int k = 0; k < size; k++)
	    {
	        double norm = (roulette.get(k)*100/sum);
	        if(selector < norm + buf)
	        {
	            selected = k;
	            break;
	        }
	        buf += norm;
	    }
		return selected;
	}
	
	public static int selectByMax(Collection<Double> weights, double minWeight) {
		int selected = 0;
		Vector<Integer> maxs = maxsOfList(weights);
		//selected = rand.nextInt(maxs.size());
		selected = maxs.size()-1;
		return maxs.get(selected);
	}
	
	public static Double maxOfList(Collection<Double> list) {
		Double max = 0.0;
		for (Double object : list) {
			if(object > max) max = object;
		}
		return max;
	}
	
	public static Vector<Integer> maxsOfList(Collection<Double> list) {
		int i = 0;
		double max = maxOfList(list);
		Vector<Integer> maxs = new Vector<Integer>();
		for (Double buf : list) {
			if(buf == max)
				maxs.add(i);
			i++;
		}
		return maxs;
	}
	
	public static Double minOfList(Collection<Double> list) {
		Double min = Double.MAX_VALUE;
		for (Double object : list) {
			if(object < min && object > 0.0) min = object;
		}
		return min;
	}

	public static Vector<Integer> minsOfList(Collection<Double> list) {
		int i = 0;
		double min = minOfList(list);
		Vector<Integer> mins = new Vector<Integer>();
		for (Double buf : list) {
			if(buf == min)
				mins.add(i);
			i++;
		}
		return mins;
	}
	
	// Bresenham's line drawing algorithm
	public static Vector<Point> sightLine(Point from, Point to) {
		Vector<Point> points = new Vector<Point>();
		int current_i, current_j;

		// Bresenham's algorithm works only on line in 4th quarter ( 0 < slope < 1)
	    boolean mirror_i = from.row > to.row;
	    boolean mirror_j = from.col > to.col;

	    int src_i = from.row;
	    int src_j = from.col;
	    int dst_i = mirror_i ? 2*from.row - to.row : to.row;
	    int dst_j = mirror_j ? 2*from.col - to.col : to.col;

		current_i = src_i; current_j = src_j;
		points.add(new Point(current_i, current_j));
	    /////////////////////////////
		// Vertical Line
	    if(dst_j == src_j) {
	        for(int i = src_i; i < dst_i-1; i++) {
	        	current_i = mirror_i?current_i-1:current_i+1;
	        	points.add(new Point(current_i, current_j)); // ?Up:Down
	        }
	    }
	    // Horizontal Line
	    else if(dst_i == src_i) {
	        for(int j = src_j; j < dst_j-1; j++) {
	        	current_j = mirror_j?current_j-1:current_j+1;
	        	points.add(new Point(current_i, current_j));	// ?Left:Right
	        }
	    }
	    // General Line: Bresenham's Line Algorithm
	    else{
	        int dj = dst_j - src_j;
	        int di = dst_i - src_i;
	        int p = 2*di - dj;
	        //int i = src_i;
	        int j = src_j;

	        while(j < dst_j) {
	            if(p < 0) {
	                j ++;
	                //i = i;
	                p += 2*di;
	                // Right action
	                current_j = mirror_j?current_j-1:current_j+1;
	                points.add(new Point(current_i, current_j));	// ?Left:Right
	            } else {
	                j ++;
	                //i ++;
	                p += 2*(di-dj);
	                // Right & Down actions
	                current_j = mirror_j?current_j-1:current_j+1;
	                points.add(new Point(current_i, current_j));	// ?Left:Right
	                current_i = mirror_i?current_i-1:current_i+1;
	                points.add(new Point(current_i, current_j));	// ?Up:Down
	            }
	        }
	    }
	    /////////////////////////////
		return points;
	}
	
	public static int getNextRandom(int max) {
		return rand.nextInt(max);
	}
	
	public static PointValueCollections getPointAndValueCollections(Collection<PointValue> collection) {
		Vector<Point> points = new Vector<Point>(collection.size());
		Vector<Double> values = new Vector<Double>(collection.size());
		Iterator<PointValue> iter = collection.iterator();
		while(iter.hasNext()) {
			PointValue element = iter.next();
			points.add(element.point.clone());
			values.add(element.value);
		}
		return new PointValueCollections(points, values);
	}
	
	@SuppressWarnings("unchecked")
	public static String listToString(Collection list, String separator) {
		String string = "";
		Iterator<Object> iter = list.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			string += o;
			if(iter.hasNext()) string += separator;
		}
		return string;
	}
	
	@SuppressWarnings("unchecked")
	public static Vector<Integer> occurencesInList(Collection list, Object object) {
		Vector<Integer> occurs = new Vector<Integer>();
		int i = 0;
		for (Object o : list) {
			if(o.equals(object))
				occurs.add(i);
			i++;
		}
		return occurs;
	}
	
	public static Point medianOfPoints(Vector<Point> points) {
		Point median = null, mean = new Point(0,0);
		for (Point point : points) {
			mean.row += point.row;
			mean.col += point.col;
		}
		mean.row /= points.size();
		mean.col /= points.size();
		double minD = Integer.MAX_VALUE;
		for (Point point : points) {
			int d = mean.manDistance(point);
			if(d < minD) {
				minD = d;
				median = point.clone();
			}
		}
		return median;
	}
}

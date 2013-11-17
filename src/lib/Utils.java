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
		
		// Bresenham's line drawing algorithm
		int x = from.col, x2 = to.col;
		int y = from.row, y2 = to.row;
		int w = x2 - x ;
		int h = y2 - y ;
		int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
		if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
		if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
		if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
		int longest = Math.abs(w) ;
		int shortest = Math.abs(h) ;
		if (!(longest>shortest)) {
			longest = Math.abs(h) ;
			shortest = Math.abs(w) ;
			if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
			dx2 = 0 ;            
		}
		int numerator = longest >> 1 ;
		for (int i=0;i<=longest;i++) {
			points.add(new Point(y, x));
			numerator += shortest ;
			if (!(numerator<longest)) {
				numerator -= longest ;
				x += dx1 ;
				y += dy1 ;
			} else {
				x += dx2 ;
				y += dy2 ;
			}
		}
		points.add(new Point(to.row, to.col));

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

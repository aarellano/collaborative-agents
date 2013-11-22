package lib.datastructs;

public class Range {

	public int from, to;
	public int weight;
		
	public Range(int from, int to) {
		super();
		this.from = from;
		this.to = to;
		weight = 0;
	}

	public Range(int from, int to, int weight) {
		super();
		this.from = from;
		this.to = to;
		this.weight = weight;
	}



	public static Range rangeIntersection(Range r1, Range r2)
	{
		Range r = new Range(-1, -1);
		if(r1.from >= r2.from && r1.from <= r2.to) r.from = r1.from;
		else if(r2.from >= r1.from && r2.from <= r1.to) r.from = r2.from;
		if(r1.to >= r2.from && r1.to <= r2.to) r.to = r1.to;
		else if(r2.to >= r1.from && r2.to <= r1.to) r.to = r2.to;
		return r;
	}
}

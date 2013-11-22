package lib.datastructs;

public class TwoPoints implements Comparable<TwoPoints> {
	public Point p1;
	public Point p2;
	public TwoPoints(Point p1, Point p2) {
		this.p1 = p1.clone();
		this.p2 = p2.clone();
	}
	@Override
	public int hashCode() {
		int h1 = p1.hashCode(), h2 = p2.hashCode();
		final int prime = 31;
		int result = 1;
		result = prime * result + Math.min(h1, h2);
		result = prime * result + Math.max(h1, h2);
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
		TwoPoints other = (TwoPoints) obj;
		if (!((p1.equals(other.p1) && p2.equals(other.p2)) ||
				(p1.equals(other.p2) && p2.equals(other.p1))))
			return false;
		return true;
	}

	@Override
	public TwoPoints clone() {
		return new TwoPoints(p1, p2);
	}

	@Override
	public int compareTo(TwoPoints o) {
		Integer myHash = hashCode();
		return myHash.compareTo(new Integer((o.hashCode())));
	}

}

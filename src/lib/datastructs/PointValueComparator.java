package lib.datastructs;
import java.util.Comparator;

public class PointValueComparator implements Comparator<PointValue>
{
	@Override
	public int compare(PointValue x, PointValue y)
	{
		if (x.value < y.value)
		{
			return -1;
		}
		if (x.value > y.value)
		{
			return 1;
		}
		return 0;
	}
}
package agent;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import lib.datastructs.OrientationEnum;
import lib.datastructs.Point;
import lib.datastructs.VisitedStatusEnum;

import org.junit.Before;
import org.junit.Test;

import environment.Environment;

public class ShortestPathNaiveTest {
	private ShortestPathNaive searcher;
	private ShortestPathNaive searcher2;

	@Before
	public void setUp() throws Exception
	{
		Point coords = new Point(2, 1);
		AgentStatus status = new AgentStatus(coords, OrientationEnum.NORTH);
		Point[] coordinates = {coords};
		Environment env = new Environment("rep", coordinates);
		Agent agent = new Agent(0, status, env);
		VisitedStatusEnum[][] currentStatus = {{VisitedStatusEnum.UNVISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED},
				{VisitedStatusEnum.UNVISITED, VisitedStatusEnum.UNVISITED, VisitedStatusEnum.UNVISITED, VisitedStatusEnum.VISITED},
				{VisitedStatusEnum.UNVISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED}};
		searcher = new ShortestPathNaive(agent);

		VisitedStatusEnum[][] currentStatus2 = {{VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED},
				{VisitedStatusEnum.VISITED, VisitedStatusEnum.UNVISITED, VisitedStatusEnum.UNVISITED, VisitedStatusEnum.VISITED},
				{VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED, VisitedStatusEnum.VISITED}};
		searcher2 = new ShortestPathNaive(agent);
	}

	@Test
	public void testGetDistancesMap() {
		int [][] expected_result = {{-1,6,5,4},{-1,-1,-1,3},{-1,0,1,2}};

		Point newPos = new Point(2, 1);
		int[][] distances_map = searcher.getDistancesMap(newPos);
		System.out.println(Arrays.deepToString(distances_map));
		assertTrue(Arrays.deepEquals(distances_map,expected_result));
	}

	@Test
	public void testGetDistancesMap2() {
		int [][] expected_result = {{2,3,4,5},{1,-1,-1,4},{0,1,2,3}};

		Point newPos = new Point(2, 0);
		int[][] distances_map = searcher2.getDistancesMap(newPos);
		System.out.println(Arrays.deepToString(distances_map));
		assertTrue(Arrays.deepEquals(distances_map,expected_result));
	}

}

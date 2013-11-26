package agent.coverage;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import lib.datastructs.Map;
import lib.datastructs.Path;
import lib.datastructs.Point;
import agent.Agent;
import agent.AgentStatus;
import agent.ShortestPathNaive;

public class GSAlgorithm implements CoverageAlgorithm  {


	Agent agent;
	@SuppressWarnings("unchecked")
	@Override
	public Path selectPath(Agent agent) {
		this.agent = agent;
		AgentStatus status = agent.getStatus();
		Map map = agent.getMap();
		Vector<Point> trajectory;

		Vector<Vector<Point>> vis = agent.getEnv().getVisibility(status.coordinates, agent.getEnv().options.Ds, map);
		Collections.sort(vis, greedyComparator);
		trajectory = vis.get(0);

		if (sum(vis.get(0)) != 0) {
			Vector<Point> trimmedTrajectory = new Vector<Point>();
			// This loop trims all the elements from the list after the first unvisited cell
			for (int i = 0; i <= trajectory.indexOf(trajectory.lastElement()); i++) {
				if (!agent.getSearchMap().isVisitedCell(trajectory.get(i).row, trajectory.get(i).col)) {
					for (int j = 0; j <= i; j++) {
						trimmedTrajectory.add(trajectory.get(j));
					}
					break;
				}
			}
			trajectory = trimmedTrajectory;
		} else {
			ShortestPathNaive sp = new ShortestPathNaive(agent);
			trajectory = sp.getCFS(status.coordinates, agent.getSearchMap());
		}

		Path result = agent.getMapBuilder().getPlanner().pathPlan(status, trajectory.lastElement());

		return result;
	}

	Comparator<Vector<Point>> greedyComparator = new Comparator<Vector<Point>>() {
		@Override
		public int compare(Vector<Point> o1, Vector<Point> o2) {
			if (sum(o1) < sum(o2))
				return 1;
			else if (sum(o1) > sum(o2) )
				return -1;
			else
				return 0;
		}
	};

	public Double sum(List<Point> list) {
		Double sum = 0.0;
		Iterator<Point> itr = list.iterator();
		while (itr.hasNext()) {
			Point point = itr.next();
			switch (agent.getEnv().options.collaborativeAlgorithm) {
			case ORIG:
				if (!agent.getSearchMap().isVisitedCell(point.row, point.col))
					sum = sum + 1;
				break;
			case SHARED_PLAN:
				if (!agent.getSearchMap().isVisitedCell(point.row, point.col) &&
						!agent.getSearchMap().isPlannedCell(point.row, point.col))
					sum = sum + 1;
				break;
			case GAUSS:
				GaussianWeight gaussian = new GaussianWeight(agent);
				Point destinationPoint = null;
				for (int i = 0; i <= list.indexOf(((Vector<Point>) list).lastElement()); i++) {
					if (!agent.getSearchMap().isVisitedCell(list.get(i).row, list.get(i).col))
						destinationPoint = new Point(list.get(i).row, list.get(i).col);
				}
				if (!agent.getSearchMap().isVisitedCell(point.row, point.col))
					sum = sum + 5.0 * gaussian.weightPoint(destinationPoint);
				break;
			default:
				break;
			}
		}
		return sum;
	}
}
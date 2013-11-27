package agent.coverage;

import java.util.Random;
import java.util.Vector;

import lib.Utils;
import lib.datastructs.Path;
import lib.datastructs.Point;
import agent.Agent;
import agent.AgentStatus;
import agent.MapBuilder;
import agent.SearchMap;
import agent.ShortestPathNaive;

public class CFSAlgorithm implements CoverageAlgorithm {

	Random rand = new Random(System.currentTimeMillis());

	public Path getPath(Agent agent) {

		MapBuilder mapInfo = agent.getMapBuilder();
		AgentStatus status = agent.getStatus();
		SearchMap map = agent.getSearchMap();

		ShortestPathNaive sp = new ShortestPathNaive(agent);
		Vector<Vector<Point>> trajs = sp.getCFSTrajectories(status.coordinates, map, agent.getEnv().options.numberAgents);
		Vector<Point> trajectory = choosePath(trajs, agent);

		//Vector<Point> trajectory = sp.getCFS(status.coordinates, map, agent.getEnv().options.numberAgents);

		Path result = mapInfo.getPlanner().pathPlan(status, trajectory.lastElement());
		//result.getPathCells().addAll(trajectory);
		//result.setActionsFromTrajectory(status);

		//System.out.println("selected("+row+","+col+")");

		//		if(agent.getEnv().options.debugMode) {
		//			agent.getEnv().maxs.clear();
		//			Vector<Integer> maxs = Utils.maxsOfList(collections.values);
		//			for (Integer i : maxs) {
		//				agent.getEnv().maxs.add(collections.points.get(i));
		//			}
		//		}

		return result;
	}

	private Vector<Point> choosePath(Vector<Vector<Point>> trajs, Agent agent) {
		CollaborativeAlgorithmEnum collaborate = agent.getEnv().options.collaborativeAlgorithm;
		Vector<Double> weights = new Vector<Double>();
		switch (collaborate) {
		case ORIG:

			break;
		case GAUSS:
			GaussianWeight gaussian = new GaussianWeight(agent);
			for (Vector<Point> traj : trajs) {
				Point destination = traj.lastElement();
				double weight = gaussian.weightPoint(destination) * traj.size()/(agent.getMap().getWidth()*agent.getMap().getHeight());
				weights.add(new Double(weight));
			}
			break;
		case FOLLOWERS_BRAKER:

			break;
		default:
			break;
		}
		int index = Utils.selectByMax(weights, 0);
		return trajs.get(index);
	}

	@Override
	public Path selectPath(Agent agent) {
		//		System.out.println("ID:"+agent.getID());
		//		int r = agent.getStatus().coordinates.row, c = agent.getStatus().coordinates.col;
		//		if(agent.getMap().isObstacle(r, c)) {
		//			r = r+1;
		//		}
		Vector<Path> paths = new Vector<Path>();
		Vector<Double> weights = new Vector<Double>();
		Path tempPath = null;
		for(int row = 0; row < agent.getMap().getHeight(); row++) {
			for(int col = 0; col < agent.getMap().getWidth(); col++) {
				Point p = new Point(row, col);
				if(agent.getMap().isFree(row, col) && !agent.isVisitedCell(p)) {

					Path path = agent.getMapBuilder().getPlanner().pathPlan(agent.getStatus(), p);
					if(!agent.getMap().isValidPath(path)) continue;

					tempPath = path;
					if (agent.getEnv().options.collaborativeAlgorithm == CollaborativeAlgorithmEnum.SHARED_PLAN &&
							agent.getSearchMap().isPlannedCell(row, col)) continue;

					double weight = weightPath(path.getPathCells(), agent);
					paths.add(path);
					weights.add(new Double(weight));
				}
			}
		}
		if(agent.getEnv().options.collaborativeAlgorithm == CollaborativeAlgorithmEnum.FOLLOWERS_BRAKER) {
			int region = 5;
			Vector<Agent> agents = agent.getAgentNeighborsWithinRange(region);
			Vector<Integer> toRemove = new Vector<Integer>();
			for (int i = 0; i < paths.size(); i++) {
				Point p = paths.get(i).getDestination();
				for (Agent neighbor : agents) {
					if(agent.getID() < neighbor.getID() ||
							agent.getMapBuilder().getPlanner().getDistanceInPoints(p,
									neighbor.getStatus().coordinates) > region)
						continue;
					toRemove.add(i);
					break;
				}
			}
			for(int i = 0; i < toRemove.size(); i++) {
				int index = toRemove.get(i)-i;
				paths.remove(index);
				weights.remove(index);
			}
		}

		if(paths.isEmpty())
			return tempPath;
		int selected = Utils.selectByMax(weights, 0);
		return paths.get(selected);
	}

	private double weightPath(Vector<Point> path, Agent agent) {
		double weight = 1.0 / path.size();
		CollaborativeAlgorithmEnum collaborate = agent.getEnv().options.collaborativeAlgorithm;
		switch (collaborate) {
		case GAUSS:
			weight = GaussianWeight.weightPath(agent, path);
			break;
			//		case FOLLOWERS_BRAKER:
			//			Vector<Point> agents = agent.getAgentNeighborsWithinRange(2);
			//			if(agents.size() > 0) {
			//				if(agent.getID() == 1)
			//					System.out.println("Neighbors:" + agents.size());
			//				//weight *= rand.nextInt(5);
			//				//				weight = GaussianWeight.weightPath(agent, path, agents.size()+1,
			//				//						//						new Point(agent.getEnv().getEnvHeight()/2,
			//				//						//								agent.getEnv().getEnvWidth()/2));
			//				//						agent.getStatus().coordinates);
			//			}
			//			break;
		default:
			break;
		}
		return weight;
	}


}

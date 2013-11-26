package agent.coverage;

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
		ShortestPathNaive sp = new ShortestPathNaive(agent);
		for(int row = 0; row < agent.getMap().getHeight(); row++) {
			for(int col = 0; col < agent.getMap().getWidth(); col++) {
				Point p = new Point(row, col);
				boolean validPath = true;
				boolean targetCondition;
				if (agent.getEnv().options.collaborativeAlgorithm == CollaborativeAlgorithmEnum.SHARED_PLAN)
					targetCondition = agent.getMap().isFree(row, col) && !agent.isVisitedCell(p) && !agent.getEnv().isDestinatedCell(p);
				else
					targetCondition = agent.getMap().isFree(row, col) && !agent.isVisitedCell(p);
				if(targetCondition) {
					Path path = agent.getMapBuilder().getPlanner().pathPlan(agent.getStatus(), p);
					//					System.out.println("From:"+agent.getStatus().coordinates);
					//					System.out.println("To:"+p);
					for(int i = 0; i < path.getPathCells().size(); i++) {
						p = path.getPathCells().get(i);
						if(!agent.getMap().isValidCell(p.row,  p.col) || !agent.getMap().isFree(p.row, p.col)) {
							validPath = false;
							break;
						}
					}
					if(!validPath) continue;
					double weight = weightPath(path.getPathCells(), agent);
					paths.add(path);
					weights.add(new Double(weight));
				}
			}
		}
		int selected = Utils.selectByMax(weights, 0);
		return paths.get(selected);
	}

	private double weightPath(Vector<Point> path, Agent agent) {
		double weight = 0;
		CollaborativeAlgorithmEnum collaborate = agent.getEnv().options.collaborativeAlgorithm;
		switch (collaborate) {
		case ORIG:
			weight = 1.0 / path.size();
			break;
		case GAUSS:
			GaussianWeight gaussian = new GaussianWeight(agent);
			Point destination = path.lastElement();
			weight =  path.size() ;/// (agent.getMap().getWidth()*agent.getMap().getHeight()); // normalize
			weight = 1.0 / weight;
			weight = gaussian.weightPoint(destination) * weight;
			break;
		case FOLLOWERS_BRAKER:

			break;
		default:
			break;
		}
		return weight;
	}
}

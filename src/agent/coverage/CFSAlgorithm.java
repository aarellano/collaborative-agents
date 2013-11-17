package agent.coverage;

import java.util.PriorityQueue;
import java.util.Vector;

import lib.Utils;
import lib.datastructs.Path;
import lib.datastructs.Point;
import lib.datastructs.PointValue;
import lib.datastructs.PointValueCollections;
import agent.Agent;
import agent.AgentStatus;
import agent.MapBuilder;
import agent.SearchMap;
import agent.ShortestPathNaive;

public class CFSAlgorithm implements CoverageAlgorithm {
	
	@Override
	public Path selectPath(Agent agent) {
		
		MapBuilder mapInfo = agent.getMapBuilder();
		AgentStatus status = agent.getStatus();
		SearchMap map = agent.getSearchMap();
		
		ShortestPathNaive sp = new ShortestPathNaive(agent);
		Vector<Point> trajectory = sp.getPath2NearestUnvisited(status.coordinates, map);
		
		Path result = mapInfo.getPlanner().pathPlan(status, status.coordinates);
		result.getPathCells().addAll(trajectory);
		result.setActionsFromTrajectory(status);
		
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
}

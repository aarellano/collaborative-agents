package agent.coverage;

import java.util.PriorityQueue;
import java.util.Vector;

import lib.Utils;
import lib.datastructs.Path;
import lib.datastructs.Point;
import lib.datastructs.PointValue;
import lib.datastructs.PointValueCollections;
import lib.datastructs.VisionDirectionEnum;
import agent.Agent;
import agent.AgentStatus;
import agent.MapBuilder;

public class OldSearchAlgorithm implements CoverageAlgorithm {

	@Override
	public Path selectPath(Agent agent) {
		
		MapBuilder mapInfo = agent.getMapBuilder();
		AgentStatus status = agent.getStatus();
		
		// Evaluate Cells
		/////////////////
		Vector<Point> cells = mapInfo.getMap().getCellsWithinDistance(
				status.coordinates, 1000, VisionDirectionEnum.ALL_D);
		PriorityQueue<PointValue> weights = agent.evaluateCells(cells);
		PointValueCollections collections = Utils.getPointAndValueCollections(weights);
//		if(agent.getEnv().options.LOG)
//			logWeightsDist(collections.values, collections.points);
		
		// Select Cell
		//////////////
		int selected = Utils.selectByMax(collections.values, 0);
		Point p = collections.points.get(selected);
		int row = p.row, col = p.col;
		
		// Plan Path to selected
		////////////////////////
		Path result = mapInfo.getPlanner().pathPlan(status, new Point(row, col));
		mapInfo.getPlanner().clearCache();
		//System.out.println("selected("+row+","+col+")");
		
		if(agent.getEnv().options.debugMode) {
			agent.getEnv().maxs.clear();
			Vector<Integer> maxs = Utils.maxsOfList(collections.values);
			for (Integer i : maxs) {
				agent.getEnv().maxs.add(collections.points.get(i));
			}
		}
		
		return result;

	}
	
	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Logs
	 * ///////////////////////////////////////////////////////////////////////////
	 */
	
//	public void logWeightsDist(Vector<Double> weights, Vector<Point> points)
//	{
//		FileLinesWriter writer = new FileLinesWriter(
//				Options.PATH_LOGS+"suggestion_dist.txt", false);
//		String row = "";
//		
//		//int count = 0;
//		for(int i = 0; i < mapBuilder.getMap().getHeight(); i++) {
//			row = "";
//			for(int j = 0; j < mapBuilder.getMap().getWidth(); j++) {
//				if(j != 0) row += ", ";
//				int index = points.indexOf(new Point(i,j));
//				if(index == -1) row += 0;
//				else row += ((int)(weights.get(index)*1000)/1000.0);
//				//row += ((int)(weights.get(count)*1000)/1000.0);
//				//count ++;
//			}
//			writer.writeLine(row);
//		}
//		writer.close();
//	}

}

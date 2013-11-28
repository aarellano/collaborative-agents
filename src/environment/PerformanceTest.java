package environment;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import lib.datastructs.Point;
import agent.Agent;

public class PerformanceTest {
	private Environment env;
	private int numberSteps;
	private int numberCells;
	private int numberRevisitedCells;

	public PerformanceTest(Environment environment){
		env = environment;
		computeTotalNumberSteps();
		computeTotalNumberRevisitedCells();
	}

	private void computeTotalNumberSteps(){
		numberSteps = 0;
		for (Agent agent : env.getSeekers()){
			numberSteps += agent.getTrajectory().size();
		}
	}

	public int getTotalNumberSteps() {
		return numberSteps;
	}

	private void computeTotalNumberRevisitedCells(){
		Vector<Point> everyStep = new Vector<Point> ();

		for (Agent agent : env.getSeekers()){
			everyStep.addAll(agent.getTrajectory());
		}

		Set<Point> visitedCells = new HashSet<Point>(everyStep);
		numberCells = visitedCells.size();
		numberRevisitedCells = (everyStep.size() - numberCells);
	}

	public int getTotalNumberRevisitedCells(){
		return numberRevisitedCells;
	}

	public int getNumberCells(){
		return numberCells;
	}
}

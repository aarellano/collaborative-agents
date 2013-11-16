package environment;

import agent.coverage.CoverageAlgorithmEnum;

public class Options {
	
	// Agent settings
	//================
	public int Ds = 1;	// Sensor range
	public boolean loadMap = false;	// Map is known/unknown
	public boolean startingPosManual = false;	// starting positions are manual in code, or loaded from the map
	public boolean fullCommunication = true;
	public CoverageAlgorithmEnum coverageAlgorithm = CoverageAlgorithmEnum.CFS;
	
	// Environment settings
	//======================
	public boolean terminateOnTimeout = false;	// terminate looping on max timeout steps
	
	public int testsCount = 1000;	// number of tests
	public int rePlayGameTimes = 1;	// number of reruns for the same test
	
	public boolean loadGame = false;	// load screen with a specific test
	public int loadGameNumber = 59;	// which test to load
	
	// Territory Division strategies
	public final static int NONE = 0;
	public final static int LEFT_RIGHT = 1;
	public final static int UP_DOWN = 2;
	public final static int DIAGONAL = 3;
	public int strategy = NONE;
	
	// View settings
	//===============
	public int sleepTime = 100;	// Sleep time between each time step
	
	public boolean updateView = true;	// update the screen each time step
	public boolean loopOnGames = true;
	public boolean viewMapKnowledge = true;	// draw map built by agents
	public boolean viewVisitedCells = false; // draw visited cells
	public boolean viewMapPartitions = false; // draw map partitions
	public boolean ViewTrajectories = true; // draw coverage trajectories

	public boolean debugMode = true;
	public boolean suspendGame = false;
	public boolean stepOverGame = false;
	public boolean terminateGame = false;
	
	// Log settings
	//==============
	public boolean LOG = true;	// log test results in text file
	public static String PATH_RES = "res/";
	public static String PATH_MAPS = PATH_RES+"maps/";
	public static String PATH_LOGS = PATH_RES+"logs/";
	
	public void setViewMapKnowledge() {
		viewNone();
		viewMapKnowledge = true;	
	}
	
	public void setViewVisitedCells() {
		viewNone();
		viewVisitedCells = true;
	}
	

	public void setViewMapPartitions() {
		viewNone();
		viewMapPartitions = true;
	}

	private void viewNone() {
		viewMapKnowledge = false;
		viewVisitedCells = false;
		viewMapPartitions = false;
	}

	public static String strategyName(int strategy) {
		String s = "";
		switch (strategy) {
		case Options.LEFT_RIGHT:
			s = "left-right";
			break;
		case Options.UP_DOWN:
			s = "up-down";
			break;
		case Options.DIAGONAL:
			s = "diagonal";
			break;
		default:
			break;
		}
		return s;
	}

}

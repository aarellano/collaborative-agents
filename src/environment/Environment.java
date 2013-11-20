package environment;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import lib.FileLinesWriter;
import lib.Utils;
import lib.datastructs.Map;
import lib.datastructs.OrientationEnum;
import lib.datastructs.Point;
import views.MainScreen;
import agent.Agent;
import agent.AgentStatus;

public class Environment {

	// Environment variables
	private Map map;	// Map information
	public Clock clock;	// Clock
	private boolean gameover;
	public Options options;

	// Agents settings in the environment
	private Agent[] agents;
	Point[] initialPositions;

	// View reference
	public MainScreen screen;
	public Hashtable<Point, String> tipInfo;
	public Vector<Point> maxs;

	public String testName = "t";
	String LOG_FILE, GAMES_LOG_FILE, MEETINGS_LOG_FILE, TRAJS_LOG_FILE;
	
	public void init() {
		options = new Options();
		
		tipInfo = new Hashtable<Point, String>();
		maxs = new Vector<Point>();

		clock = new Clock();
		agents = new Agent[initialPositions.length];
		for(int i = 0; i < agents.length; i++) {
			Point start = getStartingPoint(i);
			agents[i] = new Agent(i+1, 
					new AgentStatus(start, OrientationEnum.NORTH), this);			
		}
		
		// Give agents unlimited Vision!
		if(options.unlimitedVision) {
			options.Ds = Math.max(getEnvWidth(), getEnvHeight());
		}
	}
	
	public Environment(String mapID, int agentsCount) {
		map = Map.loadMapWithID(mapID);
		initialPositions = generateRandomPositions(agentsCount);
		init();
	}

	public Environment(String mapID, Point[] coordinates) {
		map = Map.loadMapWithID(mapID);
		initialPositions = coordinates;
		init();
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Initializers
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void resetExperiment() {

		/** Prepare Agents **/
		for(int i = 0; i < agents.length; i++) {
			if(options.loadMap || options.loadGame) {
				agents[i].loadMap(null);
			}
			//seekers[i].loadSearch("visited_2");
		}
	}
	
	public Point[] generateRandomPositions(int agentsCount) {
		Point[] positions = new Point[agentsCount];
		Random rand = new Random(System.currentTimeMillis());
		for(int i = 0 ; i < agentsCount; i++) {
			Point p = null;
			do {
				p = new Point(rand.nextInt(getEnvHeight()), 
						rand.nextInt(getEnvWidth()));
			} while(!map.isFree(p.row, p.col));
			positions[i] = p;
		}
		return positions;
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Game Body Actions
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void startGame() {

		//prepareLoggers();
		for (Agent seeker : agents) {
			seeker.prepareGame();
		}

		if(options.updateView) screen.redrawAll();

		// Place seekers in starting positions
		for (Agent seeker : agents) {
			seeker.observeMap();
		}

		// Start Game
		clock.setGameStartNow();
		gameover = false;

		// Game Loop
		while(!isGameOver()) {
			clock.incrementSteps();
			int turn = clock.getStepsCount()%agents.length;
			if(turn == 0) { 
				updateGameView();
				clock.incrementClock();
			}

			checkDebugStatus();

			agents[turn].takeATurn();
		}

		// Game Concluded
		System.out.println("Game took " + clock.getRelativeTimeInClocks() + " turns!");
		
		clock.incrementGamesCount();
		logGameResults(true);
		
		// Print summary each 50 games
		if(clock.getGamesCount() % 50 == 0) {
			System.out.println(clock.getGamesCount()+") Elapsed Time: " +
					clock.getRelativeTimeInSeconds()+" secs");
		}
	}

	public void endGame() {
		gameover = true;
		options.loopOnGames = false;
		options.suspendGame = false;
	}

	public void broadcast() {
		for (Agent seeker : agents) {
			//seeker.stopSeeking();
		}
		gameover = true;
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Auxiliaries
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public Vector<EnvCellEnum> readSensorsForCells(Vector<Point> cells) {
		Vector<EnvCellEnum> readings = new Vector<EnvCellEnum>();
		for (Point cell : cells) {
			readings.add(readSensorsForCell(cell));
		}
		return readings;
	}

	public EnvCellEnum readSensorsForCell(Point cell) {
		EnvCellEnum value = map.getCell(cell.row, cell.col);
		for (Agent seeker : agents) {
			AgentStatus status = seeker.getStatus();
			if(status != null && status.coordinates.equals(cell)) {
				value = EnvCellEnum.OCCUP_AGENT;
				break;
			}
		}
		return value;
	}

	public Vector<Point> sightCells(Point center, int distance) {
		Vector<Point> cells = new Vector<Point>();
		Vector<Point> buff = map.getCellsWithinDistance(center, distance, options.visionDirection);
		for (Point cell : buff) {
			if(!map.isSightLineBlocked(center, cell)) 
				cells.add(cell);
		}
		return cells;
	}

	private boolean isGameOver() {		
		return gameover || 
				(options.terminateOnTimeout && clock.getRelativeTimeInClocks() > 1000);
	}

	public int getCellSharedMaskForMap(Point p) {
		int mask = 0;
		for (int i = 0; i < agents.length; i++) {
			Agent seeker = agents[i];
			if(seeker.getMap().getCell(p.row, p.col).equals(map.getCell(p.row, p.col)))
				mask += Math.pow(2, i);
		}
		return mask;
	}

	public int getCellSharedMaskForSearch(Point p) {
		int mask = 0;
		for (int i = 0; i < agents.length; i++) {
			Agent seeker = agents[i];
			if(seeker.isScannedCell(p))
				mask += Math.pow(2, i);
		}
		return mask;
	}

	int maxMask = -1;
	public int getMaxSharedMask() {
		if (maxMask != -1) return maxMask;
		maxMask = 0;
		for(int i = 0; i < agents.length; i++)
			maxMask += Math.pow(2, i);
		return maxMask;
	}

	public boolean isDestinatedCell(Point p) {
		for (Agent seeker : agents) {
			Point dest = seeker.getNextDestination();
			if(dest != null && dest.equals(p)) return true;
		}
		return false;
	}

	private void checkDebugStatus() {
		if(options.debugMode && options.stepOverGame) {
			options.suspendGame = true;
			options.stepOverGame = false;
		}
		while(options.debugMode && options.suspendGame);
		if(options.debugMode && options.terminateGame) {
			options.terminateGame = false;
			gameover = true;
		}
	}

	public void loadTrajectories(int game) {
		for (Agent seeker : agents) {
			seeker.loadTrajectory(game);
		}
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Getters
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public Agent getSeekerAtCell(Point cell) {
		for (Agent seeker : agents) {
			if(seeker.getStatus().coordinates.equals(cell))
				return seeker;
		}
		return null;
	}

	private Point getStartingPoint(int index) {
		return options.startingPosManual ? initialPositions[index] : map.startingPoint;
	}

	public boolean needTerritories() {
		return options.strategy != Options.NONE && getSeekersCount() > 1;
	}

	public int getEnvWidth() {
		return map.getWidth();
	}

	public int getEnvHeight() {
		return map.getHeight();
	}

	public String getEnvID() {
		return map.id;
	}

	public Agent[] getSeekers() {
		return agents;
	}

	public int getSeekersCount() {
		return agents.length;
	}

	/**
	 * ///////////////////////////////////////////////////////////////////////////
	 * @category Loggers
	 * ///////////////////////////////////////////////////////////////////////////
	 */

	public void closeLoggers() {
		if(logger != null)
			logger.close();
		logger = null;
		if(loggerGames != null)
			loggerGames.close();
		loggerGames = null;
		if(loggerMeetings != null)
			loggerMeetings.close();
		loggerMeetings = null;
		if(loggerTrajectories != null)
			loggerTrajectories.close();
		loggerTrajectories = null;
	}

	private FileLinesWriter logger;
	private FileLinesWriter loggerGames;
	private FileLinesWriter loggerMeetings;
	private FileLinesWriter loggerTrajectories;

	private void logGameResults(boolean endGame) {
		if(!options.LOG) return;
		
		int width = map.getWidth(), height = map.getHeight();
		String line = "";
		int count;

		// Log Time
		line += clock.getRelativeTimeInClocks();

		// Log Map Building results
		count = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(getCellSharedMaskForMap(new Point(i, j)) > 0)
					count ++;
			}
		}
		line += ", " + count;

		// Log Search results
		count = 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(getCellSharedMaskForSearch(new Point(i, j)) == 0)
					count ++;
			}
		}
		line += ", " + count;

		if(endGame) {
			// Log Game results: Time/Map/Search
			GAMES_LOG_FILE = Options.PATH_LOGS+testName+"/games_log";
			if(loggerGames == null)
				loggerGames = new FileLinesWriter(GAMES_LOG_FILE+".txt", true);
			loggerGames.writeLine(line);

			// Log Meetings time stamps
			MEETINGS_LOG_FILE = Options.PATH_LOGS+testName+"/meetings";
			if(loggerMeetings == null)
				loggerMeetings = new FileLinesWriter(MEETINGS_LOG_FILE+".txt", true);
			loggerMeetings.writeLine(Utils.listToString(clock.getTimeStamps(), ", "));

			// Log Trajectories
			TRAJS_LOG_FILE = Options.PATH_LOGS+testName+"/trajs";
			if(loggerTrajectories == null)
				loggerTrajectories = new FileLinesWriter(TRAJS_LOG_FILE+".txt", true);
			String s = "";
			for(int i = 0; i < agents[0].getTrajectory().size(); i++) {
				s += "(";
				for(int j = 0; j < agents.length; j++) {
					s += agents[j].getTrajectory().get(i).row+","+agents[j].getTrajectory().get(i).col;
					if(j < agents.length-1) s += ",";
				}
				s += ")";
				if(i < agents[0].getTrajectory().size()-1) s += ", ";
			}
			loggerTrajectories.writeLine(s);

		} else if(options.LOG) {
			LOG_FILE = Options.PATH_LOGS+testName+"/log";
			if(logger == null)
				logger = new FileLinesWriter(LOG_FILE+
						"_"+clock.getGamesCount()+".txt", true);
			logger.writeLine(line);
		}
	}

	// Used for console view. 
	// Not needed anymore!
	public void updateGameView() {

		logGameResults(false);

		if(!options.updateView) 
			return;

		int width = map.getWidth(), height = map.getHeight();
		boolean logConsole = false;

		if(logConsole) {
			for(int j = 0; j < width+4; j++)
				System.out.print("=");

			for(int i = 0; i < height; i++) {
				System.out.print("\n||");
				for(int j = 0; j < width; j++) {
					Point cell = new Point(i, j);
					if(getSeekerAtCell(cell) != null)
						System.out.print("S");
					else if(map.getCell(i, j) == EnvCellEnum.FREE)
						System.out.print(" ");
					else
						System.out.print("|");
				}
				System.out.print("||");	
			}
			System.out.print("\n");
			for(int j = 0; j < width+4; j++)
				System.out.print("=");
			System.out.print("\n");
		}
		screen.redrawChanges();
		screen.updateView();

		try {
			Thread.sleep(options.sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

package environment;

import java.util.Vector;

public class Clock {

	int steps = -1;				// the total loop iterations done by all the seekers
	long clock = -1; 			// the number of clock ticks in the actual life, i.e. steps/#_of_seekers
	long gameStartTime = -1;	// the clock when the current game began
	double time;				// the actual time in millis
	int gamesCount = -1;

	Vector<Integer> timestamps;

	public Clock() {
		super();
		clock = gameStartTime = steps = 0;
		gamesCount = 0;
		timestamps = new Vector<Integer>();
	}

	public void incrementClock() {
		clock ++;
	}

	public void incrementSteps() {
		steps ++;
	}

	public void setGameStartNow() {
		gameStartTime = clock;
		steps = 0;
		time = System.currentTimeMillis();
		timestamps.clear();
	}

	public void incrementGamesCount() {
		gamesCount ++;
	}

	public int getGamesCount() {
		return gamesCount;
	}

	// get the total loop iterations done by all the seekers
	public int getStepsCount() {
		return steps;
	}

	public int getRelativeTimeInClocks() {
		return (int) (clock-gameStartTime);
	}

	public double getRelativeTimeInSeconds() {
		return (System.currentTimeMillis()-time)/1000.0;
	}

	public void addTimeStamp() {
		timestamps.add(getRelativeTimeInClocks());
	}

	public void addTimeStamp(int t) {
		timestamps.add(t);
	}

	public Vector<Integer> getTimeStamps() {
		return timestamps;
	}

	public void clearTimeStamps() {
		timestamps.clear();
	}

}

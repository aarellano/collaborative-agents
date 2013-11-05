package agent;

import java.util.Hashtable;
import java.util.Vector;

import environment.EnvCellEnum;

import lib.datastructs.Point;

public class Communicator {

	private Agent mySeeker;
	
	// TODO must add who knows what, in case of more than 2 seekers
	
	private Hashtable<Point, EnvCellEnum> localCellsInfo;
	
	private Vector<Point> localCellsVisited;
	//private Vector<Integer> sharedCounter;

	public Communicator(Agent mySeeker) {
		super();
		localCellsInfo = new Hashtable<Point, EnvCellEnum>();
		localCellsVisited = new Vector<Point>();
		this.mySeeker = mySeeker;
	}

	public void commitToSeeker(Agent seekerReceiver) {
		// 1. Map Info
		seekerReceiver.receiveMapInfo(localCellsInfo);
		synchronized (localCellsInfo) {
			localCellsInfo.clear();
		}
		
		// 2. Search Info
		seekerReceiver.receiveSearchInfo(localCellsVisited, mySeeker);
		synchronized (localCellsVisited) {
			localCellsVisited.clear();
		}
		
		// 3. Next Destination Info
		seekerReceiver.receiveNextDestination(mySeeker.getNextDestination());
		if(mySeeker.getNextDestination() != null && mySeeker.isVisitedCell(mySeeker.getNextDestination()))
			mySeeker.resetNextDestination();
	}

	public void addLocalMapInfo(Vector<Point> cells, Vector<EnvCellEnum> values) {
		synchronized (localCellsInfo) {
			for(int i = 0; i < cells.size(); i++)
				localCellsInfo.put(cells.get(i), values.get(i));
		}
	}
	
	public void addLocalSearchInfo(Vector<Point> cells) {
		synchronized (localCellsVisited) {
			for(int i = 0; i < cells.size(); i++)
				localCellsVisited.add(cells.get(i));
		}
	}

	public void clearLocalMapInfo() {
		synchronized (localCellsInfo) {
			localCellsInfo.clear();
		}
	}
	
	public void clearLocalSearchInfo() {
		synchronized (localCellsInfo) {
			localCellsVisited.clear();
		}
	}
}

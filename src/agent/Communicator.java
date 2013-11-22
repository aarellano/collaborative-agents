package agent;

import java.util.Hashtable;
import java.util.Vector;

import lib.datastructs.Point;
import environment.EnvCellEnum;

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

	public void commitToAgents(Agent[] receivers) {
		for (Agent receiver : receivers) {

			if(receiver == mySeeker) continue;

			// 1. Map Info
			receiver.receiveMapInfo(localCellsInfo);

			// 2. Search Info
			receiver.receiveSearchInfo(localCellsVisited, mySeeker);

			// 3. Next Destination Info
			//			receiver.receiveNextDestination(mySeeker.getNextDestination());
			//			if(mySeeker.getNextDestination() != null && mySeeker.isVisitedCell(mySeeker.getNextDestination()))
			//				mySeeker.resetNextDestination();
		}

		clearLocalMapInfo();
		clearLocalSearchInfo();
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

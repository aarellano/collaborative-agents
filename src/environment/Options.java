package environment;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lib.datastructs.VisionDirectionEnum;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import agent.coverage.CollaborativeAlgorithmEnum;
import agent.coverage.CoverageAlgorithmEnum;

public class Options {

	// Agent settings
	//================
	public int Ds = 1;	// Sensor range
	public boolean unlimitedVision = true;
	public VisionDirectionEnum visionDirection = VisionDirectionEnum.FOUR_D;

	public boolean loadMap = false;	// Map is known/unknown
	public String mapName = "rep";
	public boolean startingPosManual = true;	// starting positions are manual in code, or loaded from the map
	public boolean fullCommunication = true;
	public CoverageAlgorithmEnum coverageAlgorithm = CoverageAlgorithmEnum.CFS;
	public boolean takeRisk = false; // Compute nearest neighbors in map using unvisited or unknown
	public CollaborativeAlgorithmEnum collaborativeAlgorithm = CollaborativeAlgorithmEnum.GAUSS;
	public boolean collaborate = (collaborativeAlgorithm != CollaborativeAlgorithmEnum.ORIG);
	public int numberAgents = 5;
	public boolean useGUI = true;

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
	public boolean LOG_PERFORMANCE = false;

	public Options(String xmlFilePath){
		if (xmlFilePath != ""){
			setVariablesFromXml(xmlFilePath);
		}
	}

	private void setVariablesFromXml(String xmlFilePath){
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File(xmlFilePath));

			// normalize text representation
			doc.getDocumentElement ().normalize();

			NodeList settings = doc.getElementsByTagName("GeneralSettings");
			Node generalOptions = settings.item(0);
			String text = extractTextFromXml(generalOptions, "startingPosManual");
			startingPosManual = Boolean.parseBoolean(text);
			text = extractTextFromXml(generalOptions, "coverageAlgorithm");
			coverageAlgorithm = CoverageAlgorithmEnum.valueOf(text);
			text = extractTextFromXml(generalOptions, "collaborativeAlgorithm");
			collaborativeAlgorithm = CollaborativeAlgorithmEnum.valueOf(text);
			text = extractTextFromXml(generalOptions, "coverageAlgorithm");
			takeRisk = Boolean.parseBoolean(text);
			text = extractTextFromXml(generalOptions, "useGUI");
			useGUI = Boolean.parseBoolean(text);
			text = extractTextFromXml(generalOptions, "map");
			mapName = text;
			text = extractTextFromXml(generalOptions, "logPerformance");
			LOG_PERFORMANCE = Boolean.parseBoolean(text);
			text = extractTextFromXml(generalOptions, "numberAgents");
			numberAgents = Integer.parseInt(text);

		} catch (SAXParseException err) {
			System.out.println ("** Parsing error" + ", line "
					+ err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());
			System.exit(0);
		}catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();
			System.exit(0);
		}catch (Throwable t) {
			t.printStackTrace ();
			System.exit(0);
		}
	}

	private String extractTextFromXml(Node node, String tagName){
		Element element = (Element)node;
		NodeList list = element.getElementsByTagName(tagName);
		Element tagElement = (Element)list.item(0);
		NodeList textNode = tagElement.getChildNodes();
		return textNode.item(0).getNodeValue().trim();

	}

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

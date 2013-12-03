

import views.MainScreen;
import environment.Environment;
import environment.Options;

public class Main {


	public static void main(String args[]) {

		String optionsFileName = "";
		if (args.length == 1)
			optionsFileName = args[0];

		// Set test parameters
		Options options = new Options(optionsFileName);
		String mapName = options.mapName;
		//Point[] initialPos = new Point[] {new Point(0, 0)};
		//Point[] initialPos = new Point[] {new Point(0, 0), new Point(20, 20)};
		//Point[] initialPos = new Point[] {new Point(0, 0), new Point(0, 0), new Point(0, 0)};
		int strategy = Options.DIAGONAL;
		boolean useGUI = options.useGUI;
		int numberAgents = options.numberAgents;

		// Initialize the environment
		//Environment env = new Environment(mapName, initialPos, coverageAlgoz);
		Environment env = new Environment(mapName, numberAgents, options);
		env.options.strategy = strategy;
		env.testName = env.getEnvID()+"/"+
				"S"+env.getSeekersCount()+"/"+
				Options.strategyName(env.options.strategy);

		env.screen = new MainScreen();
		if(useGUI) {
			env.screen.view(env);
		} else {
			env.options.updateView = false;
			env.screen.env = env;
			env.screen.startGameThread();
			while(env.screen.isAlive());
		}
	}

}

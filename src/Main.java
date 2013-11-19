

import agent.coverage.CoverageAlgorithmEnum;
import views.MainScreen;
import lib.datastructs.Point;
import environment.Environment;
import environment.Options;

public class Main {


	public static void main(String args[]) {

		// Set test parameters
		String mapName = "rep";
		//Point[] initialPos = new Point[] {new Point(0, 0)};
		Point[] initialPos = new Point[] {new Point(0, 0), new Point(20, 20)};
		//Point[] initialPos = new Point[] {new Point(0, 0), new Point(0, 0), new Point(0, 0)};
		int strategy = Options.DIAGONAL;
		CoverageAlgorithmEnum coverageAlgoz = CoverageAlgorithmEnum.CFS;

		// Initialize the environment
		Environment env = new Environment(mapName, initialPos);
		env.options.strategy = strategy;
		env.options.coverageAlgorithm = coverageAlgoz;		
		env.testName = env.getEnvID()+"/"+
				"S"+env.getSeekersCount()+"/"+
				Options.strategyName(env.options.strategy);
		System.out.println(env.testName);
		
		env.screen = new MainScreen();
		env.screen.view(env);
	}

}

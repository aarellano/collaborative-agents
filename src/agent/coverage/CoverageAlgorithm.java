package agent.coverage;

import lib.datastructs.Path;
import agent.Agent;

public interface CoverageAlgorithm {

	public Path selectPath(Agent agent);
}

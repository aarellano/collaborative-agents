package agent.coverage;

import lib.datastructs.Path;
import agent.Agent;

public interface CoverageAlgorithm {

	public void setAgent(Agent agent);
	public Path selectPath();

}

/**
 * 
 */
package Agent.Role.ChargingRules;

import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.ExplorerRole;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * This should only run if the agent no longer needs to charge.
 * If alone, switch to Explorer.
 * 
 * @author Daniel
 */
public class RuleExploreIfAlone implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Check that no other agents are here.
		Location loc = sim.getAgentLocation(sim.getSelfID());
		List<AgentID> agents = sim.getAgentsAt(loc);
		return agents.size() == 1;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Might as well sleep one more turn as we switch to Explorer.
		return new SLEEP();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return new ExplorerRole(sim, com, base);
		}

	}

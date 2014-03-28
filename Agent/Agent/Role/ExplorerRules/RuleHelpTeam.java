/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.LinkedList;
import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Agent.Role.TeamRole;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Direction;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * If Team agent in range needs help, switch to team.
 * 
 * @author Daniel
 */
public class RuleHelpTeam implements Rule
	{
	private Path closestPath = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		LinkedList<AgentID> teamInRange = new LinkedList<AgentID>();
		LinkedList<AgentID> explorerInRange = new LinkedList<AgentID>();
		List<AgentID> teamAgents = sim.getTeammates(Role.ID.TEAM);
		List<AgentID> explorerAgents = sim.getTeammates(Role.ID.EXPLORER);
		
		//Get Team in pathing area.
		for (AgentID id : teamAgents)
			{
			PathOptions opt = new PathOptions(loc, sim.getAgentLocation(id));
			Path path = Pathfinder.getPath(sim, opt);
			
			//If no path, agent isn't reachable.
			if (path == null)
				continue;
			
			//Save the path to the closest team agent for later action.
			if (closestPath == null || path.getLength() < closestPath.getLength())
				closestPath = path;
			
			teamInRange.add(id);
			}
		
		//If team in range is even number, they should be paired. No help needed.
		if (teamInRange.size() % 2 == 0)
			return false;
		//There must be a Team agent in range that needs help.
		
		//Get explorers in range.
		for (AgentID id : explorerAgents)
			{
			PathOptions opt = new PathOptions(loc, sim.getAgentLocation(id));
			Path path = Pathfinder.getPath(sim, opt);
			
			if (path== null)
				continue;
			
			explorerInRange.add(id);
			}
		
		//Check that agent is lowest ID.
		int lowest = Integer.MAX_VALUE;
		for (AgentID id : explorerInRange)
			{
			if (id.getID() < lowest)
				lowest = id.getID();
			}
		if (lowest == sim.getSelfID().getID())
			return true;
		
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If path length is zero, already on cell of nearest team agent. Stay put.
		if (closestPath.getLength() == 0)
			return new MOVE(Direction.STAY_PUT);
		
		//Move towards the closest team agent.
		return new MOVE(Pathfinder.getDirection(sim.getAgentLocation(sim.getSelfID()), closestPath.getNext()));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return new TeamRole(sim, com, base);
		}

	}

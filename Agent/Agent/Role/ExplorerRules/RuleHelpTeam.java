/**
 * 
 */
package Agent.Role.ExplorerRules;

import java.util.LinkedList;
import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.State;
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
	private Location moveTo = null;
	private Location target = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		
		//Get Team in pathing area.
		int teamSearchInRange = 0;
		List<AgentID> teamAgents = sim.getTeammates(Role.ID.TEAM);
		for (AgentID id : teamAgents)
		{
			//Save agent searching count and the next loc to them.
			if ((sim.getAgentState(id) & State.TEAM_SEARCH.value()) > 0)
			{
				PathOptions opt = new PathOptions(loc, sim.getAgentLocation(id));
				opt.shortest = true;
				opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
				Path path = Pathfinder.getPath(sim, opt);
			
				//If no path, agent isn't reachable.
				if (path == null)
					continue;
			
				//Ensure agent does not include itself.
				if (id.equals(sim.getSelfID()))
					break;
				
				//Add to counter.
				teamSearchInRange++;
				target = sim.getAgentLocation(id);
				if (teamSearchInRange > 1)
					break;
				}
			}
		
		//If team searching in range is >1, some should pair up. Wait until there is 1.
		if (teamSearchInRange != 1)
			return false;
		
		//There must be a single Team agent in range that needs help.
		//Get explorers that can reach that Team agent.
		//Switch if I'm the closest explorer by path length.
		List<AgentID> explorerAgents = sim.getTeammates(Role.ID.EXPLORER);	
		AgentID closest = null;
		long distance = Long.MAX_VALUE;
		
		for (AgentID id : explorerAgents)
			{
			PathOptions opt = new PathOptions(sim.getAgentLocation(id), target);
			opt.shortest = true;
			opt.maxCost = sim.getAgentEnergy(id);
			Path path = Pathfinder.getPath(sim, opt);
			
			if (path== null)
				continue;
			
			if (path.getLength() < distance)
				{
				closest = id;
				distance = path.getLength();
				moveTo = path.getNext();
				}
			}
		
		if (sim.getSelfID().equals(closest))
			return true;
		
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Change to team search state.
    sim.addAgentState(sim.getSelfID(), State.TEAM_SEARCH);
		
		//If path length is zero, already on cell of nearest team agent. Stay put.
		if (moveTo == null)
			return new MOVE(Direction.STAY_PUT);
		
		//Move towards the team agent that needs help.
		return new MOVE(Pathfinder.getDirection(sim.getAgentLocation(sim.getSelfID()), moveTo));
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

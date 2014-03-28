package Agent.Role.ExplorerRules;

import java.util.LinkedList;
import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.TEAM_DIG;
import Ares.World.Cell;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * Simple rule for the agent to dig when the dig can be successful. This requires either
 * another agent on the same world cell, or for the rubble to only require 1 agent.
 * 
 * @author Daniel
 */
public class RuleCanDig implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location currentLoc = sim.getAgentLocation(sim.getSelfID());
		Cell currentCell = sim.getCell(currentLoc);
		WorldObject topLayer = currentCell.getTopLayer();
		int selfIDint = sim.getSelfID().getID();
		
		//If there are 2+ team agents on this cell, don't dig.
		List<AgentID> agentsHere = sim.getAgentsAt(currentLoc);
		int teamCount = 0;
		for (AgentID id : agentsHere)
			if (sim.getAgentRole(id) == Role.ID.TEAM)
				teamCount++;
		if (teamCount >= 2)
			return false;

		if (topLayer instanceof Rubble && sim.getPercentage(currentLoc) > 0)
			{
			Rubble rubble = (Rubble) topLayer;
			//Get list of Explorer agents on this tile.
			LinkedList<AgentID> explorersHere = new LinkedList<AgentID>();
			for (AgentID id : agentsHere)
				if (sim.getAgentRole(id) == Role.ID.EXPLORER)
					explorersHere.add(id);
			
			if (rubble.getRemoveAgents() == 1)
				{
				//Dig if the only explorer here.
				if (explorersHere.size() == 1)
					return true;
				
				//2+ explorers. Dig if the lowest ID explorer.
				int lowestID = Integer.MAX_VALUE;
				for (AgentID id : explorersHere)
					if (id.getID() < lowestID)
						lowestID = id.getID();
				if (selfIDint == lowestID)
					return true;
				}
			else if (explorersHere.size() >= 2)
				{
				//Must require two agents to do.
				//Dig if only one other explorer here.
				if (explorersHere.size() == 2)
					return true;
				
				//More than two explorers, dig if one of the lowest ids.
				int first = Integer.MAX_VALUE;
				int second = Integer.MAX_VALUE;
				for (AgentID id : explorersHere)
					{
					int test = id.getID();
					if (test < first)
						{
						second = first;
						first = test;
						}
					else if (test < second)
						second = test;
					}
				if (selfIDint == first || selfIDint == second)
					return true;
				}
			}

		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Dig conditions met, so dig.
		return new TEAM_DIG();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange()
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		//No role change needed.
		return null;
		}

	}

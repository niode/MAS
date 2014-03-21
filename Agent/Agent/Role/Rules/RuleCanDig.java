package Agent.Role.Rules;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
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
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location currentLoc = sim.getSelf().getLocation();
		Cell currentCell = sim.getCell(currentLoc);
		WorldObject topLayer = currentCell.getTopLayer();
		
		if (topLayer instanceof Rubble)
			{
			Rubble rubble = (Rubble)topLayer;
			
			if (rubble.getRemoveAgents() == 1)
				return true;
			
			if (currentCell.getCellInfo().getAgentIDList().size() > 1)
				return true;
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

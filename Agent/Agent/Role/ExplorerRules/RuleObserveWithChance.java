/**
 * 
 */
package Agent.Role.ExplorerRules;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.OBSERVE;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * Observe any rubble piles that still have a chance of holding survivors.
 * 
 * @author Daniel
 */
public class RuleObserveWithChance implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Does the world still have cells with >0 chance?
		for (int i = 0; i <= sim.getRowCount(); i++)
			for (int j = 0; j <= sim.getColCount(); j++)
				{
				int percent = sim.getPercentage(i, j);
				if (percent > 0 && percent < 100)
					return true;
				}
				
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Observe the cell with the highest % that is still <100.
		int highestPercent = 0;
		Location highestLocation = null; 
		for (int i = 0; i <= sim.getRowCount(); i++)
			for (int j = 0; j <= sim.getColCount(); j++)
				{
				int percent = sim.getPercentage(i, j);
				if (percent < 100 && percent > highestPercent)
					{
					highestPercent = percent;
					highestLocation = new Location(i, j);
					}
				}
		
		if (highestLocation != null)
			{
			return new OBSERVE(highestLocation);
			}
		
		return null;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}

	}

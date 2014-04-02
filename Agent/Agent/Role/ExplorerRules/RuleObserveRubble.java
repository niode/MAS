package Agent.Role.ExplorerRules;

import java.util.ArrayList;
import java.util.Random;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.OBSERVE;
import Ares.Commands.AgentCommands.SLEEP;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * Agent has run out of things to do. Start observing
 * rubble piles, starting with those that have a chance
 * of survivors.
 * 
 * @author Daniel
 */
public class RuleObserveRubble implements Rule
	{

	@Override
	public boolean checkConditions(Simulation sim)
		{
		//Does the world still have rubble?
		for (int i = 0; i <= sim.getRowCount(); i++)
			for (int j = 0; j <= sim.getColCount(); j++)
				{
				WorldObject top = sim.getTopLayer(i, j);
				if (top instanceof Rubble)
					return true;
				}
				
		return false;
		}

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
		
		//Start observing any rubble piles.
		ArrayList<Location> allRubble = new ArrayList<Location>();
		for (int i = 0; i < sim.getRowCount(); i++)
			for (int j = 0; j < sim.getColCount(); j++)
				{
				WorldObject top = sim.getTopLayer(i, j);
				if (top instanceof Rubble)
					allRubble.add(new Location(i, j));
				}
		
		if (allRubble.size() > 0)
			{
			Random rand = new Random();
			Location target = allRubble.get(rand.nextInt(allRubble.size()));
			return new OBSERVE(target);
			}
		
		return null;
		}

	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		// TODO Auto-generated method stub
		return null;
		}

	}

package Agent.Role;

import java.util.ArrayList;
import java.util.Random;
import Agent.*;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.*;
import Agent.Role.ExplorerRules.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.Commands.AgentCommands.*;
import Ares.World.Objects.Rubble;
import Ares.World.Objects.WorldObject;

/**
 * Basic role for exploration. The agent will not cooperate, but encountering situations that
 * require cooperation may result in a role change.
 * 
 * @author Daniel
 */
public class ExplorerRole extends Role
	{
	public static final String	CODE	= "ExplorerRole";

	/**
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public ExplorerRole(Simulation sim, Communicator com, BaseAgent base)
		{
		// These are still accessible from the superclass getter methods!
		super(sim, com, base);
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Role#setupRules(java.util.ArrayList)
	 */
	@Override
	public void setupRules(ArrayList<Rule> rules)
		{
		rules.add(new RulePlaceDigBeacon()); // No ares action.
		rules.add(new RuleSaveSurvivor()); // Save survivors over charging.
		rules.add(new RuleChargeRequired());
		rules.add(new RuleGoToNearSurv());
		rules.add(new RuleDigForSurv());
		rules.add(new RuleHelpTeam());
		rules.add(new RuleSwitchToTeam());
		rules.add(new RuleGoToNearSoloDig());
		rules.add(new RuleGoToUnknownPercent());
		rules.add(new RuleGoToUnvisited());
		rules.add(new RuleClearNearRubble());
		}

	@Override
	public void noActionUsed()
		{
		//Observe the cell with the highest % that is still <100.
		Simulation sim = getSimulation();
		
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
			getCommunicator().send(new OBSERVE(highestLocation));
			}
		else
			{
			//Start observing rubble piles.
			ArrayList<Location> allRubble = new ArrayList<Location>();
			for (int i = 0; i < sim.getRowCount(); i++)
				for (int j = 0; j < sim.getColCount(); j++)
					{
					WorldObject top = sim.getTopLayer(i, j);
					if (top instanceof Rubble)
						allRubble.add(new Location(i, j));
					}
			
			Random rand = new Random();
			Location target = allRubble.get(rand.nextInt(allRubble.size()));
			getCommunicator().send(new OBSERVE(target));
			}
		}

	public String toString()
		{
		return CODE;
		}

	}

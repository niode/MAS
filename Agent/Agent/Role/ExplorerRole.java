/**
 * 
 */
package Agent.Role;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import Agent.*;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.*;
import Agent.Role.ExplorerRules.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.World.Objects.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

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
    ExplorerState state = new ExplorerState();
		rules.add(new RulePlaceDigBeacon()); // No ares action.
		rules.add(new RuleSaveSurvivor()); // Save survivors over charging.
		rules.add(new RuleChargeRequired());
		rules.add(new RuleGoToNearSurv());
		rules.add(new RuleCanDig());
		rules.add(new RuleHelpTeam());
    rules.add(new GetTargetRule(state));
    rules.add(new SetTargetRule(state));
    rules.add(new ExplorerMoveRule(state));
		rules.add(new RuleGoToUnknownPercent());
		rules.add(new RuleGoToNearSoloDig());
		rules.add(new RuleSwitchToTeam());
		rules.add(new RuleGoToUnvisited());
    rules.add(new RuleDefault());
		}

	@Override
	public void noRuleMatch()
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
			//Not sure what to do if no places worth observing. Just move to random nearby cell.
			Location current = sim.getAgentLocation(sim.getSelfID());
			Object[] valid = Pathfinder.getValidNeighbors(sim, current).toArray();
			Random rand = new Random();
			Location target = (Location)valid[rand.nextInt(valid.length)];
			if (target.equals(current))
				getCommunicator().send(new SLEEP());
			else
				getCommunicator().send(new MOVE(Pathfinder.getDirection(current, target)));
			}
		}

	public String toString()
		{
		return CODE;
		}

	}

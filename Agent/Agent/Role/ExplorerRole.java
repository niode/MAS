/**
 * 
 */
package Agent.Role;

import java.util.ArrayList;
import Agent.*;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.*;
import Agent.Role.ExplorerRules.RuleCanDig;
import Agent.Role.ExplorerRules.RuleGoToUnknownPercent;
import Agent.Role.ExplorerRules.RuleGoToUnvisited;
import Agent.Role.ExplorerRules.RuleHelpTeam;
import Agent.Role.ExplorerRules.RulePlaceDigBeacon;
import Agent.Role.ExplorerRules.RuleSwitchToTeam;
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
		rules.add(new RulePlaceDigBeacon()); // No ares action.
		rules.add(new RuleSaveSurvivor()); // Save survivors over charging.
		rules.add(new RuleChargeRequired());
		rules.add(new RuleGoToNearSurv());
		rules.add(new RuleCanDig());
		rules.add(new RuleHelpTeam());
		rules.add(new RuleGoToUnknownPercent());
		rules.add(new RuleGoToNearSoloDig());
		rules.add(new RuleSwitchToTeam());
		rules.add(new RuleGoToUnvisited());
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
			//Not sure what to do if no places worth observing. Just sleep to conserve energy.
			getCommunicator().send(new SLEEP());
			}
		}

	public String toString()
		{
		return CODE;
		}

	}

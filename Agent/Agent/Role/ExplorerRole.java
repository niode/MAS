package Agent.Role;

import java.util.ArrayList;
import java.util.Random;
import Agent.*;
import Agent.Core.BaseAgent;
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
		rules.add(new RuleObserveWithChance());
		rules.add(new RuleGoToDigBeacon());
		rules.add(new RuleClearNearRubble());
		rules.add(new RuleObserveAnyRubble());
		}

	@Override
	public void noActionUsed()
		{
		getCommunicator().send(new SLEEP());
		}

	public String toString()
		{
		return CODE;
		}

	}

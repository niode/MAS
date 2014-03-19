package Agent.Role;

import java.util.ArrayList;
import java.util.Iterator;
import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Role.Rules.Rule;
import Ares.Commands.AgentCommand;

/**
 * Class for Roles. A Role is essentially an intelligence with a few extra methods enabling
 * switching between roles.
 * 
 * @author Daniel
 */
public abstract class Role extends Intelligence
	{
	private Role			nextRole	= null;
	private ArrayList<Rule>	rules		= new ArrayList<Rule>();

	/**
	 * Basic constructor for all roles.
	 * 
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public Role(Simulation sim, Communicator com, BaseAgent base)
		{
		super(sim, com, base);
		setupRules(rules);
		}

	/**
	 * Will be run on initialization. This method should fill the array list with rules ordered by
	 * priority.
	 * 
	 * @param rules rule list
	 */
	public abstract void setupRules(ArrayList<Rule> rules);

	/**
	 * Check if the role has determined it should change roles.
	 * 
	 * @return true if roles must be changed
	 */
	public boolean checkChangeRoles()
		{
		return !(nextRole == null);
		}

	/**
	 * Set a target role the agent should switch to.
	 * 
	 * @param role the new role the agent should change to, or null to clear.
	 */
	public void setNextRole(Role role)
		{
		nextRole = role;
		}

	/**
	 * Gets the next role that the agent should change to.
	 * 
	 * @return the next role, or null if not set.
	 */
	public Role getNextRole()
		{
		return nextRole;
		}

	/**
	 * Gets the rule list for the role.
	 * 
	 * @return rule list
	 */
	public ArrayList<Rule> getRuleList()
		{
		return rules;
		}

	/* (non-Javadoc)
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		boolean ruleUsed = false;
		Iterator<Rule> rules = getRuleList().iterator();
		while (rules.hasNext())
			{
			Rule nextRule = rules.next();
			
			if (nextRule.checkConditions(getSimulation()))
				{
				//Rule conditions met. Do rule actions.
				AgentCommand nextAction = nextRule.doAction(getCommunicator());
				getCommunicator().send(nextAction);
				setNextRole(nextRule.getRoleChange());
				ruleUsed = true;
				}
			}
		
		if (!ruleUsed)
			{
			noRuleMatch();
			}
		//If on the first move, stay put to get neighbor info.
		}
	
	/**
	 * Method that will be run if no existing rules had their conditions met.
	 */
	public abstract void noRuleMatch();
	
	}
package Agent.Role;

import java.util.ArrayList;
import java.util.Iterator;
import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Core.LogLevels;
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
	public static enum ID
		{
		UNKNOWN(0), OBSERVER(1), EXPLORER(2), TEAM(3), CHARGER(4);

		private int	id;

		ID(int id)
			{
			this.id = id;
			}

		public static ID getRoleID(String name)
			{
			switch (name)
				{
				case ObserverRole.CODE:
					return OBSERVER;
				case ExplorerRole.CODE:
					return EXPLORER;
				case ChargingRole.CODE:
					return CHARGER;
				case TeamRole.CODE:
					return TEAM;
				default:
					return UNKNOWN;
				}
			}
		}

	private Role				nextRole	= null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		boolean actionUsed = false;
		for (Rule nextRule : getRuleList())
			{

			if (nextRule.checkConditions(getSimulation()))
				{
				// Rule conditions met. Do rule actions.
				base.log(LogLevels.Always, "DOING RULE: " + nextRule.getClass().getSimpleName());
				AgentCommand nextAction = nextRule.doAction(getSimulation(), getCommunicator());

				setNextRole(nextRule.getRoleChange(getSimulation(), getCommunicator(), getBase()));

				// Go on to the next rule if this includes no actions. This allows agents
				// to match multiple rules.
				if (nextAction != null)
					{
					actionUsed = true;
					getCommunicator().send(nextAction);
					break;
					}
				}
			}

		if (!actionUsed)
			{
			base.log(LogLevels.Always, "NO ACTION USED");
			noActionMatch();
			}
		// If on the first move, stay put to get neighbor info.
		}

	/**
	 * Method that will be run if no existing rules had their conditions met.
	 */
	public abstract void noActionMatch();

	}

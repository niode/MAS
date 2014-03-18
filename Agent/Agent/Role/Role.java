package Agent.Role;

import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;

/**
 * Class for Roles. A Role is essentially an intelligence with a few extra methods enabling
 * switching between roles.
 * 
 * @author Daniel
 */
public abstract class Role extends Intelligence
	{
	private Role nextRole = null;

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
		}

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
	}

/**
 * 
 */
package Agent.Role;

import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;

/**
 * @author Daniel
 */
public class RoleIntelligence extends Intelligence
	{
	private Role currentRole = null;

	/**
	 * Main intelligence for a role based agent.
	 * 
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public RoleIntelligence(Simulation sim, Communicator com, BaseAgent base)
		{
		//Set up base intelligence.
		super(sim, com, base);
		
		//Set default role.
		currentRole = new ExplorerRole(sim, com, base);
		}

	/* (non-Javadoc)
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		//Get current role to make the next move.
		currentRole.think();
		
		//If agent wants to change roles, change.
		if (currentRole.checkChangeRoles())
			currentRole = currentRole.getNextRole();
		}

	}

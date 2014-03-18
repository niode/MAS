/**
 * 
 */
package Agent.Role;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;

/**
 * Basic role for exploration. The agent will not cooperate, but encountering situations that
 * require cooperation may result in a role change.
 * 
 * @author Daniel
 */
public class ExplorerRole extends Role
	{

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
	 * @see Agent.Intelligence#think()
	 */
	/* (non-Javadoc)
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		// TODO Auto-generated method stub

		}

	}

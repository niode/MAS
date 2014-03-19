package Agent.Role.Rules;

import Agent.Simulation;
import Agent.Role.Role;
import Ares.Commands.AgentCommand;

/**
 * Basic interface for rules used by a role based agent.
 * 
 * @author Daniel
 */
public interface Rule
	{

	/**
	 * Evaluates the current simulation state to see if the rule conditions are met.
	 * 
	 * @param sim the simulation used to check the rule
	 * @return true if the rule conditions are met, otherwise false.
	 */
	public boolean checkConditions(Simulation sim);

	/**
	 * Returns the action (as a command to be sent to the server) that the agent should take if the
	 * conditions are true.
	 * 
	 * @return the action
	 */
	public AgentCommand getAction();
	
	/**
	 * Returns the role the agent should change to if the rule evaluates to true.
	 * 
	 * @return the next agent role, or null if no role change is needed.
	 */
	public Role getRoleChange();
	
	}

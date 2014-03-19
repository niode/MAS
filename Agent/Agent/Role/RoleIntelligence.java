/**
 * 
 */
package Agent.Role;

import Agent.Brain;
import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Core.LogLevels;
import Ares.Direction;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;

/**
 * @author Daniel
 */
public class RoleIntelligence extends Intelligence
	{
	private Role currentRole = null;
	boolean onFirstMove = true;

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
		//Always stay put on first turn.
		if (onFirstMove)
			{
			onFirstMove = false;
			AgentCommand stay = new MOVE(Direction.STAY_PUT);
			getCommunicator().send(stay);
			return;
			}
		
		//Get current role to make the next move.
		currentRole.think();
		
		//If agent wants to change roles, change.
		if (currentRole.checkChangeRoles())
			currentRole = currentRole.getNextRole();
		}

	public static void main(String[] args)
		{
		if (args.length >= 2)
			{
			BaseAgent.setLogLevel(LogLevels.All);
			
			//Set up agent objects
			Simulation sim = new Simulation();
			Communicator com = new Communicator(BaseAgent.getBaseAgent(), sim);
			RoleIntelligence ai = new Agent.Role.RoleIntelligence(sim, com, BaseAgent.getBaseAgent());
			Brain brain = new Brain(BaseAgent.getBaseAgent(), ai, sim, com);

			//Start agent
			BaseAgent.getBaseAgent().start(args[0], args[1], brain);
			}
		else
			{
			System.out.println("Agent: usage java TestAgent <hostname> <groupname>");
			}

		}

	}

package Agent.Role;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import Agent.Brain;
import Agent.Communicator;
import Agent.Intelligence;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Core.LogLevels;
import Ares.*;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.*;

/**
 * @author Daniel
 */
public class RoleIntelligence extends Intelligence
	{
	private Role	currentRole	= null;

	/**
	 * Main intelligence for a role based agent.
	 * 
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 */
	public RoleIntelligence(Simulation sim, Communicator com, BaseAgent base)
		{
		// Set up base intelligence.
		super(sim, com, base);

		// Set default role.
		currentRole = new ExplorerRole(sim, com, base);
		}

	/**
	 * Role intelligence with using the given role as default. The role string should be the same as
	 * the name of the class for that role.
	 * 
	 * @param sim object representing agent world knowledge
	 * @param com object allowing for communication with server and other agents
	 * @param base object containing basic agent data
	 * @param role the role the intelligence should start with
	 */
	public RoleIntelligence(Simulation sim, Communicator com, BaseAgent base, String role)
		{
		super(sim, com, base);

		String roleString = "Agent.Role." + role;
		try
			{
			Class<?> roleClass = Class.forName(roleString);
			Constructor<?> roleConstructor = roleClass.getConstructor(sim.getClass(), com.getClass(),
					base.getClass());
			currentRole = (Role)roleConstructor.newInstance(sim, com, base);
			base.log(LogLevels.Always, "STARTING ROLE: "+role);
			}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e)
			{
			System.out.println("Agent: usage java TestAgent <hostname> <groupname> <rolename>");
			System.out.println("Agent: verify that correct role name was used");
			System.exit(0);
			}
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Intelligence#think()
	 */
	@Override
	public void think()
		{
		// Always stay put on first turn.
		if (getSimulation().getRound() == 0)
			{
			AgentCommand stay = new MOVE(Direction.STAY_PUT);
			getCommunicator().send(stay);
			return;
			}

		// Get current role to make the next move.
		currentRole.think();

		// If agent wants to change roles, change.
		if (currentRole.checkChangeRoles()) currentRole = currentRole.getNextRole();
		}

	public static void main(String[] args)
		{
		if (args.length >= 2)
			{
			BaseAgent.setLogLevel(LogLevels.All);

			// Set up agent objects
			Simulation sim = new Simulation();
			Communicator com = new Communicator(BaseAgent.getBaseAgent(), sim);
			
			// Set up intelligence
			RoleIntelligence ai;
			if (args.length == 2)
				ai = new RoleIntelligence(sim, com,BaseAgent.getBaseAgent());
			else
				ai = new RoleIntelligence(sim, com,BaseAgent.getBaseAgent(), args[2]);
				
			Brain brain = new Brain(BaseAgent.getBaseAgent(), ai, sim, com);

			// Start agent
			BaseAgent.getBaseAgent().start(args[0], args[1], brain);
			}
		else
			{
			System.out.println("Agent: usage java TestAgent <hostname> <groupname>");
			}

		}

	}

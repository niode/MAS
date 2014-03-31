package Agent.Role.Rules;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRole;
import Agent.Role.Role;
import Ares.Direction;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * Checks if the agent is in danger of having not enough energy to reach the nearest charging grid.
 * 
 * @author Daniel
 */
public class RuleChargeRequired implements Rule
	{
	/**
	 * Agents will go charge with enough energy to do this
	 * many extra moves while pathing to the charger.
	 */
	private static final int EXTRA_MOVES = 5;
	private Location currentLoc = null;
	private Path toNearestCharger = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		// Get current energy.
		int currentEnergy = sim.getSelf().getEnergyLevel();

		// Get energy required to move to nearest charger.
		currentLoc = sim.getAgentLocation(sim.getSelfID());
		PathOptions opt = new PathOptions(currentLoc);
		opt.cheapest = true;
		toNearestCharger = Pathfinder.getNearestCharger(sim, opt);

		// Return false if the agent can't reach a charger.
		if(toNearestCharger == null) return false;
		
		
		// Use the average cost of the path for calculating the buffer.
		long pathCost = toNearestCharger.getMoveCost();
		long average = pathCost / toNearestCharger.getLength();
		return currentEnergy <= pathCost + (average * EXTRA_MOVES);
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If already on a charger, sleep.
		if (toNearestCharger.getLength() == 0)
			return new SLEEP();
		
		//Move towards the nearest charger even before switching roles.
		Location towardsCharger = toNearestCharger.getNext();
		Direction dir = Pathfinder.getDirection(currentLoc, towardsCharger);
		return new MOVE(dir);
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#getRoleChange()
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return new ChargingRole(sim, com, base);
		}

	}

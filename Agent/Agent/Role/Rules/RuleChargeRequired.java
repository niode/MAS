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
	 * Charging is needed if the current agent energy drops below the energy cost to move to the
	 * nearest charger, plus energy limit. E.g. with an energyLimit of 50, if the nearest charger
	 * will take 75 energy to move to, the rule will say the agent should switch to ChargingRole if
	 * below 125 energy.
	 */
	private static final int	energyLimit	= 50;
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
		currentLoc = sim.getSelf().getLocation();
    PathOptions opt = new PathOptions(currentLoc);
    opt.shortest = true;
		toNearestCharger = Pathfinder.getNearestCharger(sim, opt);

    // Return false if the agent can't reach a charger.
    if(toNearestCharger == null) return false;

		long pathCost = toNearestCharger.getMoveCost();
		return currentEnergy < (pathCost + energyLimit);
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim)
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

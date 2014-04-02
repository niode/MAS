package Agent.Role.Rules;

import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.State;
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
	/**
	 * Agents will always charge if they reach this amount.
	 */
	private static final int ENERGY_MINIMUM = 25;
	/**
	 * The minimum value that will be used as average move cost.
	 */
	private static final int MOVE_MINIMUM = 5;
	Path toNearestCharger = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		// Get current energy.
		int currentEnergy = sim.getAgentEnergy(sim.getSelfID());

		// Get path to nearest charger.
		Location currentLoc = sim.getAgentLocation(sim.getSelfID());
		PathOptions opt = new PathOptions(currentLoc);
		opt.shortest = false;
		opt.maxCost = sim.getAgentEnergy(sim.getSelfID());
		toNearestCharger = Pathfinder.getNearestCharger(sim, opt);
		
		// Return false if the agent can't reach a charger.
		if (toNearestCharger == null) System.out.println("\tCAN'T REACH CHARGER"); //TODO
		if (toNearestCharger == null) return false;
		
		System.out.println("\tHAVE ENERGY: "+currentEnergy);//TODO
		System.out.println("\tMIN ENERGY: "+ENERGY_MINIMUM);
		
		// Go to nearest charger if below minimum.
		if (currentEnergy <= ENERGY_MINIMUM)
			return true;
		
		long average = 0;
		long pathCost = toNearestCharger.getMoveCost();
		System.out.println("\tCOST TO CHARGER: "+pathCost);//TODO
		if (toNearestCharger.getLength() > 0)
			{
			//Not on charger, use path to charger to get average.
			average = pathCost / toNearestCharger.getLength();
			System.out.println("\tCHARGER AT: "+toNearestCharger.getLast());//TODO
			}
		else
			{
			//On charger, calculate the average move cost in near area.
			int totalCost = 0, count = 0;
			Set<Location> near = Pathfinder.getValidNeighbors(sim, currentLoc,
					sim.getAgentEnergy(sim.getSelfID()));
			near.add(currentLoc);
			for (Location loc : near)
				if (sim.getMoveCost(loc) < currentEnergy) //Ignore kill cell.
					{
					count++;
					totalCost += sim.getMoveCost(loc);
					}
			average = totalCost / count;
			}
		
		//Ensure average meets minimum.
		if (average < MOVE_MINIMUM)
			average = MOVE_MINIMUM;
		
		return currentEnergy <= pathCost + (average * EXTRA_MOVES);
		//return currentEnergy <= ChargingRole.getRequiredEnergy(sim);
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//Clear team search state before charging.
		sim.removeAgentState(sim.getSelfID(), State.TEAM_SEARCH);
		
		//If already on a charger, sleep.
		if (toNearestCharger.getLength() == 0)
			return new SLEEP();
		
		//Move towards the nearest charger even before switching roles.
		Location currentLoc = sim.getAgentLocation(sim.getSelfID());
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

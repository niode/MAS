package Agent.Role.Rules;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.Role;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.World.Objects.Survivor;
import Ares.World.Objects.SurvivorGroup;
import Ares.World.Objects.WorldObject;
import Agent.Role.ChargingRole;

/**
 * If a survivor is in a neighboring top layer, go there.
 * 
 * @author Daniel
 */
public class RuleGoToNearSurv implements Rule
	{
	ArrayList<Location> survLocs = null;

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		getNearSurvivors(sim);
		
		return !survLocs.isEmpty();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		if (survLocs == null || survLocs.isEmpty())
			getNearSurvivors(sim);
		
		if (survLocs.isEmpty())
			return null;
		
		//Pick a random survivor location to move to.
		Random rand = new Random();
		Location target = survLocs.get(rand.nextInt(survLocs.size()));
		Location current = sim.getAgentLocation(sim.getSelfID());
		
		if (!ChargingRole.canStillCharge(sim, 
				sim.getEnergyRequired(target), current, sim.getSelfID()))
				return null;
				
		return new MOVE(Pathfinder.getDirection(current, target));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}
	
	/**
	 * Save locations of neighboring survivors.
	 * 
	 * @param sim
	 */
	private void getNearSurvivors(Simulation sim)
		{
		survLocs = new ArrayList<Location>();
		
		Set<Location> neighbors = Pathfinder.getValidNeighbors(sim,
        sim.getAgentLocation(sim.getSelfID()), sim.getAgentEnergy(sim.getSelfID()));

		if (neighbors == null)
			return; //No valid nearby.
		
		for (Location near : neighbors)
			{
			//Get location top layer.
			WorldObject top = sim.getCell(near).getTopLayer();
			if((top instanceof Survivor || top instanceof SurvivorGroup) && sim.getAgentsAt(near).size() == 0)
				survLocs.add(near);
			}
		}
	}

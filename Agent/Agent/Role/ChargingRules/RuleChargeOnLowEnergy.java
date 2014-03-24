/**
 * 
 */
package Agent.Role.ChargingRules;

import Agent.Communicator;
import Agent.Simulation;
import Agent.Core.BaseAgent;
import Agent.Pathfinder.Path;
import Agent.Pathfinder.PathOptions;
import Agent.Pathfinder.Pathfinder;
import Agent.Role.ChargingRole;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.MOVE;
import Ares.Commands.AgentCommands.SLEEP;

/**
 * If energy is below req, either sleep if on a charging grid, or move to the nearest charging grid.
 * 
 * @author Daniel
 */
public class RuleChargeOnLowEnergy implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		if (sim.getAgent(sim.getSelfID()).getEnergyLevel() < ChargingRole.REQUIRED_ENERGY)
			return true;
		
		return false;
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Simulation, Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		Location loc = sim.getAgentLocation(sim.getSelfID());
		
		//If on a charging cell. Sleep.
		if (sim.getCell(loc).isChargingCell())
			return new SLEEP();
		
		PathOptions opt = new PathOptions(loc);
		Path toCharge = Pathfinder.getNearestCharger(sim, opt);
		
		//If no accessible charger, do nothing so next rule can run.
		if (toCharge == null || toCharge.getLength() == 0)
			return null;
		
		//Go to nearest charging cell.
		return new MOVE(Pathfinder.getDirection(loc, toCharge.getNext()));
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange(Agent.Simulation, Agent.Communicator, Agent.Core.BaseAgent)
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		return null;
		}

	}

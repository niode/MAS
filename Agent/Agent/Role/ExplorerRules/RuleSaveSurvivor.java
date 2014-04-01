package Agent.Role.ExplorerRules;

import java.util.List;
import Agent.Communicator;
import Agent.Simulation;
import Agent.Pathfinder.*;
import Agent.Core.BaseAgent;
import Agent.Role.Role;
import Agent.Role.Rules.Rule;
import Ares.AgentID;
import Ares.Location;
import Ares.Commands.AgentCommand;
import Ares.Commands.AgentCommands.SAVE_SURV;
import Ares.World.Cell;
import Ares.World.Objects.Survivor;
import Ares.World.Objects.SurvivorGroup;
import Ares.World.Objects.WorldObject;

/**
 * Simple rule to save a survivor if currently on a cell where a survivor is
 * the top layer.
 * 
 * @author Daniel
 */
public class RuleSaveSurvivor implements Rule
	{

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#checkConditions(Agent.Simulation)
	 */
	@Override
	public boolean checkConditions(Simulation sim)
		{
		Location currentLoc = sim.getAgentLocation(sim.getSelfID());
		Cell currentCell = sim.getCell(currentLoc);
		WorldObject topLayer = currentCell.getTopLayer();

    int maxCost = sim.getAgentEnergy(sim.getSelfID()) - sim.getEnergyRequired(currentLoc) - 1;
    PathOptions opt = new PathOptions(currentLoc);
    opt.maxCost = maxCost;
    Path toCharger = Pathfinder.getNearestCharger(sim, opt);
    if(toCharger == null) return false;
		
		//If there are team agents on this cell or a lower id explorer, let them handle it.
		List<AgentID> agentsHere = sim.getAgentsAt(currentLoc);
		for (AgentID id : agentsHere)
			{
			if (sim.getAgentRole(id) == Role.ID.TEAM)
				return false;
			if (sim.getAgentRole(id) == Role.ID.EXPLORER &&
					id.getID() < sim.getSelfID().getID())
				return false;
			}

		return (topLayer instanceof Survivor || topLayer instanceof SurvivorGroup);
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#doAction(Agent.Communicator)
	 */
	@Override
	public AgentCommand doAction(Simulation sim, Communicator com)
		{
		//If there is a survivor to save, just save them.
		return new SAVE_SURV();
		}

	/* (non-Javadoc)
	 * @see Agent.Role.Rules.Rule#getRoleChange()
	 */
	@Override
	public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
		{
		//No role change needed.
		return null;
		}

	}

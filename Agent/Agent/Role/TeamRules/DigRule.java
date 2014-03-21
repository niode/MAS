package Agent.Role.TeamRules;

import Agent.*;
import Agent.Core.*;
import Agent.Pathfinder.*;
import Agent.Role.*;
import Agent.Role.Rules.*;
import Ares.*;
import Ares.Commands.*;
import Ares.Commands.AgentCommands.*;
import Ares.World.*;
import Ares.World.Objects.*;
import java.util.*;

public class DigRule implements Rule
{
  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getSelf().getLocation();

    Path charger = Pathfinder.getNearestCharger(sim, new PathOptions(loc));

    if(charger != null) energy -= charger.getMoveCost();
    int energyCost = sim.getEnergyRequired(sim.getSelf().getLocation());

    if(energy < energyCost) return false;

    // Check if there are other Team agents on this cell.
    int agentCount = 0;
    List<AgentID> agents = sim.getAgentsAt(loc);
    for(AgentID id : agents)
      if(sim.getAgentRole(id) == Role.ID.TEAM)
        agentCount++;
    if(agentCount >= 1) return true;

    if(sim.getTopLayer(loc) instanceof Survivor) return true;
    if(sim.getTopLayer(loc) instanceof SurvivorGroup) return true;

    return false;
  }

  public AgentCommand doAction(Simulation sim)
  {
    Location loc = sim.getSelf().getLocation();
    if(sim.getTopLayer(loc) instanceof Survivor) return new SAVE_SURV();
    if(sim.getTopLayer(loc) instanceof SurvivorGroup) return new SAVE_SURV();
    else return new TEAM_DIG();
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return new ExplorerRole(sim, com, base);
  }
}

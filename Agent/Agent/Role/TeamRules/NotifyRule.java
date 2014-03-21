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

public class NotifyRule implements Rule
{
  public boolean checkConditions(Simulation sim)
  {
    System.out.println("Evaluating NotifyRule.");
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getSelf().getLocation();

    if(!(sim.getTopLayer(loc) instanceof Rubble)) return false;

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
    if(agentCount <= 2) return true;
    return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    System.out.println("Doing NotifyRule.");
    Location loc = sim.getSelf().getLocation();

    com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));

    return new MOVE(Direction.STAY_PUT);
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

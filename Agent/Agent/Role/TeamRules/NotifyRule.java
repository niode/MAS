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
  private TeamFinder finder;
  public NotifyRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());

    if(!(sim.getTopLayer(loc) instanceof Rubble)) return false;

    if(sim.getAgentsRequired(loc) < 2) return false;
    if(sim.getPercentage(loc) == 0) return false;

    // Check if there are other Team agents on this cell.
    int agentCount = 0;
    List<AgentID> agents = sim.getAgentsAt(loc);
    for(AgentID id : agents)
      if(sim.getAgentRole(id) == Role.ID.TEAM)
        agentCount++;
    if(agentCount < 2) return true;
    return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    Location loc = sim.getAgentLocation(sim.getSelfID());

    com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));
    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

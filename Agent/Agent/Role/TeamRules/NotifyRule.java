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
    for(Location loc : sim.getVisited())
    {
      if(sim.getTopLayer(loc) instanceof Rubble &&
         sim.getAgentsRequired(loc) >= 2 &&
         sim.getPercentage(loc) > 0 &&
         sim.getAgentsAt(loc).size() < 0) return true;
    }

    Location loc = sim.getAgentLocation(sim.getSelfID());
    if(sim.getTopLayer(loc) instanceof Rubble &&
       sim.getAgentsRequired(loc) >= 2 &&
       sim.getPercentage(loc) > 0)
      return true;
    return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    for(Location loc : sim.getVisited())
    {
      if(sim.getTopLayer(loc) instanceof Rubble &&
         sim.getAgentsRequired(loc) >= 2 &&
         sim.getPercentage(loc) > 0 &&
         sim.getAgentsAt(loc).size() < 2)
        com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));
    }

    Location loc = sim.getAgentLocation(sim.getSelfID());
    if(sim.getTopLayer(loc) instanceof Rubble &&
       sim.getAgentsRequired(loc) >= 2 &&
       sim.getPercentage(loc) > 0)
      com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));

    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

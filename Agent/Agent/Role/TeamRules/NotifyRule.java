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
    //if(finder.getTeammate() == null) return false;

    int minID = sim.getSelfID().getID();
    for(AgentID id : sim.getTeammates(Role.ID.TEAM))
      if(id.getID() < minID) minID = id.getID();

    if(minID < sim.getSelfID().getID()) return false;
    else return true;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    for(Location loc : sim.getVisited())
    {
      boolean cond = sim.getTopLayer(loc) instanceof Rubble
      && sim.getAgentsRequired(loc) >= 2
      && sim.getPercentage(loc) > 0
      && sim.getAgentsAt(loc).size() < 2
      && !sim.isKiller(loc);

      if(!cond) continue;

      for(Beacon beacon : sim.getBeacons())
        if(beacon.getType() == Beacon.HELP_DIG
        || beacon.getType() == Beacon.TEAM_MOVE)
        {
          if(beacon.getLocation().equals(loc))
          {
            cond = false;
            break;
          }
        }

      if(cond)
      {
        com.send(new Beacon(Beacon.HELP_DIG, sim.getSelfID(), loc, Long.MAX_VALUE, 2));
      }
    }

    return null;
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

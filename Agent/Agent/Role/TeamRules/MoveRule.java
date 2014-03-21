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

public class MoveRule implements Rule
{
  Path path = null;

  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getSelf().getLocation();
    PathOptions opt = new PathOptions(loc);

    Path shortest = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      opt.end = beacon.getLocation();
      Path tmp = Pathfinder.getPath(sim, opt);
      if(shortest == null
          || (tmp.getLength() < shortest.getLength()
          && sim.getRound() + tmp.getLength() < beacon.getRound()))
        shortest = tmp;
    }
    if(shortest != null) return true;
    else return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    Location start = sim.getAgentLocation(sim.getSelfID());
    Location end = path.getNext();
    return new MOVE(Pathfinder.getDirection(start, end));
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

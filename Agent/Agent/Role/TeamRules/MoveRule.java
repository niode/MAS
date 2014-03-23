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

  private TeamFinder finder;
  public MoveRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    System.out.println("Evaluating MoveRule.");
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getSelf().getLocation();
    PathOptions opt = new PathOptions(loc);

    path = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      System.out.printf("Evaluating beacon at %s\n", beacon.getLocation().toString());
      
      // Don't consider your own location because it is the shortest path obviously.
      if(beacon.getLocation().equals(loc)) continue;

      opt.end = beacon.getLocation();
      Path tmp = Pathfinder.getPath(sim, opt);
      if(path == null
          || tmp != null && (tmp.getLength() < path.getLength()
          && sim.getRound() + tmp.getLength() < beacon.getRound()))
        path = tmp;
    }
    if(path != null) return true;
    else return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    System.out.println("Doing MoveRule.");
    Location start = sim.getAgentLocation(sim.getSelfID());
    Location end = path.getNext();
    return new MOVE(Pathfinder.getDirection(start, end));
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}

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

public class TeamMoveRule implements Rule
{
  Path path = null;

  private TeamFinder finder;
  public TeamMoveRule(TeamFinder finder)
  {
    super();
    this.finder = finder;
  }

  public boolean checkConditions(Simulation sim)
  {
    int energy = sim.getAgentEnergy(sim.getSelfID());
    Location loc = sim.getAgentLocation(sim.getSelfID());

    AgentID teammate = finder.getTeammate();
    if(teammate == null) return false;

    Location teamLoc = sim.getAgentLocation(teammate);
    
    PathOptions opt = new PathOptions(PathOptions.CHEAPEST & PathOptions.WITHIN_RANGE);

    long bestCost = 0;
    path = null;
    for(Beacon beacon : sim.getBeaconType(Beacon.HELP_DIG))
    {
      System.out.println("Evaluating beacon at " + beacon.getLocation());
      long pathCost = 0;
      opt.end = beacon.getLocation();

      opt.start = teamLoc;
      Path teamPath = Pathfinder.getPath(sim, opt);
      if(teamPath == null) continue;
      pathCost += teamPath.getMoveCost();

      opt.start = loc;
      Path selfPath = Pathfinder.getPath(sim, opt);
      if(selfPath == null) continue;
      pathCost += selfPath.getMoveCost();

      if(path == null || pathCost < bestCost)
      {
        bestCost = pathCost;
        path = selfPath;
      }
    }
    if(path != null) return true;
    else return false;
  }

  public AgentCommand doAction(Simulation sim, Communicator com)
  {
    System.out.println("Doing TeamMoveRule.");
    Location start = sim.getAgentLocation(sim.getSelfID());
    Location end = path.getLength() > 0 ? path.getNext() : start;
    return new MOVE(Pathfinder.getDirection(start, end));
  }

  public Role getRoleChange(Simulation sim, Communicator com, BaseAgent base)
  {
    return null;
  }
}
